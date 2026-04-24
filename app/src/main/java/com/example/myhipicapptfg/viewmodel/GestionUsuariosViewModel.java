package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Juez;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.AlumnoRepository;
import com.example.myhipicapptfg.repository.JuezRepository;
import com.example.myhipicapptfg.repository.ProfesorRepository;
import com.example.myhipicapptfg.repository.UsuarioRepository;

import java.util.List;

public class GestionUsuariosViewModel extends AndroidViewModel {

    private final UsuarioRepository repository;

    private AlumnoRepository alumnoRepository;
    private ProfesorRepository profesorRepository;
    private JuezRepository juezRepository;

    public GestionUsuariosViewModel(@NonNull Application application) {
        super(application);
        repository = new UsuarioRepository(application);

        alumnoRepository = new AlumnoRepository(application);
        profesorRepository = new ProfesorRepository(application);
        juezRepository = new JuezRepository(application);
    }

    public LiveData<List<Usuario>> getUsuarios() {
        return repository.obtenerTodosUsuarios();
    }

    public LiveData<Usuario> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }

    public void insertarUsuario(Usuario u) {
        repository.insertarUsuario(u);
    }

    public void actualizarUsuario(Usuario u) {
        repository.actualizarUsuario(u);
    }

    public void eliminarUsuario(Usuario u) {
        repository.eliminarUsuario(u);
    }

    public void guardarUsuarioCompleto(
            Usuario usuario,
            Alumno alumno,
            Profesor profesor,
            Juez juez
    ) {
        repository.insertarUsuarioCompleto(usuario, alumno, profesor, juez);
    }


    public void actualizarUsuarioCompleto(
            Usuario u,
            Alumno a,
            Profesor p,
            Juez j
    ) {
        repository.actualizarUsuarioCompleto(u, a, p, j);
    }


    public LiveData<Alumno> getAlumno(int id) {
        return alumnoRepository.buscarPorId(id);
    }

    public LiveData<Profesor> getProfesor(int id) {
        return profesorRepository.buscarPorId(id);
    }

    public LiveData<Juez> getJuez(int id) {
        return juezRepository.buscarPorId(id);
    }
}