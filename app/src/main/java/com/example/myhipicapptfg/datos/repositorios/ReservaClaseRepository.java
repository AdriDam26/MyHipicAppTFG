package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.AlumnoDao;
import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.dao.ReservaClaseDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;

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
        executorService = AppDatabase.getDatabaseExecutor();
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
        android.util.Log.d("CADENA", "REPO.getAlumnoById llamado con id=" + idAlumno);
        AppDatabase.getDatabaseExecutor().execute(() -> {
            int totalAlumnos = alumnoDao.contarAlumnos();
            Alumno a = alumnoDao.buscarPorIdSync(1); // añade método sync al DAO
            android.util.Log.d("CADENA", "total=" + totalAlumnos +
                    " buscarPorId(1)=" + (a == null ? "NULL" : "encontrado ID=" + a.idAlumno));

            List<Alumno> todos = alumnoDao.obtenerTodosSync();
            for (Alumno alumno : todos) {
                android.util.Log.d("CADENA", "Alumno existente: ID=" + alumno.idAlumno);
            }
        });
        return alumnoDao.buscarPorId(idAlumno);
    }

    public LiveData<List<Clase>> obtenerClasesPorPerfil(Alumno alumno, long fechaSeleccionada) {
        android.util.Log.d("CADENA", "REPO.obtenerClasesPorPerfil: alumnoID=" + alumno.idAlumno +
                " nivelDoma=" + alumno.nivelDoma + " practicaDoma=" + alumno.practicaDoma +
                " nivelSalto=" + alumno.nivelSalto + " practicaSalto=" + alumno.practicaSalto +
                " fecha=" + fechaSeleccionada);
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

    public LiveData<List<Usuario>> obtenerAlumnosDeClase(int idClase) {
        return reservaClaseDao.obtenerAlumnosDeClase(idClase);
    }


}