package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.model.PruebaConCompeticion;

import java.util.List;

/**
 * DAO de Prueba.
 *
 * Gestiona el acceso a datos de las pruebas de competición,
 * incluyendo su relación con movimientos y competiciones.
 */
@Dao
public interface PruebaDao {

    /**
     * Inserta una nueva prueba en la base de datos.
     *
     * @param prueba Prueba a insertar.
     * @return Identificador generado para la nueva prueba.
     */
    @Insert
    long insertarPrueba(Prueba prueba);

    /**
     * Actualiza los datos de una prueba existente.
     *
     * @param prueba Prueba con la información modificada.
     */
    @Update
    void actualizarPrueba(Prueba prueba);

    /**
     * Elimina una prueba de la base de datos.
     *
     * @param prueba Prueba a eliminar.
     */
    @Delete
    void eliminarPrueba(Prueba prueba);

    /**
     * Busca una prueba a partir de su identificador.
     *
     * El resultado se devuelve como LiveData para que la
     * interfaz pueda observar automáticamente los cambios.
     *
     * @param id Identificador de la prueba.
     * @return Prueba encontrada o null si no existe.
     */
    @Query("SELECT * FROM Prueba WHERE ID_Prueba = :id")
    LiveData<Prueba> buscarPruebaPorId(int id);

    /**
     * Obtiene todas las pruebas pertenecientes a una competición.
     *
     * Los resultados se ordenan alfabéticamente por nombre.
     *
     * @param idCompeticion Identificador de la competición.
     * @return Lista observable de pruebas.
     */
    @Query("SELECT * FROM Prueba WHERE ID_Competicion = :idCompeticion ORDER BY Nombre ASC")
    LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion);

    /**
     * Comprueba si existe otra prueba con el mismo nombre,
     * excluyendo una prueba determinada.
     *
     * Este método se utiliza habitualmente durante la edición
     * para evitar nombres duplicados.
     *
     * @param nombre Nombre que se desea comprobar.
     * @param idExcluir Identificador de la prueba a excluir.
     * @return true si existe otra prueba con ese nombre.
     */
    @Query("SELECT COUNT(*) > 0 FROM Prueba WHERE Nombre = :nombre AND ID_Prueba != :idExcluir")
    boolean existeNombreExcluyendoSync(String nombre, int idExcluir);

    /**
     * Verifica si existe una competición con el identificador indicado.
     *
     * Método de validación utilizado antes de crear o modificar
     * una prueba.
     *
     * @param idCompeticion Identificador de la competición.
     * @return true si la competición existe.
     */
    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE ID_Competicion = :idCompeticion")
    boolean existeCompeticionSync(int idCompeticion);

    /**
     * Inserta una lista de movimientos asociados a una prueba.
     *
     * @param movimientos Lista de movimientos a insertar.
     */
    @Insert
    void insertarMovimientos(List<Movimiento> movimientos);

    /**
     * Actualiza una lista de movimientos existentes.
     *
     * @param movimientos Lista de movimientos actualizados.
     */
    @Update
    void actualizarMovimientos(List<Movimiento> movimientos);


    /**
     * Elimina una lista de movimientos de la base de datos.
     *
     * @param movimientos Lista de movimientos a eliminar.
     */
    @Delete
    void eliminarMovimientos(List<Movimiento> movimientos);

    /**
     * Obtiene todos los movimientos asociados a una prueba.
     *
     * Los movimientos se devuelven ordenados según el campo
     * Orden para mantener la secuencia oficial de la reprise.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista observable de movimientos.
     */
    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerMovimientosPorPrueba(int idPrueba);

    /**
     * Obtiene de forma síncrona todos los movimientos asociados
     * a una prueba.
     *
     * Este método está pensado para ejecutarse en segundo plano
     * dentro del repositorio o la capa de negocio.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista de movimientos de la prueba.
     */
    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    List<Movimiento> obtenerMovimientosPorPruebaSync(int idPrueba);

    /**
     * Actualiza el estado de publicación de una prueba.
     *
     * Permite indicar si la prueba está disponible para
     * ser utilizada o visualizada dentro de la aplicación.
     *
     * @param idPrueba Identificador de la prueba.
     * @param publicado Nuevo estado de publicación.
     */
    @Query("UPDATE Prueba SET Publicado = :publicado WHERE ID_Prueba = :idPrueba")
    void actualizarPublicado(int idPrueba, boolean publicado);


    /**
     * Obtiene el estado de publicación de una prueba.
     *
     * El resultado se devuelve mediante LiveData para permitir
     * la actualización automática de la interfaz.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Estado de publicación de la prueba.
     */
    @Query("SELECT Publicado FROM Prueba WHERE ID_Prueba = :idPrueba")
    LiveData<Boolean> isPublicado(int idPrueba);

    /**
     * Obtiene todas las pruebas asignadas a un juez junto con
     * información básica de la competición correspondiente.
     *
     * La consulta realiza una unión (INNER JOIN) entre las tablas
     * Prueba y Competicion y proyecta el resultado sobre el modelo
     * PruebaConCompeticion.
     *
     * Los resultados se ordenan cronológicamente por fecha de
     * competición y posteriormente por nombre de prueba.
     *
     * @param idJuez Identificador del juez.
     * @return Lista observable de pruebas con información de competición.
     */
    @Query("SELECT " +
            "  p.ID_Prueba        AS idPrueba, " +
            "  p.Nombre           AS nombrePrueba, " +
            "  c.Nombre           AS nombreCompeticion, " +
            "  p.Categoria        AS categoria, " +
            "  p.Nivel            AS nivel " +
            "FROM Prueba p " +
            "INNER JOIN Competicion c ON p.ID_Competicion = c.ID_Competicion " +
            "WHERE p.ID_Juez = :idJuez " +
            "ORDER BY c.Fecha ASC, p.Nombre ASC")
    LiveData<List<PruebaConCompeticion>> getPruebasByJuez(int idJuez);
}