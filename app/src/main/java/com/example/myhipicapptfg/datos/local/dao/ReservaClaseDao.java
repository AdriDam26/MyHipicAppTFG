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
import com.example.myhipicapptfg.model.ClaseModel;

import java.util.List;

/**
 * DAO encargado del acceso a datos relacionados con las reservas de clases.
 *
 * Define todas las operaciones SQL necesarias para:
 * - Insertar y eliminar reservas
 * - Consultar alumnos inscritos en clases
 * - Validaciones de negocio
 * - Obtener clases disponibles y reservadas
 *
 */
@Dao
public interface ReservaClaseDao {

    /**
     * Inserta una nueva reserva en la base de datos.
     *
     * onConflict = IGNORE:
     * si ya existe una reserva con la misma clave primaria compuesta
     * (ID_Alumno + ID_Clase), se ignora la inserción sin lanzar error.
     *
     * Esto evita duplicados cuando el usuario pulsa varias veces.
     *
     * @return ID de la fila insertada o -1 si fue ignorada.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertarReserva(ReservaClase reserva);



    /**
     * Elimina una reserva específica de un alumno en una clase.
     *
     * Al eliminar la fila, la relación desaparece inmediatamente,
     * permitiendo reflejar el cambio en la UI de forma reactiva.
     */
    @Query("DELETE FROM ReservaClase WHERE ID_Alumno = :idAlumno AND ID_Clase = :idClase")
    void cancelarReserva(int idAlumno, int idClase);



    /**
     * Obtiene la lista de alumnos inscritos en una clase concreta.
     *
     * Realiza un JOIN entre Usuario y ReservaClase para recuperar
     * los datos de los alumnos asociados.
     */
    @Query("SELECT u.* FROM Usuario u " +
            "INNER JOIN ReservaClase r ON u.ID_Usuario = r.ID_Alumno " +
            "WHERE r.ID_Clase = :idClase")
    LiveData<List<Usuario>> obtenerAlumnosDeClase(int idClase);



    /**
     * Devuelve el número de alumnos inscritos en una clase.
     */
    @Query("SELECT COUNT(*) FROM ReservaClase WHERE ID_Clase = :idClase")
    int contarAlumnosEnClase(int idClase);

    /**
     * Comprueba si un usuario es un alumno válido.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Usuario WHERE ID_Usuario = :id AND Tipo = 'alumno')")
    boolean esAlumnoValido(int id);

    /**
     * Comprueba si una clase existe en la base de datos.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Clase WHERE ID_Clase = :id)")
    boolean existeClase(int id);

    /**
     * Comprueba si un alumno ya tiene una reserva en una clase.
     *
     * Devuelve TRUE si ya existe la relación.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM ReservaClase WHERE ID_Alumno = :idAlu AND ID_Clase = :idCla)")
    boolean yaEstaReservado(int idAlu, int idCla);





    /**
     * Obtiene las clases en las que un alumno está inscrito.
     *
     * Se filtra por ID de alumno y se recupera información completa
     * de la clase, profesor y pista.
     */
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
    LiveData<List<ClaseModel>> obtenerClasesReservadas(int idAlumno);

    /**
     * Consulta que obtiene las clases disponibles para un alumno concreto en un rango de fechas.
     * Filtra las clases según el nivel y la disciplina del alumno (Doma o Salto),
     * incluye información del profesor, la pista y el número de alumnos inscritos,
     * y devuelve los resultados ordenados por hora de inicio.
     */
    @Query("SELECT " +
            "c.ID_Clase AS idClase, c.Disciplina AS disciplina, c.Nivel AS nivel, " +
            "c.Fecha AS fecha, c.Hora_Inicio AS horaInicio, c.Hora_Fin AS horaFin, " +
            "u.Nombre AS nombreProfesor, p.Nombre AS nombrePista, " +
            "COUNT(r.ID_Alumno) AS inscritos " +
            "FROM Clase c " +
            "LEFT JOIN Usuario u ON u.ID_Usuario = c.ID_Profesor " +
            "LEFT JOIN Pista p ON p.ID_Pista = c.ID_Pista " +
            "LEFT JOIN ReservaClase r ON r.ID_Clase = c.ID_Clase " +
            "INNER JOIN Alumno a ON a.ID_Alumno = :idAlumno " +
            "WHERE c.Fecha BETWEEN :inicioDia AND :finDia " +
            "AND ( " +
            "  (c.Disciplina = 'Doma'  AND c.Nivel = a.Nivel_Doma  AND a.Practica_Doma  = 1) OR " +
            "  (c.Disciplina = 'Salto' AND c.Nivel = a.Nivel_Salto AND a.Practica_Salto = 1) " +
            ") " +
            "GROUP BY c.ID_Clase " +
            "ORDER BY c.Hora_Inicio ASC")
    LiveData<List<ClaseModel>> obtenerClasesPorAlumnoYFecha(
            int idAlumno, long inicioDia, long finDia
    );


}