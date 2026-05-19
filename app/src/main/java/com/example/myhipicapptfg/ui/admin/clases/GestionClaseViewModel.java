package com.example.myhipicapptfg.ui.admin.clases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.ClaseRepository;

import java.util.List;

public class GestionClaseViewModel extends AndroidViewModel {

    private final ClaseRepository repository;

    public GestionClaseViewModel(@NonNull Application application) {
        super(application);
        repository = new ClaseRepository(application);
    }

    // =====================================
    // 🔹 MÉTODOS DE CONSULTA
    // =====================================

    /**
     * Obtiene todas las clases programadas.
     */
    public LiveData<List<Clase>> obtenerTodasLasClases() {
        return repository.obtenerTodasClases();
    }

    /**
     * Busca una clase específica por su ID.
     */
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


    // =====================================
    // 🔹 MÉTODOS DE OPERACIÓN (INSERT/UPDATE/DELETE)
    // =====================================

    public void insertar(Clase clase) {
        repository.insertarClase(clase);
    }

    public void actualizar(Clase clase) {
        repository.actualizarClase(clase);
    }

    public void eliminar(Clase clase) {
        repository.eliminarClase(clase);
    }

    /**
     * Observa el resultado de las operaciones (EXITO, ERROR_PISTA_OCUPADA,
     * ERROR_PROFESOR_OCUPADO, etc.)
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }
}