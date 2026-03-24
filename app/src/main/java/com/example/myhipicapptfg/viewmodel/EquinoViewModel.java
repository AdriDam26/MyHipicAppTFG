package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.repository.EquinoRepository;

import java.util.List;
import java.util.concurrent.Executors;

public class EquinoViewModel extends AndroidViewModel {

    private final EquinoRepository repository;



    // Caballo individual cargado para la pantalla AR
    private final MutableLiveData<Equino> equinoSeleccionado = new MutableLiveData<>();
    public LiveData<Equino> getEquinoSeleccionado() { return equinoSeleccionado; }

    public EquinoViewModel(@NonNull Application application) {
        super(application);
        repository = new EquinoRepository(application);
    }

    // Insertar un caballo nuevo
    public void insertar(Equino equino) {
        repository.insertar(equino);
    }

    // Cargar un caballo por ID (lo usa la ARActivity)
    public void cargarPorId(int id) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Equino equino = repository.buscarPorIdSync(id);
            equinoSeleccionado.postValue(equino);
        });
    }

    public void cargarPorMicrochip(String microchip) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Equino equino = repository.buscarPorMicrochipSync(microchip);
            equinoSeleccionado.postValue(equino);
        });
    }
}
