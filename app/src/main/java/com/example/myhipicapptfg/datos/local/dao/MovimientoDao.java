package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Movimiento;

import java.util.List;

/**
 * DAO (Data Access Object) para la entidad Movimiento.
 *
 * Gestiona todas las operaciones de acceso a datos relacionadas
 * con la tabla Movimiento de la base de datos Room.
 */
@Dao
public interface MovimientoDao {

    /**
     * Inserta un nuevo movimiento en la base de datos.
     *
     * Si se produce un conflicto durante la inserción,
     * la operación se abortará y lanzará una excepción.
     *
     * @param movimiento Movimiento a insertar.
     * @return ID de la fila insertada.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarMovimiento(Movimiento movimiento);


    /**
     * Actualiza los datos de un movimiento existente.
     *
     * @param movimiento Movimiento con la información actualizada.
     * @return Número de filas modificadas.
     */
    @Update
    int actualizarMovimiento(Movimiento movimiento);

    /**
     * Elimina un movimiento de la base de datos.
     *
     * @param movimiento Movimiento a eliminar.
     * @return Número de filas eliminadas.
     */
    @Delete
    int eliminarMovimiento(Movimiento movimiento);

    /**
     * Obtiene todos los movimientos almacenados en la base de datos
     * ordenados por el campo Orden de forma ascendente.
     *
     * Se devuelve un LiveData para que la interfaz de usuario
     * observe automáticamente cualquier cambio.
     *
     * @return Lista observable de movimientos.
     */
    @Query("SELECT * FROM Movimiento ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerTodosMovimientos();


    /**
     * Busca un movimiento por su identificador.
     *
     * El resultado se devuelve como LiveData para permitir
     * la actualización automática de la interfaz cuando
     * cambien los datos.
     *
     * @param id Identificador del movimiento.
     * @return Movimiento encontrado o null si no existe.
     */
    @Query("SELECT * FROM Movimiento WHERE ID_Movimiento = :id LIMIT 1")
    LiveData<Movimiento> buscarMovimientoPorId(int id);

    /**
     * Obtiene todos los movimientos ordenados por el campo Orden.
     *
     * Método equivalente al listado general, útil para consultas
     * específicas donde se requiera un nombre más descriptivo.
     *
     * @return Lista observable de movimientos ordenados.
     */
    @Query("SELECT * FROM Movimiento ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerMovimientosOrdenados();


    /**
     * Comprueba si existe algún movimiento con el valor de orden indicado.
     *
     * Se utiliza habitualmente para validar que no existan
     * posiciones duplicadas dentro de una secuencia de movimientos.
     *
     * @param orden Valor del orden a comprobar.
     * @return 1 si existe, 0 si no existe.
     */
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Movimiento WHERE Orden = :orden)")
    int existeOrdenSync(int orden);


    /**
     * Obtiene todos los movimientos asociados a una prueba concreta,
     * ordenados por el campo Orden.
     *
     * Al devolver LiveData, cualquier modificación en los movimientos
     * de la prueba se reflejará automáticamente en la interfaz.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista observable de movimientos de la prueba.
     */
    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    LiveData<List<Movimiento>> getMovimientosByPrueba(int idPrueba);





}