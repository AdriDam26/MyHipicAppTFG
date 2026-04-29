package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.myhipicapptfg.entities.CoordenadaRuta;

import java.util.List;

@Dao
public interface CoordenadaRutaDao {

    @Insert
    void insertar(CoordenadaRuta coordenada);

    @Insert
    void insertarLista(List<CoordenadaRuta> coordenadas);

    @Update
    void actualizar(CoordenadaRuta coordenada);

    @Delete
    void eliminar(CoordenadaRuta coordenada);

    @Query("SELECT * FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta ORDER BY Orden ASC")
    LiveData<List<CoordenadaRuta>> obtenerPorRuta(long idRuta);

    @Query("DELETE FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta")
    void eliminarPorRuta(long idRuta);

    @Query("SELECT EXISTS(SELECT 1 FROM RutaPersonal WHERE ID_Ruta_Personal = :idRuta)")
    boolean existeRuta(long idRuta);
}