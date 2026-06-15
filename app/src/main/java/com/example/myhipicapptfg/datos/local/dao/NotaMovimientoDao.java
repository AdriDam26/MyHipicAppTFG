package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.model.MovimientoConNota;

import java.util.List;

/**
 * DAO (Data Access Object) para la entidad NotaMovimiento.
 *
 * Gestiona las operaciones de acceso a datos relacionadas con las
 * calificaciones obtenidas por los participantes en cada movimiento
 * de una prueba hípica.
 */
@Dao
public interface NotaMovimientoDao {

    /**
     * Inserta una nueva nota de movimiento.
     *
     * Si se produce un conflicto durante la inserción,
     * la operación se abortará.
     *
     * @param notaMovimiento Nota a insertar.
     * @return ID de la fila insertada.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarNotaMovimiento(NotaMovimiento notaMovimiento);

    /**
     * Actualiza una nota de movimiento existente.
     *
     * @param notaMovimiento Nota con los datos actualizados.
     * @return Número de filas modificadas.
     */
    @Update
    int actualizarNotaMovimiento(NotaMovimiento notaMovimiento);

    /**
     * Elimina una nota de movimiento de la base de datos.
     *
     * @param notaMovimiento Nota a eliminar.
     * @return Número de filas eliminadas.
     */
    @Delete
    int eliminarNotaMovimiento(NotaMovimiento notaMovimiento);

    /**
     * Obtiene todas las notas registradas.
     *
     * El resultado se devuelve como LiveData para permitir
     * la observación automática de cambios desde la interfaz.
     *
     * @return Lista observable de notas.
     */
    @Query("SELECT * FROM Nota_Movimiento")
    LiveData<List<NotaMovimiento>> obtenerTodasNotas();

    /**
     * Obtiene todas las notas asociadas a una participación concreta.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Lista observable de notas de la participación.
     */
    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    LiveData<List<NotaMovimiento>> obtenerPorParticipacion(int idParticipacion);

    /**
     * Obtiene todas las notas asociadas a un movimiento concreto.
     *
     * @param idMovimiento Identificador del movimiento.
     * @return Lista observable de notas del movimiento.
     */
    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Movimiento = :idMovimiento")
    LiveData<List<NotaMovimiento>> obtenerPorMovimiento(int idMovimiento);


    /**
     * Inserta o actualiza un conjunto de notas.
     *
     * Resulta útil para guardar una hoja de calificaciones completa
     * en una única operación.
     *
     * @param notas Lista de notas a insertar o actualizar.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAll(List<NotaMovimiento> notas);

    /**
     * Calcula la suma total de las notas obtenidas por una participación.
     *
     * Este método se ejecuta de forma síncrona y suele utilizarse
     * para cálculos internos o generación de clasificaciones.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Puntuación total obtenida.
     */
    @Query("SELECT SUM(Nota) FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    double totalParticipacionSync(int idParticipacion);


    /**
     * Obtiene todas las notas asociadas a una participación.
     *
     * Variante orientada a la interfaz de usuario mediante LiveData.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Lista observable de notas.
     */
    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    LiveData<List<NotaMovimiento>> getNotasByParticipacion(int idParticipacion);

    /**
     * Obtiene la hoja completa de calificaciones de una participación.
     *
     * La consulta realiza una unión (JOIN) entre las tablas
     * Nota_Movimiento y Movimiento para recuperar tanto la información
     * del movimiento como la nota y observaciones asociadas.
     *
     * El resultado se devuelve mediante el modelo MovimientoConNota,
     * utilizado para mostrar la hoja de juzgamiento completa en la
     * interfaz de usuario.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Lista observable de movimientos con sus respectivas
     *         calificaciones y observaciones.
     */
    @Query("SELECT " +
            "  nm.ID_Movimiento    AS idMovimiento, " +
            "  nm.ID_Participacion AS idParticipacion, " +
            "  m.Letra             AS letra, " +
            "  m.Ejercicio         AS ejercicio, " +
            "  m.Coeficiente       AS coeficiente, " +
            "  m.Directriz         AS directriz, " +
            "  m.Orden             AS orden, " +
            "  nm.Nota             AS nota, " +
            "  nm.Observacion      AS observacion " +
            "FROM Nota_Movimiento nm " +
            "INNER JOIN Movimiento m ON nm.ID_Movimiento = m.ID_Movimiento " +
            "WHERE nm.ID_Participacion = :idParticipacion " +
            "ORDER BY m.Orden ASC")
    LiveData<List<MovimientoConNota>> getHojaCalificaciones(int idParticipacion);
}