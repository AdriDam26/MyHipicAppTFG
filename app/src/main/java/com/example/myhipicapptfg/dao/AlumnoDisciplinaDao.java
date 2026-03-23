package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import java.util.List;

@Dao
public interface AlumnoDisciplinaDao {

    @Insert
    void insertar(AlumnoDisciplina ad);

    @Update
    void actualizar(AlumnoDisciplina ad);

    @Delete
    void eliminar(AlumnoDisciplina ad);

    @Query("SELECT * FROM AlumnoDisciplina")
    LiveData<List<AlumnoDisciplina>> obtenerTodas();

    // Verificación 1: ¿Existe el Alumno y tiene ese rol?
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Alumno')")
    boolean esAlumnoValido(int id);

    // Verificación 2: ¿Existe la Disciplina?
    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplina(int id);

    @Query("SELECT * FROM AlumnoDisciplina WHERE ID_Alumno = :idAlumno")
    List<AlumnoDisciplina> obtenerPorAlumno(int idAlumno);
}