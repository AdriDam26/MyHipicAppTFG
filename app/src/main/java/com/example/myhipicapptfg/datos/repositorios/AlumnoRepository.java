package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.AlumnoDao;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar todas las operaciones
 * relacionadas con la entidad Alumno.
 *
 */
public class AlumnoRepository {

    /**
     * DAO encargado de las operaciones CRUD sobre la entidad Alumno.
     */
    private final AlumnoDao alumnoDao;

    /**
     * DAO utilizado para validar la existencia y el tipo de usuario
     * asociado a cada alumno.
     */
    private final UsuarioDao usuarioDao;

    /**
     * Executor encargado de ejecutar operaciones de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executorService;


    /**
     * LiveData utilizado para comunicar a la interfaz el resultado
     * de las operaciones realizadas.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_USUARIO_NO_EXISTE
     * - ERROR_TIPO_USUARIO_INVALIDO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa los DAO necesarios para la gestión de alumnos,
     * usuarios, participaciones y calificaciones.
     *
     * También obtiene la instancia compartida del ExecutorService
     * utilizada para ejecutar operaciones en segundo plano.
     *
     * @param application Contexto global de la aplicación.
     */
    public AlumnoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        alumnoDao = db.alumnoDao();
        usuarioDao = db.usuarioDao();



        executorService = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * Permite que los ViewModel y la interfaz reaccionen ante
     * operaciones exitosas o errores producidos durante el acceso
     * a la base de datos.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta un nuevo alumno en la base de datos.
     *
     * Antes de realizar la inserción se ejecutan varias validaciones:
     *
     * 1. Verificar que existe un usuario asociado.
     * 2. Comprobar que dicho usuario tiene el rol ALUMNO.
     *
     * Si alguna validación falla, la operación se cancela y se
     * informa del error correspondiente mediante estadoOperacion.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param alumno Alumno que se desea registrar.
     */
    public void insertarAlumno(Alumno alumno) {

        executorService.execute(() -> {

            // Verificar que existe el Usuario
            Usuario usuarioExistente = usuarioDao.buscarPorIdSync(alumno.idAlumno);

            if (usuarioExistente == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }

            // Verificar que el tipo es ALUMNO
            if (!Usuario.TIPO_ALUMNO.equals(usuarioExistente.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            try {
                alumnoDao.insertarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza la información de un alumno existente.
     *
     * La operación se ejecuta de forma asíncrona para evitar
     * bloquear el hilo principal de la aplicación.
     *
     * @param alumno Alumno con los datos actualizados.
     */
    public void actualizarAlumno(Alumno alumno) {
        executorService.execute(() -> {
            try {
                alumnoDao.actualizarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todos los alumnos registrados en el sistema.
     *
     * El resultado se devuelve mediante LiveData para que
     * cualquier modificación en la base de datos se refleje
     * automáticamente en la interfaz de usuario.
     *
     * @return Lista observable de alumnos.
     */
    public void eliminarAlumno(Alumno alumno) {
        executorService.execute(() -> {
            try {
                alumnoDao.eliminarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todos los alumnos registrados en el sistema.
     *
     * El resultado se devuelve mediante LiveData para que
     * cualquier modificación en la base de datos se refleje
     * automáticamente en la interfaz de usuario.
     *
     * @return Lista observable de alumnos.
     */
    public LiveData<List<Alumno>> obtenerTodosAlumnos() {
        return alumnoDao.obtenerTodosAlumnos();
    }

    /**
     * Busca un alumno a partir de su identificador.
     *
     * @param id Identificador del alumno.
     * @return Alumno correspondiente al identificador indicado.
     */
    public LiveData<Alumno> buscarPorId(int id) {
        return alumnoDao.buscarPorId(id);
    }


}