package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.ReservaClase;

import java.util.List;

@Dao
public interface ReservaClaseDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarReserva(ReservaClase reserva);

    // 🔹 UPDATE
    @Update
    int actualizarReserva(ReservaClase reserva);

    // 🔹 DELETE
    @Delete
    int eliminarReserva(ReservaClase reserva);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM ReservaClase")
    LiveData<List<ReservaClase>> obtenerTodasReservas();

    // 🔹 VER SI YA ESTÁ RESERVADO
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM ReservaClase " +
            "WHERE ID_Alumno = :idAlu AND ID_Clase = :idCla)")
    int yaEstaReservado(int idAlu, int idCla);

    // 🔹 CONTAR ALUMNOS EN UNA CLASE
    @Query("SELECT COUNT(*) FROM ReservaClase WHERE ID_Clase = :idClase")
    int contarAlumnosEnClase(int idClase);

    // 🔹 VALIDAR ALUMNO
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Usuario " +
            "WHERE ID_Usuario = :id AND Tipo = 'alumno')")
    int esAlumnoValido(int id);

    // 🔹 VALIDAR CLASE
    @Query("SELECT EXISTS(" +
            "SELECT 1 FROM Clase " +
            "WHERE ID_Clase = :id)")
    int existeClase(int id);
}