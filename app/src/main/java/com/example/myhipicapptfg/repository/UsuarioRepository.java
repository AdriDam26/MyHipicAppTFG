package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Juez;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UsuarioRepository {

    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public UsuarioRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarUsuario(Usuario usuario) {
        executorService.execute(() -> {

            // Validaciones
            if (usuarioDao.buscarPorEmailSync(usuario.email) != null) {
                estadoOperacion.postValue("ERROR_EMAIL_DUPLICADO");
                return;
            }

            if (usuarioDao.buscarPorDNISync(usuario.dni) != null) {
                estadoOperacion.postValue("ERROR_DNI_DUPLICADO");
                return;
            }

            try {
                usuarioDao.insertarUsuario(usuario);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void insertarUsuarioCompleto(
            Usuario u,
            Alumno a,
            Profesor p,
            Juez j
    ) {
        executorService.execute(() -> {

            if (usuarioDao.buscarPorEmailSync(u.email) != null) {
                estadoOperacion.postValue("ERROR_EMAIL_DUPLICADO");
                return;
            }

            if (usuarioDao.buscarPorDNISync(u.dni) != null) {
                estadoOperacion.postValue("ERROR_DNI_DUPLICADO");
                return;
            }

            try {
                usuarioDao.insertarUsuarioCompleto(u, a, p, j);
                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }



    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarUsuario(Usuario usuario) {
        executorService.execute(() -> {
            try {
                usuarioDao.actualizarUsuario(usuario);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarUsuario(Usuario usuario) {
        executorService.execute(() -> {
            try {
                usuarioDao.eliminarUsuario(usuario);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA (LiveData)
    // =====================================

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

    public LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo) {
        return usuarioDao.obtenerUsuariosPorTipo(tipo);
    }

    public LiveData<Integer> contarUsuarios() {
        return usuarioDao.contarUsuarios();
    }

    public void actualizarUsuarioCompleto(
            Usuario u,
            Alumno a,
            Profesor p,
            Juez j
    ) {
        executorService.execute(() -> {

            try {
                // 1. actualizar usuario base
                usuarioDao.actualizarUsuario(u);

                // 2. actualizar según tipo
                if (a != null) {
                    usuarioDao.actualizarAlumno(a);
                }

                if (p != null) {
                    usuarioDao.actualizarProfesor(p);
                }

                if (j != null) {
                    usuarioDao.actualizarJuez(j);
                }

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}