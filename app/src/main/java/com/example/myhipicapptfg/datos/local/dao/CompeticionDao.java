package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Competicion;

import java.util.List;

/**
 * DAO de Competicion.
 *
 * Gestiona el acceso a datos de las competiciones hípicas,
 * incluyendo operaciones CRUD y validaciones de unicidad.
 */
@Dao
public interface CompeticionDao {

    /**
     * Inserta una nueva competición.
     * @return ID generado de la competición.
     */
    @Insert
    long insertarCompeticion(Competicion c);

    @Update
    void actualizarCompeticion(Competicion c);

    @Delete
    void eliminarCompeticion(Competicion c);

    /**
     * Obtiene todas las competiciones ordenadas por fecha descendente.
     */
    @Query("SELECT * FROM Competicion ORDER BY Fecha DESC")
    LiveData<List<Competicion>> obtenerTodas();

    /**
     * Busca una competición por su ID.
     */
    @Query("SELECT * FROM Competicion WHERE ID_Competicion = :id")
    LiveData<Competicion> buscarPorId(int id);

    /**
     * Comprueba si existe una competición con el nombre indicado.
     */
    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE Nombre = :nombre")
    boolean existeNombreSync(String nombre);

    /**
     * Comprueba si existe una competición con ese nombre,
     * excluyendo una competición concreta (edición).
     */
    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE Nombre = :nombre AND ID_Competicion != :idExcluir")
    boolean existeNombreExcluyendoSync(String nombre, int idExcluir);
}