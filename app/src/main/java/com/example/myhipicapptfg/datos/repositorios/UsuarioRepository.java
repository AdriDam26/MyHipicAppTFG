package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class UsuarioRepository {

    private final AppDatabase db;
    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public UsuarioRepository(@NonNull Application application) {
        db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        executorService = AppDatabase.getDatabaseExecutor();
    }

    // =====================================
    // ESTADO
    // =====================================

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // INSERTAR
    // =====================================

    public void insertarUsuario(Usuario usuario) {
        executorService.execute(() -> {

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

    public void insertarUsuarioCompleto(Usuario u, Alumno a, Profesor p, Juez j) {
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
                db.runInTransaction(() -> {                                // ✅ usa el campo db
                    long id = usuarioDao.insertarUsuario(u);

                    if (a != null) { a.idAlumno    = (int) id; usuarioDao.insertarAlumno(a); }
                    if (p != null) { p.idProfesor  = (int) id; usuarioDao.insertarProfesor(p); }
                    if (j != null) { j.idJuez      = (int) id; usuarioDao.insertarJuez(j); }
                });

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // ACTUALIZAR
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

    public void actualizarUsuarioCompleto(Usuario u, Alumno a, Profesor p, Juez j) {
        executorService.execute(() -> {
            try {
                db.runInTransaction(() -> {                                // ✅ usa el campo db
                    usuarioDao.actualizarUsuario(u);
                    if (a != null) usuarioDao.actualizarAlumno(a);
                    if (p != null) usuarioDao.actualizarProfesor(p);
                    if (j != null) usuarioDao.actualizarJuez(j);
                });

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // ELIMINAR
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
    // LECTURA (LiveData)
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

    public LiveData<List<Usuario>> obtenerAlumnosDoma() {
        return usuarioDao.obtenerAlumnosDoma();
    }

    public LiveData<List<Usuario>> obtenerJuecesActivos() {
        return usuarioDao.obtenerJuecesActivos();
    }

    public LiveData<List<Usuario>> buscarPorNombre(String nombre) {
        return usuarioDao.buscarPorNombre(nombre);
    }

    public LiveData<List<Usuario>> buscarUsuariosFiltrado(
            String texto,
            boolean alumno,
            boolean profesor,
            boolean juez,
            boolean propietario
    ) {
        return usuarioDao.buscarUsuariosFiltrado(
                texto,
                alumno,
                profesor,
                juez,
                propietario
        );
    }
}