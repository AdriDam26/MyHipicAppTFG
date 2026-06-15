package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.ProfesorDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Profesor.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y la capa
 * de acceso a datos (DAO), centralizando la lógica de negocio,
 * validaciones y operaciones ejecutadas en segundo plano.
 *
 * Además, garantiza que únicamente los usuarios registrados con
 * el tipo PROFESOR puedan disponer de un perfil de profesor asociado.
 */
public class ProfesorRepository {

    /**
     * DAO encargado de las operaciones sobre la entidad Profesor.
     */
    private final ProfesorDao profesorDao;
    /**
     * DAO utilizado para validar la existencia y tipo
     * del usuario asociado al profesor.
     */
    private final UsuarioDao usuarioDao;
    /**
     * Executor utilizado para ejecutar operaciones de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executorService;

    /**
     * LiveData que almacena el resultado de las operaciones realizadas.
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
     * Inicializa los DAO necesarios y el ejecutor de tareas.
     *
     * @param application Contexto de la aplicación.
     */
    public ProfesorRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        profesorDao = db.profesorDao();
        usuarioDao = db.usuarioDao();

        executorService = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta un nuevo profesor en la base de datos.
     *
     * Antes de realizar la inserción se verifican dos condiciones:
     * 1. Que exista un usuario asociado con el identificador indicado.
     * 2. Que dicho usuario tenga asignado el tipo PROFESOR.
     *
     * Estas validaciones garantizan la coherencia entre la tabla
     * Usuario y la tabla Profesor.
     *
     * @param profesor Profesor a insertar.
     */
    public void insertarProfesor(Profesor profesor) {

        executorService.execute(() -> {


            Usuario usuarioBase = usuarioDao.buscarPorIdSync(profesor.idProfesor);

            if (usuarioBase == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }


            if (!Usuario.TIPO_PROFESOR.equals(usuarioBase.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            try {
                profesorDao.insertarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza la información de un profesor existente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param profesor Profesor con los datos actualizados.
     */
    public void actualizarProfesor(Profesor profesor) {
        executorService.execute(() -> {
            try {
                profesorDao.actualizarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina un profesor de la base de datos.
     *
     * @param profesor Profesor a eliminar.
     */
    public void eliminarProfesor(Profesor profesor) {
        executorService.execute(() -> {
            try {
                profesorDao.eliminarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todos los profesores registrados.
     *
     * El resultado se devuelve mediante LiveData para que
     * la interfaz pueda reaccionar automáticamente ante
     * cualquier modificación de los datos.
     *
     * @return Lista observable de profesores.
     */
    public LiveData<List<Profesor>> obtenerTodosProfesores() {
        return profesorDao.obtenerTodosProfesores();
    }

    /**
     * Busca un profesor por su identificador.
     *
     * @param id Identificador del profesor.
     * @return Profesor encontrado.
     */
    public LiveData<Profesor> buscarPorId(int id) {
        return profesorDao.buscarPorId(id);
    }

    /**
     * Obtiene el número total de profesores registrados.
     *
     * @return Cantidad total de profesores.
     */
    public LiveData<Integer> contarProfesores() {
        return profesorDao.contarProfesores();
    }
}