package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.CoordenadaRuta;

import java.util.List;

@Dao
public interface CoordenadaRutaDao {

    // 🔹 INSERT (una coordenada)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarCoordenada(CoordenadaRuta coordenada);

    // 🔹 INSERT LISTA (trayecto completo GPS)
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insertarListaCoordenadas(List<CoordenadaRuta> coordenadas);

    // 🔹 UPDATE
    @Update
    int actualizarCoordenada(CoordenadaRuta coordenada);

    // 🔹 DELETE (una coordenada)
    @Delete
    int eliminarCoordenada(CoordenadaRuta coordenada);

    // 🔹 OBTENER RUTA ORDENADA (para dibujar mapa)
    @Query("SELECT * FROM CoordenadaRuta " +
            "WHERE ID_Ruta_Personal = :idRuta " +
            "ORDER BY Orden ASC")
    LiveData<List<CoordenadaRuta>> obtenerPorRuta(int idRuta);

    // 🔹 VALIDAR QUE EXISTE LA RUTA PADRE
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM RutaPersonal " +
            "WHERE ID_Ruta_Personal = :idRuta)")
    int existeRuta(int idRuta);

    // 🔹 BORRAR TODAS LAS COORDENADAS DE UNA RUTA
    @Query("DELETE FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta")
    void eliminarPorRuta(int idRuta);
}