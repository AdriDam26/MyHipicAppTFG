package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Alumno;

import java.util.List;

@Dao
public interface AlumnoDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarAlumno(Alumno alumno);

    // 🔹 UPDATE
    @Update
    int actualizarAlumno(Alumno alumno);

    // 🔹 DELETE
    @Delete
    int eliminarAlumno(Alumno alumno);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Alumno")
    LiveData<List<Alumno>> obtenerTodosAlumnos();

    // 🔹 BUSCAR POR ID
    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    LiveData<Alumno> buscarPorId(int id);

    // 🔹 CONTAR REGISTROS
    @Query("SELECT COUNT(*) FROM Alumno")
    LiveData<Integer> contarAlumnos();

    // 🔹 CONSULTA SÍNCRONA (validaciones / lógica interna)
    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    Alumno buscarPorIdSync(int id);
}