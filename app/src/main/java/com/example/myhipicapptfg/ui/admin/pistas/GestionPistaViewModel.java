package com.example.myhipicapptfg.ui.admin.pistas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.repositorios.PistaRepository;

import java.util.List;


/**
 * ViewModel encargado de la gestión de Pistas.
 *
 *
 * Su responsabilidad es exponer datos en forma de LiveData
 * y delegar las operaciones CRUD al repositorio.
 */
public class GestionPistaViewModel extends AndroidViewModel {

    // Repositorio que gestiona el acceso a datos de Pista
    private final PistaRepository repository;

    public GestionPistaViewModel(@NonNull Application application) {
        super(application);
        // Inicialización del repositorio con contexto de aplicación
        repository = new PistaRepository(application);
    }

    /**
     * Obtiene todas las pistas disponibles en la base de datos.
     */
    public LiveData<List<Pista>> obtenerTodas() {
        return repository.obtenerTodasPistas();
    }

    /**
     * Busca una pista concreta por su ID.
     */
    public LiveData<Pista> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }


    /**
     * Inserta una nueva pista en la base de datos.
     */
    public void insertar(Pista pista) {
        repository.insertarPista(pista);
    }


    /**
     * Actualiza los datos de una pista existente.
     */
    public void actualizar(Pista pista) {
        repository.actualizarPista(pista);
    }

    /**
     * LiveData que expone el estado de las operaciones
     * (EXITO, ERROR, etc.), útil para mostrar mensajes en UI.
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }


    /**
     * Elimina una pista de la base de datos.
     */
    public void eliminar(Pista pista) {
        repository.eliminarPista(pista);
    }
}