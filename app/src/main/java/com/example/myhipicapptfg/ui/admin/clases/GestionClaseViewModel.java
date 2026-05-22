package com.example.myhipicapptfg.ui.admin.clases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.ClaseRepository;

import java.util.List;

public class GestionClaseViewModel extends AndroidViewModel {

    private final ClaseRepository repository;

    private final MutableLiveData<List<Usuario>>
            profesoresDisponibles = new MutableLiveData<>();

    private final MutableLiveData<List<Pista>>
            pistasDisponibles = new MutableLiveData<>();

    public GestionClaseViewModel(@NonNull Application application) {

        super(application);

        repository = new ClaseRepository(application);
    }

    // =========================================================
    // LIVEDATA DISPONIBILIDAD
    // =========================================================

    public LiveData<List<Usuario>> getProfesoresDisponibles() {
        return profesoresDisponibles;
    }

    public LiveData<List<Pista>> getPistasDisponibles() {
        return pistasDisponibles;
    }

    // =========================================================
    // FILTRAR
    // =========================================================

    public void filtrarDisponibilidad(
            long inicio,
            long fin,
            String disciplina,
            String nivel,
            List<Usuario> usuarios,
            List<Profesor> profesores,
            List<Pista> pistas,
            List<Clase> clases,
            int claseEditando
    ) {

        repository.filtrarDisponibilidad(
                inicio,
                fin,
                disciplina,
                nivel,
                usuarios,
                profesores,
                pistas,
                clases,
                claseEditando,
                profesoresDisponibles,
                pistasDisponibles
        );
    }

    // =========================================================
    // CONSULTAS
    // =========================================================

    public LiveData<List<Clase>> obtenerTodasLasClases() {
        return repository.obtenerTodasClases();
    }

    public LiveData<Clase> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public LiveData<List<Profesor>> obtenerTodosLosProfesores() {
        return repository.obtenerTodosLosProfesores();
    }

    public LiveData<List<Pista>> obtenerTodasLasPistas() {
        return repository.obtenerTodasLasPistas();
    }

    public LiveData<List<Usuario>> obtenerTodosLosUsuarios() {
        return repository.obtenerTodosLosUsuarios();
    }

    // =========================================================
    // CRUD
    // =========================================================

    public void insertar(Clase clase) {
        repository.insertarClase(clase);
    }

    public void actualizar(Clase clase) {
        repository.actualizarClase(clase);
    }

    public void eliminar(Clase clase) {
        repository.eliminarClase(clase);
    }

    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }
}