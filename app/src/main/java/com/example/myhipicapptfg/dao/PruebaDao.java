package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Movimiento;
import com.example.myhipicapptfg.entities.Prueba;

import java.util.List;

@Dao
public interface PruebaDao {

    @Insert
    long insertarPrueba(Prueba prueba);

    @Update
    void actualizarPrueba(Prueba prueba);

    @Delete
    void eliminarPrueba(Prueba prueba);

    @Query("SELECT * FROM Prueba ORDER BY Nombre ASC")
    LiveData<List<Prueba>> obtenerTodasPruebas();

    @Query("SELECT * FROM Prueba WHERE ID_Prueba = :id")
    LiveData<Prueba> buscarPruebaPorId(int id);

    @Query("SELECT * FROM Prueba WHERE ID_Competicion = :idCompeticion ORDER BY Nombre ASC")
    LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion);

    @Query("SELECT COUNT(*) > 0 FROM Prueba WHERE Nombre = :nombre")
    boolean existeNombreSync(String nombre);

    @Query("SELECT COUNT(*) > 0 FROM Prueba WHERE Nombre = :nombre AND ID_Prueba != :idExcluir")
    boolean existeNombreExcluyendoSync(String nombre, int idExcluir);

    @Query("SELECT COUNT(*) > 0 FROM Competicion WHERE ID_Competicion = :idCompeticion")
    boolean existeCompeticionSync(int idCompeticion);

    @Insert
    void insertarMovimientos(List<Movimiento> movimientos);

    @Update
    void actualizarMovimientos(List<Movimiento> movimientos);

    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    LiveData<List<Movimiento>> obtenerMovimientosPorPrueba(int idPrueba);

    @Query("SELECT * FROM Movimiento WHERE ID_Prueba = :idPrueba ORDER BY Orden ASC")
    List<Movimiento> obtenerMovimientosPorPruebaSync(int idPrueba);

    @Query("DELETE FROM Movimiento WHERE ID_Prueba = :idPrueba")
    void eliminarMovimientosDePrueba(int idPrueba);

    @Transaction
    default long insertarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        long idPrueba = insertarPrueba(prueba);
        for (Movimiento m : movimientos) {
            m.idPrueba = (int) idPrueba;
        }
        insertarMovimientos(movimientos);
        return idPrueba;
    }

    @Transaction
    default void actualizarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        actualizarPrueba(prueba);
        eliminarMovimientosDePrueba(prueba.idPrueba);
        for (Movimiento m : movimientos) {
            m.idPrueba    = prueba.idPrueba;
            m.idMovimiento = 0;
        }
        insertarMovimientos(movimientos);
    }
}