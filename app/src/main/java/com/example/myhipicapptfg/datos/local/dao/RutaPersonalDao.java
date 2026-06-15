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

/**
 * DAO encargado de gestionar las operaciones de la tabla RutaPersonal
 * y también parte de sus coordenadas asociadas.
 *
 * Aquí se manejan las rutas completas creadas por los usuarios.
 */
@Dao
public interface RutaPersonalDao {

    /**
     * Inserta una nueva ruta en la base de datos.
     */
    @Insert
    long insertarRuta(RutaPersonal ruta);

    /**
     * Inserta una lista de coordenadas asociadas a una ruta.
     * Esto permite guardar todo el recorrido GPS de una sola vez.
     */
    @Insert
    void insertarCoordenadas(List<CoordenadaRuta> coordenadas);

    /**
     * Actualiza los datos de una ruta existente.
     */
    @Update
    void actualizarRuta(RutaPersonal ruta);

    /**
     * Elimina una ruta concreta de la base de datos.
     */
    @Delete
    void eliminarRuta(RutaPersonal ruta);


    /**
     * Obtiene todas las rutas de un usuario concreto (propietario).
     *
     * Permite filtrar las rutas por usuario.
     */
    @Query("SELECT * FROM RutaPersonal WHERE ID_Propietario = :idPropietario ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario);
}