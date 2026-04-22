package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Clase;

import java.util.List;

@Dao
public interface ClaseDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarClase(Clase clase);

    // 🔹 UPDATE
    @Update
    int actualizarClase(Clase clase);

    // 🔹 DELETE
    @Delete
    int eliminarClase(Clase clase);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Clase")
    LiveData<List<Clase>> obtenerTodasClases();

    // 🔹 BUSCAR POR ID
    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    LiveData<Clase> buscarPorId(int id);

    // 🔹 CONTAR
    @Query("SELECT COUNT(*) FROM Clase")
    LiveData<Integer> contarClases();

    // 🔹 VALIDAR EXISTENCIA PISTA (SYNC)
    @Query("SELECT EXISTS(SELECT 1 FROM Pista WHERE ID_Pista = :idPista)")
    boolean existePistaSync(int idPista);

    // 🔹 VALIDAR EXISTENCIA PROFESOR (SYNC)
    @Query("SELECT EXISTS(SELECT 1 FROM Profesor WHERE ID_Profesor = :idProfesor)")
    boolean existeProfesorSync(int idProfesor);

    // 🔹 BUSCAR POR ID (SYNC)
    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    Clase buscarPorIdSync(int id);
}