package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.AlumnoDao;
import com.example.myhipicapptfg.datos.local.dao.ReservaClaseDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.model.ClaseModel;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de centralizar el acceso a datos relacionados
 * con las reservas de clases.
 *
 * Actúa como capa intermedia entre ViewModel y DAO, aplicando:
 * - Lógica de negocio
 * - Validaciones
 * - Comunicación del estado de operaciones
 */
public class ReservaClaseRepository {

    /**
     * DAO para manipular la base de datos
     */
    private final ReservaClaseDao reservaClaseDao;


    /**
     * Executor para ejecutar operaciones de base de datos en segundo plano,
     * evitando bloquear el hilo principal (UI Thread).
     */
    private final ExecutorService executorService;

    /**
     * LiveData que comunica el estado de las operaciones al ViewModel/UI.
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa la base de datos y obtiene los DAO necesarios.
     */
    public ReservaClaseRepository(@NonNull Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        reservaClaseDao = db.reservaClaseDao();
        executorService = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado actual de la última operación realizada.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta una nueva reserva aplicando validaciones:
     * - El alumno existe y es válido
     * - La clase existe
     * - No existe una reserva previa
     * - La clase no está llena
     *
     * La operación se ejecuta en segundo plano.
     */
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
            if (alumnosActuales >= ClaseModel.MAXIMO) {
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



    /**
     * Elimina una reserva existente.
     * Se ejecuta en background thread.
     */
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


    /**
     * Reinicia el estado de operación.
     */
    public void resetearEstado() {
        estadoOperacion.postValue(null);
    }

    /**
     * Obtiene los alumnos inscritos en una clase.
     */
    public LiveData<List<Usuario>> obtenerAlumnosDeClase(int idClase) {
        return reservaClaseDao.obtenerAlumnosDeClase(idClase);
    }

    /**
     * Obtiene las clases disponibles para un alumno en una fecha concreta.
     *
     * Se calculan los límites del día (inicio y fin) para filtrar correctamente
     * las clases del mismo día.
     *
     * También se incluyen condiciones según:
     * - Nivel del alumno
     * - Disciplinas que practica
     */
    public LiveData<List<ClaseModel>> obtenerClasesPorAlumnoYFecha(int idAlumno, long fecha) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(fecha);
        cal.set(Calendar.HOUR_OF_DAY, 0);  cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);       cal.set(Calendar.MILLISECOND, 0);
        long inicioDia = cal.getTimeInMillis();

        cal.set(Calendar.HOUR_OF_DAY, 23); cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);      cal.set(Calendar.MILLISECOND, 999);
        long finDia = cal.getTimeInMillis();

        return reservaClaseDao.obtenerClasesPorAlumnoYFecha(idAlumno, inicioDia, finDia);
    }

    /**
     * Obtiene las clases en las que un alumno está inscrito.
     */
    public LiveData<List<ClaseModel>> obtenerClasesReservadas(int idAlumno) {
        return reservaClaseDao.obtenerClasesReservadas(idAlumno);
    }


}