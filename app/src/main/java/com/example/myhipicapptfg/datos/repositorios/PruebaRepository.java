package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con las pruebas hípicas y los movimientos asociados.
 *
 * Actúa como intermediario entre los ViewModel y el DAO,
 * centralizando la lógica de negocio, las validaciones y
 * las operaciones transaccionales sobre la base de datos.
 */
public class PruebaRepository {


    /**
     * DAO utilizado para acceder a los datos de las pruebas.
     */
    private final PruebaDao dao;

    /**
     * Instancia de la base de datos Room.
     */
    private final AppDatabase db;

    /**
     * Executor utilizado para ejecutar operaciones de base de datos
     * en segundo plano.
     */
    private final ExecutorService executor;
    /**
     * LiveData utilizado para informar del resultado de las operaciones.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_COMPETICION_NO_EXISTE
     * - ERROR_NOMBRE_DUPLICADO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa el acceso a la base de datos, el DAO y el
     * ejecutor de tareas en segundo plano.
     *
     * @param application Contexto de la aplicación.
     */
    public PruebaRepository(@NonNull Application application) {
        db       = AppDatabase.getInstance(application);
        dao      = db.pruebaDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() { return estadoOperacion; }

    /**
     * Busca una prueba por su identificador.
     *
     * @param id Identificador de la prueba.
     * @return Prueba encontrada.
     */
    public LiveData<Prueba> buscarPorId(int id) {
        return dao.buscarPruebaPorId(id);
    }

    /**
     * Obtiene todas las pruebas pertenecientes
     * a una competición determinada.
     *
     * @param idCompeticion Identificador de la competición.
     * @return Lista observable de pruebas.
     */
    public LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion) {
        return dao.obtenerPorCompeticion(idCompeticion);
    }

    /**
     * Obtiene los movimientos asociados a una prueba.
     *
     * Los movimientos se devuelven ordenados según
     * el orden definido en la reprise.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista observable de movimientos.
     */
    public LiveData<List<Movimiento>> obtenerMovimientosPorPrueba(int idPrueba) {
        return dao.obtenerMovimientosPorPrueba(idPrueba);
    }

    /**
     * Obtiene el estado de publicación de una prueba.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Estado de publicación.
     */
    public LiveData<Boolean> isPublicado(int idPrueba) {
        return dao.isPublicado(idPrueba);
    }

    /**
     * Actualiza el estado de publicación de una prueba.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param idPrueba Identificador de la prueba.
     * @param publicado Nuevo estado de publicación.
     */
    public void actualizarPublicado(int idPrueba, boolean publicado) {
        executor.execute(() -> dao.actualizarPublicado(idPrueba, publicado));
    }

    /**
     * Inserta una nueva prueba junto con todos sus movimientos.
     *
     * Antes de realizar la operación se comprueba que la
     * competición asociada exista en la base de datos.
     *
     * La inserción se realiza dentro de una transacción para
     * garantizar que la prueba y sus movimientos se almacenan
     * de forma consistente.
     *
     * @param prueba Prueba a insertar.
     * @param movimientos Lista de movimientos asociados.
     */
    public void insertarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        executor.execute(() -> {
            if (!dao.existeCompeticionSync(prueba.idCompeticion)) {
                estadoOperacion.postValue("ERROR_COMPETICION_NO_EXISTE");
                return;
            }
            try {
                db.runInTransaction(() -> {
                    long idPrueba = dao.insertarPrueba(prueba);
                    for (Movimiento m : movimientos) {
                        m.idPrueba = (int) idPrueba;
                    }
                    dao.insertarMovimientos(movimientos);
                });
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void actualizarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        executor.execute(() -> {

            // Validar que no haya otra prueba con el mismo nombre
            if (dao.existeNombreExcluyendoSync(prueba.nombre, prueba.idPrueba)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }

            try {
                // Leer los movimientos actuales en BD
                // Fuera de la transacción para no mantener el lock más de lo necesario
                List<Movimiento> movimientosEnBD = dao.obtenerMovimientosPorPruebaSync(prueba.idPrueba);

                // Recoger los IDs de los movimientos que llegan desde la UI
                //  Si un movimiento tiene ID > 0, significa que ya existía en BD
                Set<Integer> idsQueSeConservan = new HashSet<>();
                for (Movimiento movimientoEntrante : movimientos) {
                    if (movimientoEntrante.idMovimiento > 0) {
                        idsQueSeConservan.add(movimientoEntrante.idMovimiento);
                    }
                }

                // Clasificar movimientos en tres listas ──────────────────────

                // Los que estaban en BD pero ya no vienen, hay que borrarlos
                List<Movimiento> aEliminar = new ArrayList<>();
                for (Movimiento movimientoEnBD : movimientosEnBD) {
                    if (!idsQueSeConservan.contains(movimientoEnBD.idMovimiento)) {
                        aEliminar.add(movimientoEnBD);
                    }
                }

                // Los que vienen desde la UI, actualizar si ya tenían ID, insertar si son nuevos
                List<Movimiento> aActualizar = new ArrayList<>();
                List<Movimiento> aInsertar   = new ArrayList<>();
                for (Movimiento movimientoEntrante : movimientos) {
                    // Aseguramos la FK correcta
                    movimientoEntrante.idPrueba = prueba.idPrueba;
                    if (movimientoEntrante.idMovimiento > 0) {
                        aActualizar.add(movimientoEntrante);
                    } else                                    {
                        aInsertar.add(movimientoEntrante);
                    }
                }

                // Persistir todo en una única transacción atómica
                // Si cualquier operación falla, Room revierte los cambios completos
                db.runInTransaction(() -> {
                    dao.actualizarPrueba(prueba);
                    if (!aEliminar.isEmpty())   {
                        dao.eliminarMovimientos(aEliminar);
                    }
                    if (!aActualizar.isEmpty()) {
                        dao.actualizarMovimientos(aActualizar);
                    }
                    if (!aInsertar.isEmpty())   {
                        dao.insertarMovimientos(aInsertar);
                    }
                });

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina una prueba de la base de datos.
     *
     * La eliminación se ejecuta en segundo plano.
     *
     * @param prueba Prueba a eliminar.
     */
    public void eliminarPrueba(Prueba prueba) {
        executor.execute(() -> {
            try {
                dao.eliminarPrueba(prueba);
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}