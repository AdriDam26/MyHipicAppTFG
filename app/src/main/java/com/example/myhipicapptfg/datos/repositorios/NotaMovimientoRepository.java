package com.example.myhipicapptfg.datos.repositorios;
import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad NotaMovimiento.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y el DAO,
 * centralizando el acceso a los datos de las calificaciones asignadas
 * a cada movimiento durante la evaluación de una participación.
 *
 * Además, gestiona la ejecución de operaciones de escritura en
 * segundo plano y comunica el resultado de las mismas mediante LiveData.
 */
public class NotaMovimientoRepository {


    /**
     * DAO utilizado para acceder a los datos de las notas de movimientos.
     */
    private final NotaMovimientoDao dao;

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
    public NotaMovimientoRepository(Application application) {
        dao = AppDatabase.getInstance(application).notaMovimientoDao();
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
     * Inserta una nueva nota asociada a un movimiento.
     *
     * La operación se ejecuta en segundo plano para evitar
     * bloqueos de la interfaz de usuario.
     *
     * @param n NotaMovimiento a insertar.
     */
    public void insertar(NotaMovimiento n) {

        executor.execute(() -> {
            try {
                dao.insertarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    /**
     * Actualiza una nota de movimiento existente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param n NotaMovimiento con los datos actualizados.
     */
    public void actualizar(NotaMovimiento n) {

        executor.execute(() -> {
            try {
                dao.actualizarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina una nota de movimiento de la base de datos.
     *
     * @param n NotaMovimiento a eliminar.
     */
    public void eliminar(NotaMovimiento n) {

        executor.execute(() -> {
            try {
                dao.eliminarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    /**
     * Obtiene todas las notas registradas en el sistema.
     *
     * El resultado se devuelve mediante LiveData para que
     * la interfaz se actualice automáticamente ante cambios.
     *
     * @return Lista observable de notas.
     */
    public LiveData<List<NotaMovimiento>> obtenerTodas() {
        return dao.obtenerTodasNotas();
    }

    /**
     * Obtiene todas las notas asociadas a una participación.
     *
     * Este método se utiliza habitualmente para mostrar la hoja
     * de calificaciones de un participante en una prueba.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Lista observable de notas.
     */
    public LiveData<List<NotaMovimiento>> obtenerPorParticipacion(int idParticipacion) {
        return dao.obtenerPorParticipacion(idParticipacion);
    }


    /**
     * Obtiene todas las notas registradas para un movimiento concreto.
     *
     * Permite analizar o consultar las evaluaciones realizadas
     * sobre un movimiento específico.
     *
     * @param idMovimiento Identificador del movimiento.
     * @return Lista observable de notas.
     */
    public LiveData<List<NotaMovimiento>> obtenerPorMovimiento(int idMovimiento) {
        return dao.obtenerPorMovimiento(idMovimiento);
    }


}