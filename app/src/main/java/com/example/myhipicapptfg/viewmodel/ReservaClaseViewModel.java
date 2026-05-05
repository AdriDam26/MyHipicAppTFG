package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.ReservaClase;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.ReservaClaseRepository;

import java.util.List;
import java.util.Objects;

public class ReservaClaseViewModel extends AndroidViewModel {

    private final ReservaClaseRepository repository;

    // ── Disparadores ──────────────────────────────────────────────────────────
    private final MutableLiveData<Integer> idAlumnoTrigger   = new MutableLiveData<>();
    private final MutableLiveData<Long>    fechaSeleccionada = new MutableLiveData<>(System.currentTimeMillis());

    // ── Observables intermedios ───────────────────────────────────────────────
    private final LiveData<Alumno>      perfilAlumno;
    private final LiveData<List<Clase>> clasesReservadas;

    /**
     * CORRECCIÓN 1: Trigger combinado (Alumno + Fecha) como un único objeto.
     * Así switchMap solo se activa cuando AMBOS valores están listos,
     * evitando que se añadan múltiples fuentes simultáneas al mediador.
     */
    private final MediatorLiveData<Pair<Alumno, Long>> triggerCombinado = new MediatorLiveData<>();

    /**
     * CORRECCIÓN 1 (cont.): switchMap cancela automáticamente el LiveData
     * anterior antes de suscribirse al nuevo → cero duplicados.
     */
    private final LiveData<List<Clase>> clasesRecomendadas;

    public ReservaClaseViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservaClaseRepository(application);

        // 1. Perfil del alumno reactivo al ID
        perfilAlumno = Transformations.switchMap(idAlumnoTrigger, repository::getAlumnoById);

        // 2. Clases ya reservadas por el alumno
        clasesReservadas = Transformations.switchMap(idAlumnoTrigger, repository::getClasesReservadasPorAlumno);

        // 3. Construimos el trigger combinado: se actualiza cuando cambia
        //    el perfil O la fecha, pero solo emite si AMBOS tienen valor.
        triggerCombinado.addSource(perfilAlumno, alumno -> {
            Long fecha = fechaSeleccionada.getValue();
            if (alumno != null && fecha != null) {
                triggerCombinado.setValue(new Pair<>(alumno, fecha));
            }
        });

        triggerCombinado.addSource(fechaSeleccionada, fecha -> {
            Alumno alumno = perfilAlumno.getValue();
            if (alumno != null && fecha != null) {
                triggerCombinado.setValue(new Pair<>(alumno, fecha));
            }
        });

        // 4. switchMap: cada vez que el trigger cambia, Room devuelve un nuevo
        //    LiveData limpio. El anterior se desconecta solo. Sin fugas, sin duplicados.
        clasesRecomendadas = Transformations.switchMap(triggerCombinado, par ->
                repository.obtenerClasesPorPerfil(par.first, par.second)
        );
    }

    // ── Métodos de entrada ────────────────────────────────────────────────────

    public void cargarDatosAlumno(int idAlumno) {
        if (Objects.equals(idAlumnoTrigger.getValue(), idAlumno)) return;
        idAlumnoTrigger.setValue(idAlumno);
    }

    public void setFechaFiltro(long nuevaFecha) {
        if (Objects.equals(fechaSeleccionada.getValue(), nuevaFecha)) return;
        fechaSeleccionada.setValue(nuevaFecha);
    }

    /**
     * CORRECCIÓN 3: Resetear el estado tras consumirlo desde la Activity,
     * para que no se reemita el Snackbar al rotar la pantalla.
     */
    public void resetearEstado() {
        repository.resetearEstado();
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public LiveData<Alumno>      getPerfilAlumno()       { return perfilAlumno; }
    public LiveData<List<Clase>> getClasesReservadas()   { return clasesReservadas; }
    public LiveData<List<Clase>> getClasesRecomendadas() { return clasesRecomendadas; }
    public LiveData<String>      getEstadoOperacion()    { return repository.getEstadoOperacion(); }

    // ── Operaciones de negocio ────────────────────────────────────────────────

    public void reservarClase(int idAlumno, int idClase) {
        ReservaClase reserva = new ReservaClase();
        reserva.idAlumno     = idAlumno;
        reserva.idClase      = idClase;
        reserva.fechaReserva = System.currentTimeMillis();
        repository.insertarReserva(reserva);
    }

    public void cancelarReserva(int idAlumno, int idClase) {
        repository.cancelarReserva(idAlumno, idClase);
    }

    public void forzarActualizacion() {
        Alumno alumno = perfilAlumno.getValue();
        Long fecha = fechaSeleccionada.getValue();
        if (alumno != null && fecha != null) {
            // Reasignar el mismo par fuerza a switchMap a re-ejecutar la query
            triggerCombinado.setValue(new Pair<>(alumno, fecha));
        }
    }

    public LiveData<List<Usuario>> getProfesores() {
        return repository.obtenerProfesoresUsuarios();
    }

    public LiveData<List<Pista>> getPistas() {
        return repository.obtenerTodasPistas();
    }

    public LiveData<List<ReservaClase>> getReservas() {
        return repository.obtenerTodasLasReservas();
    }
}