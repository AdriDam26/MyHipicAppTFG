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


    public void insertarUsuarioSeguro(Usuario nuevoUsuario) {
        executorService.execute(() -> {

            // 1. Validar Email único
            if (usuarioDao.buscarPorEmailSync(nuevoUsuario.email) != null) {
                errorProgreso.postValue("Error: El email ya está registrado.");
                return; // Cortamos la ejecución
            }

            // 2. Validar DNI único
            if (usuarioDao.buscarPorDNISync(nuevoUsuario.dni) != null) {
                errorProgreso.postValue("Error: El DNI ya pertenece a otro usuario.");
                return;
            }

            // 3. Si pasa las validaciones, insertamos
            try {
                usuarioDao.insertarUsuario(nuevoUsuario);
                errorProgreso.postValue(null); // Limpiamos errores si hubo éxito
            } catch (Exception e) {
                errorProgreso.postValue("Error crítico en la base de datos.");
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
}