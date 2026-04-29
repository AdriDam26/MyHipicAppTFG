package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.NotaMovimiento;

import java.util.List;

@Dao
public interface NotaMovimientoDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarNotaMovimiento(NotaMovimiento notaMovimiento);

    // 🔹 UPDATE
    @Update
    int actualizarNotaMovimiento(NotaMovimiento notaMovimiento);

    // 🔹 DELETE
    @Delete
    int eliminarNotaMovimiento(NotaMovimiento notaMovimiento);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Nota_Movimiento")
    LiveData<List<NotaMovimiento>> obtenerTodasNotas();

    // 🔹 POR PARTICIPACIÓN (resultado de un jinete + caballo)
    @Query("SELECT * FROM Nota_Movimiento " +
            "WHERE ID_Participacion = :idParticipacion")
    LiveData<List<NotaMovimiento>> obtenerPorParticipacion(int idParticipacion);

    // 🔹 POR MOVIMIENTO (todos los jinetes en un ejercicio)
    @Query("SELECT * FROM Nota_Movimiento " +
            "WHERE ID_Movimiento = :idMovimiento")
    LiveData<List<NotaMovimiento>> obtenerPorMovimiento(int idMovimiento);

    // 🔹 POR JUEZ


    // ✔ calcular puntuación total de una participación
    @Query("SELECT SUM(Nota) FROM Nota_Movimiento " +
            "WHERE ID_Participacion = :idParticipacion")
    double totalParticipacionSync(int idParticipacion);
}