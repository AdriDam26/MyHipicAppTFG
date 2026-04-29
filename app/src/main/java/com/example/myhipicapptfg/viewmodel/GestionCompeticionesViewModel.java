package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Competicion;
import com.example.myhipicapptfg.repository.CompeticionRepository;

import java.util.List;

public class GestionCompeticionesViewModel extends AndroidViewModel {

    private final CompeticionRepository repository;

    public GestionCompeticionesViewModel(@NonNull Application application) {
        super(application);
        repository = new CompeticionRepository(application);
    }

    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }

    public LiveData<List<Competicion>> getCompeticiones() {
        return repository.obtenerTodas();
    }

    public LiveData<Competicion> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public void insertar(Competicion c) {
        repository.insertarCompeticion(c);
    }

    public void actualizar(Competicion c) {
        repository.actualizarCompeticion(c);
    }

    public void eliminar(Competicion c) {
        repository.eliminarCompeticion(c);
    }
}