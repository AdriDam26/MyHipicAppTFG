package com.example.myhipicapptfg.ui.ar.ar;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.repository.EquinoRepository;

import java.util.concurrent.Executors;

public class AREquinoViewModel extends AndroidViewModel {

    private final EquinoRepository repository;

    private final MutableLiveData<Equino> equinoSeleccionado = new MutableLiveData<>();
    private final MutableLiveData<String> nombrePropietario = new MutableLiveData<>();


    public LiveData<Equino> getEquinoSeleccionado() {
        return equinoSeleccionado;
    }

    public LiveData<String> getNombrePropietario() { return nombrePropietario; }


    public AREquinoViewModel(@NonNull Application application) {
        super(application);
        repository = new EquinoRepository(application);
    }



    public void cargarPorMicrochip(String microchip) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Equino equino = repository.buscarPorMicrochipSync(microchip);
            equinoSeleccionado.postValue(equino);

            if (equino != null && equino.idUsuario != null) {
                String nombre = repository.obtenerNombrePropietarioSync(equino.idUsuario);
                nombrePropietario.postValue(nombre != null ? nombre.trim() : "Hípica");
            } else {
                nombrePropietario.postValue("Hípica");
            }
        });
    }
}