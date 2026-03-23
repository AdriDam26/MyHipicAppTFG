package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.RutaPersonal;
import java.util.List;

@Dao
public interface RutaPersonalDao {

    @Insert
    long insertar(RutaPersonal ruta);

    @Update
    void actualizar(RutaPersonal ruta);

    @Delete
    void eliminar(RutaPersonal ruta);

    // Para ver todas las rutas en un RecyclerView (se actualiza solo)
    @Query("SELECT * FROM RutaPersonal ORDER BY ID_Ruta_Personal DESC")
    LiveData<List<RutaPersonal>> obtenerTodas();

    // Para buscar el ID del dueño por su nombre (Usa el JOIN con Usuario)
    @Query("SELECT u.ID_Usuario FROM Usuario u WHERE u.Nombre = :nombreUsuario LIMIT 1")
    int buscarIdPorNombre(String nombreUsuario);

    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Propietario')")
    boolean esPropietarioValido(int id);
}