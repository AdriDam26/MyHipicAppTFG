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

@Dao
public interface ProfesorDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarProfesor(Profesor profesor);

    // 🔹 UPDATE
    @Update
    int actualizarProfesor(Profesor profesor);

    // 🔹 DELETE
    @Delete
    int eliminarProfesor(Profesor profesor);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Profesor")
    LiveData<List<Profesor>> obtenerTodosProfesores();

    // 🔹 BUSCAR POR ID
    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    LiveData<Profesor> buscarPorId(int id);

    // 🔹 CONTAR
    @Query("SELECT COUNT(*) FROM Profesor")
    LiveData<Integer> contarProfesores();

    // 🔹 SYNC (validaciones / lógica interna)
    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    Profesor buscarPorIdSync(int id);

    // 🔹 OBTENER ID POR NOMBRE
    @Query("SELECT p.ID_Profesor " +
            "FROM Profesor p " +
            "INNER JOIN Usuario u ON p.ID_Profesor = u.ID_Usuario " +
            "WHERE u.Nombre = :nombre " +
            "LIMIT 1")
    int obtenerIdProfesorPorNombreSync(String nombre);
}