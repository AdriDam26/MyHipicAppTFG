package com.example.myhipicapptfg.datos.local.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import java.util.List;


/**
 * DAO que gestiona las operaciones
 * de la tabla CoordenadaRuta en la base de datos Room.
 *
 * Esta tabla almacena los puntos GPS de una ruta personal.
 */
@Dao
public interface CoordenadaRutaDao {

    /**
     * Inserta una única coordenada (un punto GPS) en la base de datos.
     */
    @Insert
    void insertar(CoordenadaRuta coordenada);

    /**
     * Inserta una lista completa de coordenadas.
     */
    @Insert
    void insertarLista(List<CoordenadaRuta> coordenadas);


    /**
     * Actualiza una coordenada existente.
     * Se utiliza si se necesita modificar algún punto guardado.
     */
    @Update
    void actualizar(CoordenadaRuta coordenada);

    /**
     * Elimina una coordenada concreta de la base de datos.
     */
    @Delete
    void eliminar(CoordenadaRuta coordenada);

    /**
     * Obtiene todas las coordenadas de una ruta específica.
     *
     * IMPORTANTE:
     * - Se filtra por ID_Ruta_Personal
     * - Se ordena por el campo "Orden" para reconstruir correctamente el recorrido
     *
     * LiveData permite que la UI se actualice automáticamente si los datos cambian.
     */
    @Query("SELECT * FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta ORDER BY Orden ASC")
    LiveData<List<CoordenadaRuta>> obtenerPorRuta(int idRuta);


    /**
     * Elimina todas las coordenadas pertenecientes a una ruta concreta.
     * Se usa cuando se borra una ruta completa del sistema.
     */
    @Query("DELETE FROM CoordenadaRuta WHERE ID_Ruta_Personal = :idRuta")
    void eliminarPorRuta(int idRuta);
}
