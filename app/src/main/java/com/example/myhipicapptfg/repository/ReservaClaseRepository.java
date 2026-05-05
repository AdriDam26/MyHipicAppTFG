package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.AlumnoDao;
import com.example.myhipicapptfg.dao.PistaDao;
import com.example.myhipicapptfg.dao.ReservaClaseDao;
import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.ReservaClase;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaClaseRepository {

    private final ReservaClaseDao reservaClaseDao;

    private final AlumnoDao alumnoDao;
    private final ExecutorService executorService;

    private final UsuarioDao usuarioDao; // Asegúrate de tener este DAO
    private final PistaDao pistaDao;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ReservaClaseRepository(@NonNull Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        reservaClaseDao = db.reservaClaseDao();
        alumnoDao = db.alumnoDao();
        usuarioDao = db.usuarioDao(); // Inicializar
        pistaDao = db.pistaDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // ── Insertar reserva con validaciones ─────────────────────────────────────
    public void insertarReserva(ReservaClase reserva) {
        executorService.execute(() -> {
            if (!reservaClaseDao.esAlumnoValido(reserva.idAlumno)) {
                estadoOperacion.postValue("ERROR_ALUMNO_NO_VALIDO");
                return;
            }
            if (!reservaClaseDao.existeClase(reserva.idClase)) {
                estadoOperacion.postValue("ERROR_CLASE_NO_EXISTE");
                return;
            }
            if (reservaClaseDao.yaEstaReservado(reserva.idAlumno, reserva.idClase)) {
                estadoOperacion.postValue("ERROR_YA_RESERVADO");
                return;
            }
            int alumnosActuales = reservaClaseDao.contarAlumnosEnClase(reserva.idClase);
            if (alumnosActuales >= 10) {
                estadoOperacion.postValue("ERROR_CLASE_LLENA");
                return;
            }
            try {
                reservaClaseDao.insertarReserva(reserva);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    public LiveData<Alumno> getAlumnoById(int idAlumno) {
        return alumnoDao.buscarPorId(idAlumno);
    }

    public LiveData<List<Clase>> obtenerClasesPorPerfil(Alumno alumno, long fechaSeleccionada) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fechaSeleccionada);

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        long finDia = cal.getTimeInMillis();

        return reservaClaseDao.obtenerClases(
                alumno.nivelDoma,
                alumno.practicaDoma ? 1 : 0,
                alumno.nivelSalto,
                alumno.practicaSalto ? 1 : 0,
                inicioDia,
                finDia
        );
    }

    // ── Cancelar reserva ──────────────────────────────────────────────────────
    public void cancelarReserva(int idAlumno, int idClase) {
        executorService.execute(() -> {
            try {
                reservaClaseDao.cancelarReserva(idAlumno, idClase);
                estadoOperacion.postValue("CANCELADA");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // ── Queries LiveData ──────────────────────────────────────────────────────


    public void resetearEstado() {
        estadoOperacion.postValue(null);
    }

    public LiveData<List<Clase>> getClasesReservadasPorAlumno(int idAlumno) {
        return reservaClaseDao.obtenerClasesReservadasPorAlumno(idAlumno);
    }

    public LiveData<List<Usuario>> getAlumnosDeClase(int idClase) {
        return reservaClaseDao.obtenerAlumnosDeClase(idClase);
    }

    public LiveData<List<Usuario>> obtenerProfesoresUsuarios() {
        return usuarioDao.obtenerUsuariosPorTipo(Usuario.TIPO_PROFESOR);
    }

    /**
     * Obtiene todas las pistas para traducir el ID_Pista a un nombre real.
     */
    public LiveData<List<Pista>> obtenerTodasPistas() {
        return pistaDao.obtenerTodasPistas();
    }

    /**
     * Obtiene todas las reservas actuales para que el Adapter pueda contar cuántos
     * alumnos hay en cada clase mediante el idClase.
     */
    public LiveData<List<ReservaClase>> obtenerTodasLasReservas() {
        return reservaClaseDao.obtenerTodasLasReservas();
    }
}