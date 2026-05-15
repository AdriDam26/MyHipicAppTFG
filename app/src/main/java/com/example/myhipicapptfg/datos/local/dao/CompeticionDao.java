package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Competicion;

import java.util.List;

@Dao
public interface CompeticionDao {

    @Insert
    long insertarCompeticion(Competicion c);

    @Update
    void actualizarCompeticion(Competicion c);

    @Delete
    void eliminarCompeticion(Competicion c);

    @Query("SELECT * FROM Competicion ORDER BY Fecha DESC")
    LiveData<List<Competicion>> obtenerTodas();

    @Query("SELECT * FROM Competicion WHERE ID_Competicion = :id")
    LiveData<Competicion> buscarPorId(int id);

    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE Nombre = :nombre")
    boolean existeNombreSync(String nombre);

    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE Nombre = :nombre AND ID_Competicion != :idExcluir")
    boolean existeNombreExcluyendoSync(String nombre, int idExcluir);
}