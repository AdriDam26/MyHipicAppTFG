package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Equino;
import java.util.List;

@Dao
public interface EquinoDao {

    @Insert
    long insertar(Equino equino);

    @Update
    void actualizar(Equino equino);

    @Delete
    void eliminar(Equino equino);

    @Query("SELECT * FROM Equino ORDER BY Nombre ASC")
    LiveData<List<Equino>> obtenerTodos();

    // Verificación 1: ¿Existe la Cuadra?
    @Query("SELECT EXISTS(SELECT 1 FROM Cuadra WHERE ID_Cuadra = :id)")
    boolean existeCuadra(int id);

    // Verificación 2: ¿Existe el Propietario (y es de tipo Propietario)?
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'Propietario')")
    boolean esPropietarioValido(int id);

    // Verificación 3: ¿El microchip ya está registrado? (Para evitar crashes por el Index UNIQUE)
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE Numero_Microchip = :microchip)")
    boolean existeMicrochip(String microchip);
}
