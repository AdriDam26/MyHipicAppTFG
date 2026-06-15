package com.example.myhipicapptfg.datos.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;


/**
 * DAO de Usuario.
 *
 * Define todas las operaciones de acceso a datos relacionadas con:
 * - Usuario
 * - y sus subtipos: Alumno, Profesor y Juez
 *
 * Utiliza Room como capa de persistencia.
 */
@Dao
public interface UsuarioDao {

    /**
     * Inserta un usuario.
     * Si hay conflicto (email o DNI duplicado), se aborta la operación.
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarUsuario(Usuario usuario);

    /**
     * Actualiza un usuario existente.
     */
    @Update
    int actualizarUsuario(Usuario usuario);

    /**
     * Elimina un usuario.
     */
    @Delete
    int eliminarUsuario(Usuario usuario);

    @Insert
    void insertarAlumno(Alumno alumno);

    @Insert
    void insertarProfesor(Profesor profesor);

    @Insert
    void insertarJuez(Juez juez);

    @Update
    void actualizarAlumno(Alumno alumno);

    @Update
    void actualizarProfesor(Profesor profesor);

    @Update
    void actualizarJuez(Juez juez);

    /**
     * Obtiene todos los usuarios ordenados por nombre.
     */
    @Query("SELECT * FROM Usuario ORDER BY Nombre ASC")
    LiveData<List<Usuario>> obtenerTodosUsuarios();

    /**
     * Busca usuario por ID.
     */
    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    LiveData<Usuario> buscarPorId(int id);


    /**
     * Busca usuario por email.
     */
    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    LiveData<Usuario> buscarPorEmail(String email);


    /**
     * Busca usuario por DNI.
     */
    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    LiveData<Usuario> buscarPorDNI(String dni);

    /**
     * Busca usuarios por coincidencia en el nombre.
     */
    @Query("SELECT * FROM Usuario WHERE Nombre LIKE '%' || :nombre || '%' ORDER BY Nombre ASC")
    LiveData<List<Usuario>> buscarPorNombre(String nombre);

    /**
     * Obtiene usuarios filtrados por tipo (alumno, profesor, juez, etc.).
     */
    @Query("SELECT * FROM Usuario WHERE Tipo = :tipo ORDER BY Nombre ASC")
    LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo);

    /**
     * Cuenta el número total de usuarios.
     */
    @Query("SELECT COUNT(*) FROM Usuario")
    LiveData<Integer> contarUsuarios();

    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    Usuario buscarPorDNISync(String dni);

    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    Usuario buscarPorEmailSync(String email);

    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    Usuario buscarPorIdSync(int id);

    /**
     * Obtiene usuarios que son alumnos y practican doma.
     */
    @Query("SELECT Usuario.* FROM Usuario " +
            "INNER JOIN Alumno ON Usuario.ID_Usuario = Alumno.ID_Alumno " +
            "WHERE Alumno.Practica_Doma = 1")
    LiveData<List<Usuario>> obtenerAlumnosDoma();

    /**
     * Obtiene jueces activos.
     */
    @Query("SELECT Usuario.* FROM Usuario " +
            "INNER JOIN Juez ON Usuario.ID_Usuario = Juez.ID_Juez " +
            "WHERE Juez.Activo = 1")
    LiveData<List<Usuario>> obtenerJuecesActivos();

    /**
     * Búsqueda avanzada de usuarios con filtros múltiples:
     * - Texto (nombre, DNI o email)
     * - Tipo de usuario (alumno, profesor, juez, propietario)
     */
    @Query("SELECT * FROM Usuario " +
            "WHERE (" +
            "Nombre LIKE '%' || :texto || '%' " +
            "OR DNI LIKE '%' || :texto || '%' " +
            "OR Email LIKE '%' || :texto || '%'" +
            ") " +
            "AND (" +
            "(:alumno = 0 AND :profesor = 0 AND :juez = 0 AND :propietario = 0) " +

            "OR (:alumno = 1 AND lower(Tipo) = 'alumno') " +
            "OR (:profesor = 1 AND lower(Tipo) = 'profesor') " +
            "OR (:juez = 1 AND lower(Tipo) = 'juez') " +
            "OR (:propietario = 1 AND lower(Tipo) = 'propietario') " +

            ") " +
            "ORDER BY Nombre ASC")
    LiveData<List<Usuario>> buscarUsuariosFiltrado(
            String texto,
            boolean alumno,
            boolean profesor,
            boolean juez,
            boolean propietario
    );


}