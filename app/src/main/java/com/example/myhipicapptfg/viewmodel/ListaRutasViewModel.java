package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.RutaPersonal;
import com.example.myhipicapptfg.repository.RutaPersonalRepository;

import java.util.List;

public class ListaRutasViewModel extends AndroidViewModel {

    private final RutaPersonalRepository repository;

    public ListaRutasViewModel(@NonNull Application application) {
        super(application);
        repository = new RutaPersonalRepository(application);
    }

    public LiveData<List<RutaPersonal>> obtenerRutas(int idPropietario) {
        return repository.obtenerPorPropietario(idPropietario);
    }

    public void eliminar(RutaPersonal ruta) {
        repository.eliminar(ruta);
    }
}