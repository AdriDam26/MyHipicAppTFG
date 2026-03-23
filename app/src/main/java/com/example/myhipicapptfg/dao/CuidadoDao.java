package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Cuidado;
import java.util.List;

@Dao
public interface CuidadoDao {

    @Insert
    void insertar(Cuidado cuidado);

    @Update
    void actualizar(Cuidado cuidado);

    @Delete
    void eliminar(Cuidado cuidado);

    @Query("SELECT * FROM Cuidado ORDER BY Fecha DESC")
    LiveData<List<Cuidado>> obtenerTodos();

    @Query("SELECT * FROM Cuidado WHERE ID_Equino = :idEquino ORDER BY Fecha DESC")
    LiveData<List<Cuidado>> obtenerPorEquino(int idEquino);

    // Verificación para el Repositorio
    @Query("SELECT EXISTS(SELECT 1 FROM Equino WHERE ID_Equino = :id)")
    boolean existeEquino(int id);

    @Query("SELECT * FROM Cuidado WHERE Proxima_Revision IS NOT NULL AND Proxima_Revision >= date('now') ORDER BY Proxima_Revision ASC")
    LiveData<List<Cuidado>> obtenerProximasRevisiones();
}
