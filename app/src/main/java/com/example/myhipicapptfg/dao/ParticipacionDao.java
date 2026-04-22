package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Participacion;

import java.util.List;

@Dao
public interface ParticipacionDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarParticipacion(Participacion participacion);

    // 🔹 UPDATE
    @Update
    int actualizarParticipacion(Participacion participacion);

    // 🔹 DELETE
    @Delete
    int eliminarParticipacion(Participacion participacion);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Participacion")
    LiveData<List<Participacion>> obtenerTodasParticipaciones();

    // 🔹 POR PRUEBA
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Prueba = :idPrueba " +
            "ORDER BY Posicion ASC")
    LiveData<List<Participacion>> obtenerPorPrueba(int idPrueba);

    // 🔹 POR ALUMNO
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Alumno = :idAlumno")
    LiveData<List<Participacion>> obtenerPorAlumno(int idAlumno);

    // 🔹 POR EQUINO
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Equino = :idEquino")
    LiveData<List<Participacion>> obtenerPorEquino(int idEquino);

    // 🔹 DETALLE (UI)
    @Query("SELECT * FROM Participacion " +
            "WHERE ID_Participacion = :id LIMIT 1")
    LiveData<Participacion> buscarPorId(int id);

    // ----------------------------------------------------
    // 🔹 MÉTODOS SYNC (validaciones / lógica negocio)
    // ----------------------------------------------------

    // ✔ comprobar si ya está inscrito en la prueba
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Participacion " +
            "WHERE ID_Alumno = :idAlumno AND ID_Prueba = :idPrueba)")
    int yaParticipaSync(int idAlumno, int idPrueba);

    // ✔ contar participantes en una prueba
    @Query("SELECT COUNT(*) FROM Participacion WHERE ID_Prueba = :idPrueba")
    int contarParticipantesSync(int idPrueba);
}