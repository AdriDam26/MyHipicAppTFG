package com.example.myhipicapptfg.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;

@Dao
public interface UsuarioDao {

    // 🔹 INSERT
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertarUsuario(Usuario usuario);

    // 🔹 UPDATE
    @Update
    int actualizarUsuario(Usuario usuario);

    // 🔹 DELETE
    @Delete
    int eliminarUsuario(Usuario usuario);

    // 🔹 LISTADO GENERAL
    @Query("SELECT * FROM Usuario ORDER BY Nombre ASC")
    LiveData<List<Usuario>> obtenerTodosUsuarios();

    // 🔹 BUSQUEDAS PRINCIPALES
    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    LiveData<Usuario> buscarPorId(int id);

    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    LiveData<Usuario> buscarPorEmail(String email);

    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    LiveData<Usuario> buscarPorDNI(String dni);

    @Query("SELECT * FROM Usuario WHERE Nombre LIKE '%' || :nombre || '%' ORDER BY Nombre ASC")
    LiveData<List<Usuario>> buscarPorNombre(String nombre);

    // FILTRO POR TIPO (ADMIN, ALUMNO, etc.)
    @Query("SELECT * FROM Usuario WHERE Tipo = :tipo ORDER BY Nombre ASC")
    LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo);

    // ESTADÍSTICA SIMPLE
    @Query("SELECT COUNT(*) FROM Usuario")
    LiveData<Integer> contarUsuarios();

    // 🔹 CONSULTAS SINCRONAS (validaciones)
    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    Usuario buscarPorDNISync(String dni);

    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    Usuario buscarPorEmailSync(String email);

    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    Usuario buscarPorIdSync(int id);
}