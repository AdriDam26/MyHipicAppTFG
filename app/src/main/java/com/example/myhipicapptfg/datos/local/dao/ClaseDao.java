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


    @Insert
    long insertarClase(Clase clase);


    @Update
    int actualizarClase(Clase clase);


    @Delete
    int eliminarClase(Clase clase);


    @Query("SELECT * FROM Clase")
    LiveData<List<Clase>> obtenerTodasClases();


    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    LiveData<Clase> buscarPorId(int id);




    @Query("SELECT EXISTS(SELECT 1 FROM Pista WHERE ID_Pista = :idPista)")
    boolean existePistaSync(int idPista);


    @Query("SELECT EXISTS(SELECT 1 FROM Profesor WHERE ID_Profesor = :idProfesor)")
    boolean existeProfesorSync(int idProfesor);


    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    Clase buscarPorIdSync(int id);

    @Query("SELECT * FROM Clase WHERE ID_Profesor = :idProfesor ORDER BY Fecha ASC, Hora_Inicio ASC")
    LiveData<List<Clase>> obtenerClasesPorProfesor(int idProfesor);
}