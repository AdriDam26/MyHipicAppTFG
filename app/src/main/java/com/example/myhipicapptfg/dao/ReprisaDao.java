package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Reprisa;
import java.util.List;

@Dao
public interface ReprisaDao {

    @Insert
    long insertarReprisa(Reprisa reprisa);

    @Update
    void actualizarReprisa(Reprisa reprisa);

    @Delete
    void eliminarReprisa(Reprisa reprisa);

    @Query("SELECT * FROM Reprisa WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    LiveData<List<Reprisa>> obtenerPorPlantilla(int idPrueba);

    // Verificaciones para el Repositorio
    @Query("SELECT EXISTS(SELECT 1 FROM PlantillaPrueba WHERE ID_Prueba = :id)")
    boolean existePlantilla(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Reprisa WHERE ID_Prueba = :idPrueba AND Orden = :orden)")
    boolean existeOrdenEnPlantilla(int idPrueba, int orden);
}