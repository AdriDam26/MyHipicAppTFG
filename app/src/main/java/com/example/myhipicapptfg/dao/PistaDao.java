package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Pista;

import java.util.List;

@Dao
public interface PistaDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarPista(Pista pista);

    // 🔹 UPDATE
    @Update
    int actualizarPista(Pista pista);

    // 🔹 DELETE
    @Delete
    int eliminarPista(Pista pista);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Pista")
    LiveData<List<Pista>> obtenerTodasPistas();

    // 🔹 BUSCAR POR ID
    @Query("SELECT * FROM Pista WHERE ID_Pista = :id LIMIT 1")
    LiveData<Pista> buscarPorId(int id);

    // 🔹 BUSCAR POR NOMBRE (SYNC)
    @Query("SELECT * FROM Pista WHERE Nombre = :nombre LIMIT 1")
    Pista buscarPorNombreSync(String nombre);

    // 🔹 CONTAR
    @Query("SELECT COUNT(*) FROM Pista")
    LiveData<Integer> contarPistas();
}