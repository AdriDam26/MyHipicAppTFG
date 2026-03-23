package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;

import com.example.myhipicapptfg.entities.Cuadra;
import java.util.List;

@Dao
public interface CuadraDao {

    @Insert
    long insertarCuadra(Cuadra cuadra);

    @Query("SELECT * FROM Cuadra")
    LiveData<List<Cuadra>> obtenerTodas();

    @Query("SELECT * FROM Cuadra WHERE Numero_Cuadra = :numero LIMIT 1")
    Cuadra obtenerPorNumero(int numero);

    @Query("SELECT * FROM Cuadra WHERE ID_Cuadra = :idCuadra LIMIT 1")
    Cuadra obtenerPorId(int idCuadra);

    @Update
    void actualizarCuadra(Cuadra cuadra);

    @Delete
    void eliminarCuadra(Cuadra cuadra);

    @Query("SELECT COUNT(*) FROM Cuadra")
    int contarCuadras();

    // Esta la usaremos en el Repositorio para evitar duplicados del número único
    @Query("SELECT EXISTS(SELECT 1 FROM Cuadra WHERE Numero_Cuadra = :numero)")
    boolean existeNumero(int numero);
}
