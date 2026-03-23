package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.myhipicapptfg.entities.Pista;
import java.util.List;

@Dao
public interface PistaDao {

    @Insert
    long insertarPista(Pista pista);

    @Update
    void actualizarPista(Pista pista);

    @Delete
    void eliminarPista(Pista pista);

    @Query("SELECT * FROM Pista")
    LiveData<List<Pista>> obtenerTodasPistas();

    @Query("SELECT * FROM Pista WHERE ID_Pista = :id LIMIT 1")
    LiveData<Pista> buscarPorId(int id);

    @Query("SELECT * FROM Pista WHERE Nombre = :nombre LIMIT 1")
    Pista buscarPorNombreSync(String nombre);

    @Query("SELECT * FROM Pista WHERE Estado = :estado")
    LiveData<List<Pista>> obtenerPistasPorEstado(String estado);

    @Query("SELECT COUNT(*) FROM Pista")
    LiveData<Integer> contarPistas();
}