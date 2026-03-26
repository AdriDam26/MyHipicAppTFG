package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Usuario;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {

    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> errorProgreso = new MutableLiveData<>();

    public UsuarioRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        // Usamos un pool de 4 hilos para manejar múltiples peticiones si fuera necesario
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorProgreso() {
        return errorProgreso;
    }


    public void insertarUsuario(Usuario nuevoUsuario) {
        executorService.execute(() -> {
            // 1. Validaciones
            if (usuarioDao.buscarPorEmailSync(nuevoUsuario.email) != null) {
                errorProgreso.postValue("Error: El email ya está registrado.");
                return;
            }
            if (usuarioDao.buscarPorDNISync(nuevoUsuario.dni) != null) {
                errorProgreso.postValue("Error: El DNI ya pertenece a otro usuario.");
                return;
            }

            try {
                // 2. Inserción real
                usuarioDao.insertarUsuario(nuevoUsuario);
                // 3. ¡IMPORTANTE! Notificamos el éxito aquí mismo
                errorProgreso.postValue("EXITO_BASE_DATOS");
            } catch (Exception e) {
                errorProgreso.postValue("Error: Fallo al guardar en la base de datos.");
            }
        });
    }

    public void actualizarUsuario(Usuario usuario) {
        executorService.execute(() -> usuarioDao.actualizarUsuario(usuario));
    }

    public void eliminarUsuario(Usuario usuario) {
        executorService.execute(() -> usuarioDao.eliminarUsuario(usuario));
    }

    // --- LECTURA (LiveData) ---
    public LiveData<List<Usuario>> obtenerTodosUsuarios() {
        return usuarioDao.obtenerTodosUsuarios();
    }

    public LiveData<Usuario> buscarPorId(int id) {
        return usuarioDao.buscarPorId(id);
    }

    public LiveData<Usuario> buscarPorEmail(String email) {
        return usuarioDao.buscarPorEmail(email);
    }

    public LiveData<Usuario> buscarPorDNI(String dni) {
        return usuarioDao.buscarPorDNI(dni);
    }

    public LiveData<Integer> contarUsuarios() {
        return usuarioDao.contarUsuarios();
    }

    // --- CONSULTAS SÍNCRONAS (Para lógica de negocio) ---
    public Usuario buscarPorEmailSync(String email) {
        return usuarioDao.buscarPorEmailSync(email);
    }

    public Usuario buscarPorDNISync(String dni) {
        return usuarioDao.buscarPorDNISync(dni);
    }

    public long insertarUsuarioSync(Usuario usuario) {
        // 1. Validaciones previas (Síncronas)
        if (usuarioDao.buscarPorEmailSync(usuario.email) != null) {
            return -1; // Código de error: Email duplicado
        }
        if (usuarioDao.buscarPorDNISync(usuario.dni) != null) {
            return -2; // Código de error: DNI duplicado
        }

        try {
            // 2. Inserción real: Room devuelve el ID generado por SQLite
            return usuarioDao.insertarUsuario(usuario);
        } catch (Exception e) {
            return -3; // Código de error: Fallo general
        }
    }

    public LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo) {
        return usuarioDao.obtenerUsuariosPorTipo(tipo);
    }


}