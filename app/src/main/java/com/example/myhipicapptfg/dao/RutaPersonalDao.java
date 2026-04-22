package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.RutaPersonal;

import java.util.List;

@Dao
public interface RutaPersonalDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarRuta(RutaPersonal ruta);

    // 🔹 UPDATE
    @Update
    int actualizarRuta(RutaPersonal ruta);

    // 🔹 DELETE
    @Delete
    int eliminarRuta(RutaPersonal ruta);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM RutaPersonal ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerTodasRutas();

    // 🔹 BUSCAR ID USUARIO POR NOMBRE
    @Query("SELECT ID_Usuario FROM Usuario WHERE Nombre = :nombre LIMIT 1")
    int buscarIdPorNombre(String nombre);

    // 🔹 VALIDAR SI ES PROPIETARIO
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Usuario " +
            "WHERE ID_Usuario = :id AND Tipo = 'propietario')")
    int esPropietarioValido(int id);
}