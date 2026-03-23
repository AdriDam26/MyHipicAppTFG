package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.CoordenadaRuta;
import java.util.List;

@Dao
public interface CoordenadaRutaDao {

    @Insert
    void insertar(CoordenadaRuta coordenada);

    // Muy útil para guardar el trayecto completo del GPS de una vez
    @Insert
    void insertarLista(List<CoordenadaRuta> coordenadas);

    @Update
    void actualizar(CoordenadaRuta coordenada);

    @Delete
    void eliminar(CoordenadaRuta coordenada);

    // Obtener puntos de una ruta ordenados para poder dibujar la línea en el mapa
    @Query("SELECT * FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta ORDER BY Orden ASC")
    LiveData<List<CoordenadaRuta>> obtenerPorRuta(int idRuta);

    // Verificación de seguridad: ¿Existe la ruta padre?
    @Query("SELECT EXISTS(SELECT 1 FROM RutaPersonal WHERE ID_Ruta_Personal = :idRuta)")
    boolean existeRuta(int idRuta);

    @Query("DELETE FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta")
    void eliminarPorRuta(int idRuta);
}
