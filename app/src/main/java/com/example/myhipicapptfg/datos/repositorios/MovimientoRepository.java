package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.dao.MovimientoDao;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Movimiento.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y el DAO,
 * centralizando las validaciones de negocio, las operaciones de
 * escritura en segundo plano y el acceso a los datos de los
 * movimientos que componen una prueba de doma.
 *
 * Los movimientos representan cada uno de los ejercicios que deben
 * ser ejecutados y posteriormente evaluados durante una prueba.
 */
public class MovimientoRepository {

    /**
     * DAO utilizado para acceder a los datos de los movimientos.
     */
    private final MovimientoDao dao;


    /**
     * Executor encargado de ejecutar operaciones de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executor;

    /**
     * LiveData que almacena el resultado de la última operación realizada.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_ORDEN_DUPLICADO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa el DAO y el ejecutor de tareas utilizado para
     * realizar operaciones asíncronas sobre la base de datos.
     *
     * @param application Contexto de la aplicación.
     */
    public MovimientoRepository(Application application) {
        dao = AppDatabase.getInstance(application).movimientoDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta un nuevo movimiento en la base de datos.
     *
     * Antes de realizar la inserción se comprueba que el orden
     * asignado al movimiento no exista previamente, evitando
     * duplicidades en la secuencia de ejercicios.
     *
     * @param m Movimiento a insertar.
     */
    public void insertar(Movimiento m) {

        executor.execute(() -> {

            // Validar orden único
            if (dao.existeOrdenSync(m.orden) > 0) {
                estadoOperacion.postValue("ERROR_ORDEN_DUPLICADO");
                return;
            }

            try {
                dao.insertarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza la información de un movimiento existente.
     *
     * La operación se ejecuta en segundo plano para evitar
     * bloqueos de la interfaz de usuario.
     *
     * @param m Movimiento con los datos actualizados.
     */
    public void actualizar(Movimiento m) {

        executor.execute(() -> {

            try {
                dao.actualizarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina un movimiento de la base de datos.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param m Movimiento a eliminar.
     */
    public void eliminar(Movimiento m) {

        executor.execute(() -> {

            try {
                dao.eliminarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todos los movimientos registrados en el sistema.
     *
     * @return Lista observable de movimientos.
     */

    public LiveData<List<Movimiento>> obtenerTodos() {
        return dao.obtenerTodosMovimientos();
    }

    /**
     * Busca un movimiento a partir de su identificador.
     *
     * @param id Identificador del movimiento.
     * @return Movimiento encontrado.
     */
    public LiveData<Movimiento> buscarPorId(int id) {
        return dao.buscarMovimientoPorId(id);
    }

    /**
     * Obtiene todos los movimientos ordenados según
     * el campo Orden.
     *
     * Esta consulta permite recuperar los movimientos
     * respetando la secuencia establecida dentro de una prueba.
     *
     * @return Lista observable de movimientos ordenados.
     */
    public LiveData<List<Movimiento>> obtenerOrdenados() {
        return dao.obtenerMovimientosOrdenados();
    }
}