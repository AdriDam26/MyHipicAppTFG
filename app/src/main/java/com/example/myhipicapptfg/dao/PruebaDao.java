package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Prueba;

import java.util.List;

@Dao
public interface PruebaDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarPrueba(Prueba prueba);

    // 🔹 UPDATE
    @Update
    int actualizarPrueba(Prueba prueba);

    // 🔹 DELETE
    @Delete
    int eliminarPrueba(Prueba prueba);

    // 🔹 LISTADO GENERAL (UI)
    @Query("SELECT * FROM Prueba ORDER BY Nombre ASC")
    LiveData<List<Prueba>> obtenerTodasPruebas();

    // 🔹 BUSCAR POR ID (UI)
    @Query("SELECT * FROM Prueba WHERE ID_Prueba = :id LIMIT 1")
    LiveData<Prueba> buscarPruebaPorId(int id);

    // 🔹 BUSCAR POR COMPETICIÓN (UI)
    @Query("SELECT * FROM Prueba WHERE ID_Competicion = :idCompeticion")
    LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion);

    // ----------------------------------------------------
    // 🔹 MÉTODOS SYNC (validaciones / repositorio)
    // ----------------------------------------------------

    // ✔ validar nombre único (evitar duplicados)
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Prueba WHERE Nombre = :nombre)")
    int existeNombreSync(String nombre);

    // ✔ validar competición
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Competicion WHERE ID_Competicion = :id)")
    int existeCompeticionSync(int id);
}