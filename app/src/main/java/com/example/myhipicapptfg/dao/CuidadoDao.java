package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Cuidado;

import java.util.List;

@Dao
public interface CuidadoDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertarCuidado(Cuidado cuidado);

    // 🔹 UPDATE
    @Update
    int actualizarCuidado(Cuidado cuidado);

    // 🔹 DELETE
    @Delete
    int eliminarCuidado(Cuidado cuidado);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Cuidado ORDER BY Fecha DESC")
    LiveData<List<Cuidado>> obtenerTodosCuidado();

    // ----------------------------------------------------
    // 🔹 SYNC (validaciones internas / repositorio)
    // ----------------------------------------------------

    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE ID_Equino = :id)")
    boolean existeEquino(int id);
}