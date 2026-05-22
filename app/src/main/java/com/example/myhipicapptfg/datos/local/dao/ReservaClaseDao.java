package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.model.ClaseUIModel;

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





    @Query("SELECT " +
            "c.ID_Clase AS idClase, c.Disciplina AS disciplina, c.Nivel AS nivel, " +
            "c.Fecha AS fecha, c.Hora_Inicio AS horaInicio, c.Hora_Fin AS horaFin, " +
            "u.Nombre AS nombreProfesor, p.Nombre AS nombrePista, " +
            "COUNT(r.ID_Alumno) AS inscritos " +
            "FROM Clase c " +
            "LEFT JOIN Usuario u ON u.ID_Usuario = c.ID_Profesor " +
            "LEFT JOIN Pista   p ON p.ID_Pista   = c.ID_Pista " +
            "LEFT JOIN ReservaClase r ON r.ID_Clase = c.ID_Clase " +
            "WHERE c.Fecha BETWEEN :inicioDia AND :finDia " +
            "AND ( " +
            "  (c.Disciplina = 'Doma'  AND c.Nivel = :nivelDoma  AND :practicaDoma  = 1) OR " +
            "  (c.Disciplina = 'Salto' AND c.Nivel = :nivelSalto AND :practicaSalto = 1) " +
            ") " +
            "GROUP BY c.ID_Clase " +
            "ORDER BY c.Hora_Inicio ASC")
    LiveData<List<ClaseUIModel>> obtenerClasesUI(
            long inicioDia, long finDia,
            String nivelDoma, int practicaDoma,
            String nivelSalto, int practicaSalto
    );

    // Variante para "Mis Reservas" (filtrado por alumno)
    @Query("SELECT " +
            "c.ID_Clase        AS idClase, " +
            "c.Disciplina      AS disciplina, " +
            "c.Nivel           AS nivel, " +
            "c.Fecha           AS fecha, " +
            "c.Hora_Inicio     AS horaInicio, " +
            "c.Hora_Fin        AS horaFin, " +
            "u.Nombre          AS nombreProfesor, " +
            "p.Nombre          AS nombrePista, " +
            "COUNT(r2.ID_Alumno) AS inscritos " +
            "FROM Clase c " +
            "INNER JOIN ReservaClase r ON r.ID_Clase = c.ID_Clase AND r.ID_Alumno = :idAlumno " +
            "LEFT JOIN Usuario u  ON u.ID_Usuario = c.ID_Profesor " +
            "LEFT JOIN Pista   p  ON p.ID_Pista   = c.ID_Pista " +
            "LEFT JOIN ReservaClase r2 ON r2.ID_Clase = c.ID_Clase " +
            "GROUP BY c.ID_Clase " +
            "ORDER BY c.Fecha ASC")
    LiveData<List<ClaseUIModel>> obtenerClasesReservadasUI(int idAlumno);





}