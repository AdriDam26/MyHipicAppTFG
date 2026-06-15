package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;


/**
 * DAO de Juez.
 *
 * Gestiona el acceso a datos de los jueces del sistema,
 * incluyendo operaciones CRUD, búsquedas y consultas combinadas con Usuario.
 */
@Dao
public interface JuezDao {

    /**
     * Inserta un juez.
     * Si hay conflicto (ID duplicado), se aborta la operación.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarJuez(Juez juez);


    @Update
    int actualizarJuez(Juez juez);


    @Delete
    int eliminarJuez(Juez juez);

    /**
     * Obtiene todos los jueces registrados.
     */

    @Query("SELECT * FROM Juez")
    LiveData<List<Juez>> obtenerTodosJueces();


    /**
     * Busca un juez por su ID.
     */
    @Query("SELECT * FROM Juez WHERE ID_Juez = :id LIMIT 1")
    LiveData<Juez> buscarPorId(int id);

    /**
     * Cuenta el número total de jueces.
     */
    @Query("SELECT COUNT(*) FROM Juez")
    LiveData<Integer> contarJueces();


    /**
     * Obtiene un juez por ID de forma síncrona.
     */
    @Query("SELECT * FROM Juez WHERE ID_Juez = :id LIMIT 1")
    Juez buscarPorIdSync(int id);

    /**
     * Busca un juez por número de licencia (modo síncrono).
     */
    @Query("SELECT * FROM Juez WHERE Numero_Licencia = :licencia LIMIT 1")
    Juez buscarPorLicenciaSync(String licencia);


    /**
     * Obtiene jueces activos junto con datos del usuario asociado.
     *
     * Se realiza un JOIN entre Usuario y Juez para obtener información completa.
     */
    @Query("SELECT u.* FROM Usuario u " +
            "INNER JOIN Juez j ON u.ID_Usuario = j.ID_Juez " +
            "WHERE j.Activo = 1 " +
            "ORDER BY u.Apellido1 ASC")
    LiveData<List<Usuario>> obtenerJuecesActivosConNombre();
}