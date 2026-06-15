package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Pista;

import java.util.List;

/**
 * DAO de Pista.
 *
 * Gestiona el acceso a datos de las pistas del sistema hípico,
 * incluyendo operaciones CRUD y consultas básicas.
 */
@Dao
public interface PistaDao {

    /**
     * Inserta una nueva pista.
     * Si hay conflicto (nombre duplicado), se aborta la operación.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarPista(Pista pista);


    @Update
    int actualizarPista(Pista pista);


    @Delete
    int eliminarPista(Pista pista);

    /**
     * Obtiene todas las pistas registradas.
     */
    @Query("SELECT * FROM Pista")
    LiveData<List<Pista>> obtenerTodasPistas();

    /**
     * Busca una pista por su ID.
     */
    @Query("SELECT * FROM Pista WHERE ID_Pista = :id LIMIT 1")
    LiveData<Pista> buscarPorId(int id);

    /**
     * Cuenta el número total de pistas registradas.
     */
    @Query("SELECT * FROM Pista WHERE Nombre = :nombre LIMIT 1")
    Pista buscarPorNombreSync(String nombre);


}