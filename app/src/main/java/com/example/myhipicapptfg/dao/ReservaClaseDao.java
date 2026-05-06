package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.ReservaClase;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;

@Dao
public interface ReservaClaseDao {

    // Cambiado a IGNORE para que si el usuario pulsa muchas veces seguidas
    // no de error de Primary Key, simplemente no inserte el duplicado.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertarReserva(ReservaClase reserva);

    @Update
    int actualizarReserva(ReservaClase reserva);

    @Delete
    int eliminarReserva(ReservaClase reserva);

    // --- ELIMINAR RESERVA (Lógica Simplificada) ---
    // Borramos la fila para que 'yaEstaReservado' pase a ser false inmediatamente.
    @Query("DELETE FROM ReservaClase WHERE ID_Alumno = :idAlumno AND ID_Clase = :idClase")
    void cancelarReserva(int idAlumno, int idClase);

    // --- CONSULTAS DE DATOS ---

    @Query("SELECT * FROM ReservaClase WHERE ID_Alumno = :idAlumno")
    LiveData<List<ReservaClase>> obtenerReservasPorAlumno(int idAlumno);

    // Corregido: Se añadió un espacio antes de ORDER BY para evitar error de sintaxis
    @Query("SELECT c.* FROM Clase c " +
            "INNER JOIN ReservaClase r ON c.ID_Clase = r.ID_Clase " +
            "WHERE r.ID_Alumno = :idAlumno " +
            "ORDER BY c.Fecha ASC")
    LiveData<List<Clase>> obtenerClasesReservadasPorAlumno(int idAlumno);

    @Query("SELECT u.* FROM Usuario u " +
            "INNER JOIN ReservaClase r ON u.ID_Usuario = r.ID_Alumno " +
            "WHERE r.ID_Clase = :idClase")
    LiveData<List<Usuario>> obtenerAlumnosDeClase(int idClase);

    // --- VALIDACIONES ---

    @Query("SELECT COUNT(*) FROM ReservaClase WHERE ID_Clase = :idClase")
    int contarAlumnosEnClase(int idClase);

    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'alumno')")
    boolean esAlumnoValido(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM Clase WHERE ID_Clase = :id)")
    boolean existeClase(int id);

    @Query("SELECT EXISTS(SELECT 1 FROM ReservaClase WHERE ID_Alumno = :idAlu AND ID_Clase = :idCla)")
    boolean yaEstaReservado(int idAlu, int idCla);

    // --- FILTRADO DE CLASES RECOMENDADAS ---

    @Query("SELECT * FROM Clase WHERE " +
            "( (Disciplina = 'Doma' AND Nivel = :nivelDoma AND :practicaDoma = 1) OR " +
            "  (Disciplina = 'Salto' AND Nivel = :nivelSalto AND :practicaSalto = 1) ) " +
            "AND Fecha BETWEEN :inicioDia AND :finDia " +
            "ORDER BY Hora_Inicio ASC")
    LiveData<List<Clase>> obtenerClases(
            String nivelDoma,
            int practicaDoma,
            String nivelSalto,
            int practicaSalto,
            long inicioDia,
            long finDia
    );



    // Para contar los inscritos en tiempo real
    @Query("SELECT * FROM ReservaClase")
    LiveData<List<ReservaClase>> obtenerTodasLasReservas();





}