package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Competicion;

import java.util.List;

@Dao
public interface CompeticionDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarCompeticion(Competicion competicion);

    // 🔹 UPDATE
    @Update
    int actualizarCompeticion(Competicion competicion);

    // 🔹 DELETE
    @Delete
    int eliminarCompeticion(Competicion competicion);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Competicion ORDER BY Fecha DESC")
    LiveData<List<Competicion>> obtenerTodasCompeticion();

    // 🔹 BUSCAR POR ID (UI)
    @Query("SELECT * FROM Competicion WHERE ID_Competicion = :id LIMIT 1")
    LiveData<Competicion> buscarPorId(int id);

    // ----------------------------------------------------
    // 🔹 MÉTODOS SYNC (validaciones / repositorio)
    // ----------------------------------------------------

    // ✔ validar nombre único
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Competicion WHERE Nombre = :nombre)")
    int existeNombreSync(String nombre);
}