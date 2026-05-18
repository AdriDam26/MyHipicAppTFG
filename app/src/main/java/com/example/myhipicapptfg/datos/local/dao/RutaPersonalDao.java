package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;

import java.util.List;

@Dao
public interface RutaPersonalDao {

    @Insert
    long insertar(RutaPersonal ruta);

    @Insert
    void insertarCoordenadas(List<CoordenadaRuta> coordenadas);

    @Update
    void actualizar(RutaPersonal ruta);

    @Delete
    void eliminar(RutaPersonal ruta);

    @Query("SELECT * FROM RutaPersonal ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerTodas();

    @Query("SELECT * FROM RutaPersonal WHERE ID_Propietario = :idPropietario ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario);
}