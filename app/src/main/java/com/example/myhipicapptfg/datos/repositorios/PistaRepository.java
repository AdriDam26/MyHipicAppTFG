package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Pista.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y el DAO,
 * centralizando las operaciones de acceso a datos, las validaciones
 * de negocio y la ejecución de tareas en segundo plano.
 */
public class PistaRepository {

    /**
     * DAO utilizado para acceder a los datos de las pistas.
     */
    private final PistaDao pistaDao;

    /**
     * Executor encargado de ejecutar operaciones de base de datos
     * fuera del hilo principal de la aplicación.
     */
    private final ExecutorService executor;

    /**
     * LiveData utilizado para comunicar el resultado de las
     * operaciones realizadas sobre la base de datos.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_NOMBRE_DUPLICADO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estado = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa el acceso al DAO y al ejecutor de tareas.
     *
     * @param app Contexto de la aplicación.
     */
    public PistaRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        pistaDao = db.pistaDao();
        executor= AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estado;
    }

    /**
     * Inserta una nueva pista en la base de datos.
     *
     * Antes de realizar la inserción se comprueba que no exista
     * otra pista con el mismo nombre para evitar duplicidades.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param pista Pista a insertar.
     */
    public void insertarPista(Pista pista) {

        executor.execute(() -> {

            if (pistaDao.buscarPorNombreSync(pista.nombre) != null) {
                estado.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }

            try {
                pistaDao.insertarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza la información de una pista existente.
     *
     * La operación se ejecuta en segundo plano para evitar
     * bloqueos de la interfaz de usuario.
     *
     * @param pista Pista con los datos actualizados.
     */
    public void actualizarPista(Pista pista) {

        executor.execute(() -> {

            try {
                pistaDao.actualizarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todas las pistas registradas en la base de datos.
     *
     * El resultado se devuelve mediante LiveData para que la
     * interfaz pueda actualizarse automáticamente cuando
     * se produzcan cambios.
     *
     * @return Lista observable de pistas.
     */
    public LiveData<List<Pista>> obtenerTodasPistas() {
        return pistaDao.obtenerTodasPistas();
    }

    /**
     * Busca una pista a partir de su identificador.
     *
     * @param id Identificador de la pista.
     * @return Pista encontrada.
     */
    public LiveData<Pista> buscarPorId(int id) {
        return pistaDao.buscarPorId(id);
    }


    /**
     * Elimina una pista de la base de datos.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param pista Pista a eliminar.
     */
    public void eliminarPista(Pista pista) {

        executor.execute(() -> {
            try {
                pistaDao.eliminarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

}