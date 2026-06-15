package com.example.myhipicapptfg.ui.admin.usuarios;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.AlumnoRepository;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;
import com.example.myhipicapptfg.datos.repositorios.ProfesorRepository;
import com.example.myhipicapptfg.datos.repositorios.UsuarioRepository;

import java.util.List;

/**
 * ViewModel encargado de la gestión de usuarios del sistema.
 *
 * Centraliza operaciones relacionadas con:
 * - Usuarios base (Usuario)
 * - Subtipos: Alumno, Profesor y Juez
 *
 * Actúa como intermediario entre la UI y los repositorios,
 * siguiendo el patrón MVVM.
 */
public class GestionUsuariosViewModel extends AndroidViewModel {

    // Repositorios de entidades especializadas
    private final UsuarioRepository repository;

    private AlumnoRepository alumnoRepository;
    private ProfesorRepository profesorRepository;
    private JuezRepository juezRepository;

    /**
     * Constructor: inicializa todos los repositorios
     */
    public GestionUsuariosViewModel(@NonNull Application application) {
        super(application);
        repository = new UsuarioRepository(application);

        alumnoRepository = new AlumnoRepository(application);
        profesorRepository = new ProfesorRepository(application);
        juezRepository = new JuezRepository(application);
    }

    /**
     * Obtiene todos los usuarios del sistema.
     */
    public LiveData<List<Usuario>> getUsuarios() {
        return repository.obtenerTodosUsuarios();
    }

    /**
     * Busca un usuario por su ID.
     */
    public LiveData<Usuario> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    /**
     * LiveData que expone el estado de las operaciones
     * (EXITO, ERROR, etc.)
     */
    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }


    /**
     * Elimina un usuario del sistema.
     */
    public void eliminarUsuario(Usuario u) {
        repository.eliminarUsuario(u);
    }


    /**
     * Inserta un usuario completo con sus subtipos:
     * Alumno, Profesor o Juez.
     *
     * Se usa cuando se crea un usuario desde cero.
     */
    public void guardarUsuarioCompleto(
            Usuario usuario,
            Alumno alumno,
            Profesor profesor,
            Juez juez
    ) {
        repository.insertarUsuarioCompleto(usuario, alumno, profesor, juez);
    }


    /**
     * Actualiza un usuario completo incluyendo su subtipo.
     */
    public void actualizarUsuarioCompleto(
            Usuario u,
            Alumno a,
            Profesor p,
            Juez j
    ) {
        repository.actualizarUsuarioCompleto(u, a, p, j);
    }


    /**
     * Obtiene datos adicionales de un alumno.
     */
    public LiveData<Alumno> getAlumno(int id) {
        return alumnoRepository.buscarPorId(id);
    }

    /**
     * Obtiene datos adicionales de un profesor.
     */
    public LiveData<Profesor> getProfesor(int id) {
        return profesorRepository.buscarPorId(id);
    }


    /**
     * Obtiene datos adicionales de un juez.
     */
    public LiveData<Juez> getJuez(int id) {
        return juezRepository.buscarPorId(id);
    }

    /**
     * Permite buscar usuarios filtrando por texto y tipo:
     * - alumno
     * - profesor
     * - juez
     * - propietario
     */
    public LiveData<List<Usuario>> buscarUsuariosFiltrado(
            String texto,
            boolean alumno,
            boolean profesor,
            boolean juez,
            boolean propietario
    ) {
        return repository.buscarUsuariosFiltrado(
                texto,
                alumno,
                profesor,
                juez,
                propietario
        );
    }
}