package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.model.MovimientoConNota;

import java.util.List;

@Dao
public interface NotaMovimientoDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarNotaMovimiento(NotaMovimiento notaMovimiento);

    @Update
    int actualizarNotaMovimiento(NotaMovimiento notaMovimiento);

    @Delete
    int eliminarNotaMovimiento(NotaMovimiento notaMovimiento);

    @Query("SELECT * FROM Nota_Movimiento")
    LiveData<List<NotaMovimiento>> obtenerTodasNotas();

    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    LiveData<List<NotaMovimiento>> obtenerPorParticipacion(int idParticipacion);

    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Movimiento = :idMovimiento")
    LiveData<List<NotaMovimiento>> obtenerPorMovimiento(int idMovimiento);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(NotaMovimiento nota);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateAll(List<NotaMovimiento> notas);

    @Query("SELECT SUM(Nota) FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    double totalParticipacionSync(int idParticipacion);

    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    LiveData<List<NotaMovimiento>> getNotasByParticipacion(int idParticipacion);

    @Query("SELECT * FROM Nota_Movimiento WHERE ID_Participacion = :idParticipacion")
    List<NotaMovimiento> getNotasByParticipacionSync(int idParticipacion);

    @Query("SELECT " +
            "  nm.ID_Movimiento    AS idMovimiento, " +
            "  nm.ID_Participacion AS idParticipacion, " +
            "  m.Letra             AS letra, " +
            "  m.Ejercicio         AS ejercicio, " +
            "  m.Coeficiente       AS coeficiente, " +
            "  m.Directriz         AS directriz, " +
            "  m.Orden             AS orden, " +
            "  nm.Nota             AS nota, " +
            "  nm.Observacion      AS observacion " +
            "FROM Nota_Movimiento nm " +
            "INNER JOIN Movimiento m ON nm.ID_Movimiento = m.ID_Movimiento " +
            "WHERE nm.ID_Participacion = :idParticipacion " +
            "ORDER BY m.Orden ASC")
    LiveData<List<MovimientoConNota>> getHojaCalificaciones(int idParticipacion);
}