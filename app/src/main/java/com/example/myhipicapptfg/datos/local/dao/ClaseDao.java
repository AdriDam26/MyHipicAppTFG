package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Clase;

import java.util.List;

/**
 * DAO de Clase.
 *
 * Gestiona el acceso a datos de las clases de equitación,
 * incluyendo operaciones CRUD y consultas específicas por profesor.
 */
@Dao
public interface ClaseDao {


    /**
     * Inserta una nueva clase.
     * @return ID generado de la clase.
     */
    @Insert
    long insertarClase(Clase clase);


    @Update
    int actualizarClase(Clase clase);


    @Delete
    int eliminarClase(Clase clase);

    /**
     * Obtiene todas las clases registradas.
     */
    @Query("SELECT * FROM Clase")
    LiveData<List<Clase>> obtenerTodasClases();


    /**
     * Busca una clase por su ID (reactivo).
     */
    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    LiveData<Clase> buscarPorId(int id);



    /**
     * Verifica si existe una pista con el ID indicado.
     * Se usa para validar integridad antes de insertar una clase.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Pista WHERE ID_Pista = :idPista)")
    boolean existePistaSync(int idPista);

    /**
     * Verifica si existe un profesor con el ID indicado.
     */
    @Query("SELECT EXISTS(SELECT 1 FROM Profesor WHERE ID_Profesor = :idProfesor)")
    boolean existeProfesorSync(int idProfesor);

    /**
     * Obtiene una clase por ID de forma síncrona.
     * Usado normalmente en lógica interna o validaciones.
     */
    @Query("SELECT * FROM Clase WHERE ID_Clase = :id LIMIT 1")
    Clase buscarPorIdSync(int id);

    /**
     * Obtiene todas las clases asignadas a un profesor,
     * ordenadas por fecha y hora de inicio.
     */
    @Query("SELECT * FROM Clase WHERE ID_Profesor = :idProfesor ORDER BY Fecha ASC, Hora_Inicio ASC")
    LiveData<List<Clase>> obtenerClasesPorProfesor(int idProfesor);
}