package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Movimiento;
import com.example.myhipicapptfg.model.MovimientoConNota;

import java.util.List;

@Dao
public interface MovimientoDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarMovimiento(Movimiento movimiento);

    // 🔹 UPDATE
    @Update
    int actualizarMovimiento(Movimiento movimiento);

    // 🔹 DELETE
    @Delete
    int eliminarMovimiento(Movimiento movimiento);

    // 🔹 LISTADO GENERAL (UI)
    @Query("SELECT * FROM Movimiento ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerTodosMovimientos();

    // 🔹 BUSCAR POR ID (UI)
    @Query("SELECT * FROM Movimiento WHERE ID_Movimiento = :id LIMIT 1")
    LiveData<Movimiento> buscarMovimientoPorId(int id);

    // 🔹 LISTAR POR ORDEN (UI)
    @Query("SELECT * FROM Movimiento ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerMovimientosOrdenados();

    // ----------------------------------------------------
    // 🔹 MÉTODOS SYNC (validaciones / repositorio)
    // ----------------------------------------------------

    // ✔ comprobar orden único o existente
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Movimiento WHERE Orden = :orden)")
    int existeOrdenSync(int orden);

    // ✔ contar movimientos
    @Query("SELECT COUNT(*) FROM Movimiento")
    int contarMovimientosSync();

    // Para cargar los movimientos de una prueba de forma síncrona en el ViewModel



    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    LiveData<List<Movimiento>> getMovimientosByPrueba(int idPrueba);

    // Versión síncrona para operaciones en background
    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    List<Movimiento> getMovimientosByPruebaSync(int idPrueba);




}