package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.myhipicapptfg.entities.Alumno;
import java.util.List;

@Dao
public interface AlumnoDao {

    @Insert
    void insertarAlumno(Alumno alumno);

    @Update
    void actualizarAlumno(Alumno alumno);

    @Delete
    int eliminarAlumno(Alumno alumno);

    @Query("SELECT * FROM Alumno")
    LiveData<List<Alumno>> obtenerTodosAlumnos();

    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    LiveData<Alumno> buscarPorId(int id);

    @Query("SELECT COUNT(*) FROM Alumno")
    LiveData<Integer> contarAlumnos();

    // Método síncrono para comprobaciones rápidas en hilos de fondo
    @Query("SELECT * FROM Alumno WHERE ID_Alumno = :id LIMIT 1")
    Alumno buscarPorIdSync(int id);
}
