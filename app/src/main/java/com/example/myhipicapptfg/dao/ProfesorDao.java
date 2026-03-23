package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Profesor;

import java.util.List;

@Dao
public interface ProfesorDao {

    @Insert
    void insertarProfesor(Profesor profesor);

    @Update
    void actualizarProfesor(Profesor profesor);

    @Delete
    void eliminarProfesor(Profesor profesor);

    // Usamos LiveData para que el repositorio pueda observar cambios
    @Query("SELECT * FROM Profesor")
    LiveData<List<Profesor>> obtenerTodosProfesores();

    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    LiveData<Profesor> buscarPorId(int id);

    @Query("SELECT COUNT(*) FROM Profesor")
    LiveData<Integer> contarProfesores();

    // Método síncrono por si necesitas consultar datos en un hilo de fondo sin observar
    @Query("SELECT * FROM Profesor WHERE ID_Profesor = :id LIMIT 1")
    Profesor buscarPorIdSync(int id);

    @Query("SELECT p.ID_Profesor FROM Profesor p " +
            "INNER JOIN Usuario u ON p.ID_Profesor = u.ID_Usuario " +
            "WHERE u.Nombre = :nombre LIMIT 1")
    int obtenerIdProfesorPorNombreSync(String nombre);
}
