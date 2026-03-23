package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.PlantillaPrueba;
import java.util.List;

@Dao
public interface PlantillaPruebaDao {

    @Insert
    long insertarPrueba(PlantillaPrueba prueba);

    @Update
    void actualizarPrueba(PlantillaPrueba prueba);

    @Delete
    void eliminarPrueba(PlantillaPrueba prueba);

    @Query("SELECT * FROM PlantillaPrueba")
    LiveData<List<PlantillaPrueba>> obtenerTodas();

    // Verificación para el Repositorio
    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplina(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM PlantillaPrueba WHERE Nombre = :nombre AND ID_Disciplina = :idDisc)")
    boolean existeNombreEnDisciplina(String nombre, int idDisc);
}