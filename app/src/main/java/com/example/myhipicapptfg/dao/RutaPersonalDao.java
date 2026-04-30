package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.RutaPersonal;
import java.util.List;

@Dao
public abstract class RutaPersonalDao {

    @Insert
    public abstract long insertar(RutaPersonal ruta);

    @Insert
    public abstract void insertarCoordenadas(List<CoordenadaRuta> coordenadas);

    @Update
    public abstract void actualizar(RutaPersonal ruta);

    @Delete
    public abstract void eliminar(RutaPersonal ruta);

    /**
     * Este método es la clave. Ejecuta ambas inserciones en una sola transacción.
     */
    @Transaction
    public long guardarRutaConPuntos(RutaPersonal ruta, List<CoordenadaRuta> puntos) {
        // 1. Insertamos la ruta y obtenemos el ID generado
        long idRuta = insertar(ruta);

        // 2. Asignamos ese ID a cada coordenada de la lista
        if (puntos != null && !puntos.isEmpty()) {
            for (CoordenadaRuta p : puntos) {
                p.idRutaPersonal = (int) idRuta;
            }
            // 3. Insertamos todos los puntos
            insertarCoordenadas(puntos);
        }
        return idRuta;
    }

    @Query("SELECT * FROM RutaPersonal ORDER BY ID_Ruta_Personal DESC")
    public abstract LiveData<List<RutaPersonal>> obtenerTodas();

    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'propietario')")
    public abstract boolean esPropietarioValido(int id);

    @Query("SELECT * FROM RutaPersonal WHERE ID_Propietario = :idPropietario ORDER BY ID_Ruta_Personal DESC")
    public abstract LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario);
}