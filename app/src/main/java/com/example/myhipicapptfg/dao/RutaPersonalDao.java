package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;

import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.RutaPersonal;

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

    @Transaction
    default long guardarRutaConPuntos(RutaPersonal ruta, List<CoordenadaRuta> puntos) {
        long idRuta = insertar(ruta);

        if (puntos != null) {
            for (CoordenadaRuta p : puntos) {
                p.idRutaPersonal = idRuta;
            }
            insertarCoordenadas(puntos);
        }

        return idRuta;
    }

    @Query("SELECT * FROM RutaPersonal ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerTodas();

    @Query("SELECT * FROM RutaPersonal WHERE ID_Propietario = :idPropietario ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerPorPropietario(long idPropietario);

    @Query("SELECT COUNT(*) > 0 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'propietario'")
    boolean esPropietarioValido(long id);
}