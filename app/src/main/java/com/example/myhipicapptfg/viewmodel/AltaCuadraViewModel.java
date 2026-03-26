package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.repository.CuadraRepository;

public class AltaCuadraViewModel extends AndroidViewModel {

    private final CuadraRepository repository;
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public AltaCuadraViewModel(@NonNull Application application) {
        super(application);
        repository = new CuadraRepository(application);
    }

    // Exponemos el mensaje de estado del repositorio (errores de BD)
    public LiveData<String> getMensajeEstado() {
        return repository.getMensajeStatus();
    }

    public LiveData<Boolean> getCargando() {
        return cargando;
    }

    public void registrarCuadra(String numeroTexto) {
        // Marcamos inicio de operación
        cargando.setValue(true);

        try {
            int numero = Integer.parseInt(numeroTexto);
            Cuadra nuevaCuadra = new Cuadra();
            nuevaCuadra.numeroCuadra = numero;
            repository.insertar(nuevaCuadra);
        } catch (NumberFormatException e) {
            // Este error se capturaría en la Activity, pero lo manejamos por seguridad
            cargando.setValue(false);
        }
    }

    // Método para resetear el estado de carga cuando el repositorio responde
    public void finalizarOperacion() {
        cargando.setValue(false);
    }
}