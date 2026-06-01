package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.model.PruebaConCompeticion;

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

    @Delete
    void eliminarMovimientos(List<Movimiento> movimientos);;

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
    default void actualizarPruebaConMovimientos(Prueba prueba,
                                                List<Movimiento> aActualizar,
                                                List<Movimiento> aInsertar,
                                                List<Movimiento> aEliminar) {
        actualizarPrueba(prueba);
        if (!aEliminar.isEmpty())   eliminarMovimientos(aEliminar);
        if (!aActualizar.isEmpty()) actualizarMovimientos(aActualizar);
        if (!aInsertar.isEmpty())   insertarMovimientos(aInsertar);
    }

    @Query("UPDATE Prueba SET Publicado = :publicado WHERE ID_Prueba = :idPrueba")
    void actualizarPublicado(int idPrueba, boolean publicado);

    @Query("SELECT Publicado FROM Prueba WHERE ID_Prueba = :idPrueba")
    LiveData<Boolean> isPublicado(int idPrueba);

    @Query("SELECT " +
            "  p.ID_Prueba        AS idPrueba, " +
            "  p.Nombre           AS nombrePrueba, " +
            "  c.Nombre           AS nombreCompeticion, " +
            "  p.Categoria        AS categoria, " +
            "  p.Nivel            AS nivel " +
            "FROM Prueba p " +
            "INNER JOIN Competicion c ON p.ID_Competicion = c.ID_Competicion " +
            "WHERE p.ID_Juez = :idJuez " +
            "ORDER BY c.Fecha ASC, p.Nombre ASC")
    LiveData<List<PruebaConCompeticion>> getPruebasByJuez(int idJuez);
}