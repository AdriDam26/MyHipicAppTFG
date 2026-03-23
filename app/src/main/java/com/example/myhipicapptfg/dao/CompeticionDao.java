package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Competicion;
import java.util.List;

@Dao
public interface CompeticionDao {

    @Insert
    long insertarCompeticion(Competicion competicion);

    @Update
    void actualizarCompeticion(Competicion competicion);

    @Delete
    void eliminarCompeticion(Competicion competicion);

    @Query("SELECT * FROM Competicion ORDER BY Fecha DESC")
    LiveData<List<Competicion>> obtenerTodasLiveData();

    // --- VALIDACIONES PARA EL REPOSITORIO ---

    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplina(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Competicion WHERE Nombre = :nombre)")
    boolean existeNombre(String nombre);
}