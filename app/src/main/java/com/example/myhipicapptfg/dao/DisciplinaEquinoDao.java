package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.DisciplinaEquino;
import java.util.List;

@Dao
public interface DisciplinaEquinoDao {

    @Insert
    void insertar(DisciplinaEquino de);

    @Delete
    void eliminar(DisciplinaEquino de);

    @Query("SELECT * FROM Disciplina_Equino")
    LiveData<List<DisciplinaEquino>> obtenerTodas();

    // Verificación 1: ¿Existe el Equino?
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE ID_Equino = :id)")
    boolean existeEquino(int id);

    // Verificación 2: ¿Existe la Disciplina?
    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplina(int id);

    @Query("SELECT * FROM Disciplina_Equino WHERE ID_Equino = :idEquino")
    List<DisciplinaEquino> obtenerPorEquino(int idEquino);
}