package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Clase;

import java.util.List;

@Dao
public interface ClaseDao {

    // 🔹 INSERT
    @Insert
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



    // 🔹 VALIDAR EXISTENCIA PISTA (SYNC)
    @Query("SELECT EXISTS(SELECT 1 FROM Pista WHERE ID_Pista = :idPista)")
    boolean existePistaSync(int idPista);

    // 🔹 VALIDAR EXISTENCIA PROFESOR (SYNC)
    @Query("SELECT EXISTS(SELECT 1 FROM Profesor WHERE ID_Profesor = :idProfesor)")
    boolean existeProfesorSync(int idProfesor);

    // 🔹 BUSCAR POR ID (SYNC)
    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    Clase buscarPorIdSync(int id);

    @Query("SELECT * FROM Clase WHERE ID_Profesor = :idProfesor ORDER BY Fecha ASC, Hora_Inicio ASC")
    LiveData<List<Clase>> obtenerClasesPorProfesor(int idProfesor);
}