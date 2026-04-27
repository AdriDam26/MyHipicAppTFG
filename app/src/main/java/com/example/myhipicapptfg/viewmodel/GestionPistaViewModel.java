package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.repository.PistaRepository;

import java.util.List;

public class GestionPistaViewModel extends AndroidViewModel {

    private final PistaRepository repository;

    public GestionPistaViewModel(@NonNull Application application) {
        super(application);
        repository = new PistaRepository(application);
    }

    public LiveData<List<Pista>> obtenerTodas() {
        return repository.obtenerTodasPistas();
    }

    public LiveData<Pista> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public void insertar(Pista pista) {
        repository.insertarPista(pista);
    }

    public void actualizar(Pista pista) {
        repository.actualizarPista(pista);
    }

    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }

    public void eliminar(Pista pista) {
        repository.eliminarPista(pista);
    }
}