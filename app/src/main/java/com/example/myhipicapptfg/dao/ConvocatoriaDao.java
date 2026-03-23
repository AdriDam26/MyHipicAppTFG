package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Convocatoria;
import java.util.List;

@Dao
public interface ConvocatoriaDao {

    @Insert
    long insertarConvocatoria(Convocatoria convocatoria);

    @Update
    void actualizarConvocatoria(Convocatoria convocatoria);

    @Delete
    void eliminarConvocatoria(Convocatoria convocatoria);

    @Query("SELECT * FROM Convocatoria WHERE ID_Competicion = :idComp")
    LiveData<List<Convocatoria>> obtenerPorCompeticionLiveData(int idComp);

    // --- VALIDACIONES ---
    @Query("SELECT EXISTS(SELECT 1 FROM Competicion WHERE ID_Competicion = :id)")
    boolean existeCompeticion(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM PlantillaPrueba WHERE ID_Prueba = :id)")
    boolean existePrueba(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Convocatoria WHERE ID_Competicion = :idComp AND Categoria = :cat)")
    boolean existeCategoriaEnCompeticion(int idComp, String cat);
}
