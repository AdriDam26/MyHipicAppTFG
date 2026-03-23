package com.example.myhipicapptfg.dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Delete;
import com.example.myhipicapptfg.entities.Usuario;
import java.util.List;

@Dao
public interface UsuarioDao {

    @Insert
    long insertarUsuario(Usuario usuario);

    @Update
    void actualizarUsuario(Usuario usuario);

    @Delete
    int eliminarUsuario(Usuario usuario);

    @Query("SELECT * FROM Usuario")
    LiveData<List<Usuario>> obtenerTodosUsuarios();

    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    LiveData<Usuario> buscarPorEmail(String email);

    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    LiveData<Usuario> buscarPorId(int id);

    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    LiveData<Usuario> buscarPorDNI(String dni);

    @Query("SELECT * FROM Usuario WHERE Nombre LIKE '%' || :nombre || '%'")
    LiveData<List<Usuario>> buscarPorNombre(String nombre);

    @Query("SELECT COUNT(*) FROM Usuario")
    LiveData<Integer> contarUsuarios();

    // Métodos Síncronos para validaciones previas
    @Query("SELECT * FROM Usuario WHERE DNI = :dni LIMIT 1")
    Usuario buscarPorDNISync(String dni);

    @Query("SELECT * FROM Usuario WHERE Email = :email LIMIT 1")
    Usuario buscarPorEmailSync(String email);

    @Query("SELECT * FROM Usuario WHERE ID_Usuario = :id LIMIT 1")
    Usuario buscarPorIdSync(int id);
}