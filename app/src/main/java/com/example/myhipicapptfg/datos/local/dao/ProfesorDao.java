package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Profesor;

import java.util.List;


/**
 * DAO de Profesor.
 *
 * Gestiona el acceso a datos de los profesores del sistema,
 * incluyendo operaciones CRUD, consultas básicas y búsquedas relacionadas con Usuario.
 */
@Dao
public interface ProfesorDao {

    /**
     * Inserta un profesor.
     * Si hay conflicto (ID duplicado), se aborta la operación.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarProfesor(Profesor profesor);


    @Update
    int actualizarProfesor(Profesor profesor);

    @Delete
    int eliminarProfesor(Profesor profesor);

    /**
     * Obtiene todos los profesores registrados.
     */
    @Query("SELECT * FROM Profesor")
    LiveData<List<Profesor>> obtenerTodosProfesores();

    /**
     * Busca un profesor por su ID.
     */
    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    LiveData<Profesor> buscarPorId(int id);

    /**
     * Busca un profesor por su ID.
     */
    // 🔹 CONTAR
    @Query("SELECT COUNT(*) FROM Profesor")
    LiveData<Integer> contarProfesores();

    /**
     * Obtiene un profesor por ID de forma síncrona.
     */
    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    Profesor buscarPorIdSync(int id);


}