package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.repositorios.ReservaClaseRepository;
import com.example.myhipicapptfg.model.ClaseUIModel;

import java.util.List;
import java.util.Objects;

public class ReservaClaseViewModel extends AndroidViewModel {

    private final ReservaClaseRepository repository;

    // ── Disparadores ───────────────────────────────────────────────
    private final MutableLiveData<Integer> idAlumnoTrigger   = new MutableLiveData<>();
    private final MutableLiveData<Long>    fechaSeleccionada =
            new MutableLiveData<>(System.currentTimeMillis());

    // ── Perfil reactivo ────────────────────────────────────────────
    private final LiveData<Alumno> perfilAlumno;

    // ── Trigger combinado (Alumno + Fecha) ─────────────────────────
    private final MediatorLiveData<Pair<Alumno, Long>> triggerCombinado =
            new MediatorLiveData<>();

    public ReservaClaseViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservaClaseRepository(application);

        // 1️⃣ Perfil del alumno reactivo al ID
        perfilAlumno = Transformations.switchMap(
                idAlumnoTrigger,
                repository::getAlumnoById
        );

        // 2️⃣ Trigger combinado seguro (evita duplicados)
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
    }

    // ───────────────────────────────────────────────────────────────
    // MÉTODOS DE ENTRADA
    // ───────────────────────────────────────────────────────────────

    public void cargarDatosAlumno(int idAlumno) {
        if (Objects.equals(idAlumnoTrigger.getValue(), idAlumno)) return;
        idAlumnoTrigger.setValue(idAlumno);
    }

    public void setFechaFiltro(long nuevaFecha) {
        if (Objects.equals(fechaSeleccionada.getValue(), nuevaFecha)) return;
        fechaSeleccionada.setValue(nuevaFecha);
    }

    public void resetearEstado() {
        repository.resetearEstado();
    }

    // ───────────────────────────────────────────────────────────────
    // GETTERS
    // ───────────────────────────────────────────────────────────────

    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }

    /**
     * Clases disponibles según:
     * - Nivel del alumno
     * - Ficha
     * - Disciplina
     * - Fecha seleccionada
     */
    public LiveData<List<ClaseUIModel>> getClasesUI() {
        return Transformations.switchMap(triggerCombinado, par ->
                repository.obtenerClasesUI(par.first, par.second)
        );
    }

    /**
     * Clases reservadas por el alumno
     */
    public LiveData<List<ClaseUIModel>> getClasesReservadasUI() {
        return Transformations.switchMap(
                idAlumnoTrigger,
                repository::obtenerClasesReservadasUI
        );
    }

    // ───────────────────────────────────────────────────────────────
    // OPERACIONES DE NEGOCIO
    // ───────────────────────────────────────────────────────────────

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
}