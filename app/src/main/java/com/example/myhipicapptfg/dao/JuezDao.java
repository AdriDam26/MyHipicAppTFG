package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Juez;

import java.util.List;

@Dao
public interface JuezDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarJuez(Juez juez);

    // 🔹 UPDATE
    @Update
    int actualizarJuez(Juez juez);

    // 🔹 DELETE
    @Delete
    int eliminarJuez(Juez juez);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Juez")
    LiveData<List<Juez>> obtenerTodosJueces();

    // 🔹 BUSCAR POR ID
    @Query("SELECT * FROM Juez WHERE ID_Juez = :id LIMIT 1")
    LiveData<Juez> buscarPorId(int id);

    // 🔹 CONTAR
    @Query("SELECT COUNT(*) FROM Juez")
    LiveData<Integer> contarJueces();

    // 🔹 SYNC (validaciones / lógica interna)
    @Query("SELECT * FROM Juez WHERE ID_Juez = :id LIMIT 1")
    Juez buscarPorIdSync(int id);

    // 🔹 BUSCAR POR NÚMERO DE LICENCIA
    @Query("SELECT * FROM Juez WHERE Numero_Licencia = :licencia LIMIT 1")
    Juez buscarPorLicenciaSync(String licencia);

    // 🔹 OBTENER ID POR LICENCIA
    @Query("SELECT ID_Juez FROM Juez WHERE Numero_Licencia = :licencia LIMIT 1")
    int obtenerIdPorLicenciaSync(String licencia);
}