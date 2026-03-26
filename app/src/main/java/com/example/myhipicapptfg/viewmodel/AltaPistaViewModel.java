package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.repository.PistaRepository;

public class AltaPistaViewModel extends AndroidViewModel {

    private final PistaRepository repository;
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public AltaPistaViewModel(@NonNull Application application) {
        super(application);
        repository = new PistaRepository(application);
    }

    // Exponemos los errores del repositorio (reglas de negocio como nombre duplicado)
    public LiveData<String> getMensajeEstado() {
        return repository.getErrorLiveData();
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public void registrarPista(String nombre, String anchoStr, String largoStr, String estado) {
        cargando.setValue(true);

        try {
            Pista nuevaPista = new Pista();
            nuevaPista.nombre = nombre;
            nuevaPista.ancho = Double.parseDouble(anchoStr);
            nuevaPista.largo = Double.parseDouble(largoStr);
            nuevaPista.estado = estado;

            repository.insertarPista(nuevaPista);
        } catch (NumberFormatException e) {
            // Error de conversión
            cargando.setValue(false);
        }
    }

    public void finalizarOperacion() {
        cargando.setValue(false);
    }
}