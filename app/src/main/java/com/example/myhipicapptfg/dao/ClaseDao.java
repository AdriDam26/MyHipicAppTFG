package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import androidx.room.OnConflictStrategy;

import com.example.myhipicapptfg.entities.Clase;
import java.util.List;

@Dao
public interface ClaseDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarClase(Clase clase);

    @Update
    void actualizarClase(Clase clase);

    @Delete
    void eliminarClase(Clase clase);

    @Query("SELECT * FROM Clase")
    LiveData<List<Clase>> obtenerTodasClases();

    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    LiveData<Clase> buscarPorId(int id);

    // --- MÉTODOS SYNC PARA VALIDACIÓN ---

    @Query("SELECT EXISTS(SELECT 1 FROM Pista WHERE ID_Pista = :id)")
    boolean existePistaSync(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Disciplina WHERE ID_Disciplina = :id)")
    boolean existeDisciplinaSync(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Profesor WHERE ID_Profesor = :id)")
    boolean existeProfesorSync(int id);

    @Query("SELECT COUNT(*) FROM Clase")
    LiveData<Integer> contarClases();
}
