package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ReprisaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Reprisa;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReprisaRepository {

    private final ReprisaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public ReprisaRepository(Application application) {
        dao = TestDatabase.getInstance(application).reprisaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(Reprisa reprisa) {
        executor.execute(() -> {
            // 1. Validar Plantilla
            if (!dao.existePlantilla(reprisa.idPrueba)) {
                mensajeStatus.postValue("Error: La Plantilla de Prueba no existe.");
                return;
            }

            // 2. Validar Orden y Peso
            if (reprisa.orden < 1) {
                mensajeStatus.postValue("Error: El número de orden debe ser mayor o igual a 1.");
                return;
            }
            if (reprisa.pesoEjercicio <= 0) {
                mensajeStatus.postValue("Error: El coeficiente (peso) debe ser mayor a 0.");
                return;
            }

            // 3. Validar duplicidad de orden
            if (dao.existeOrdenEnPlantilla(reprisa.idPrueba, reprisa.orden)) {
                mensajeStatus.postValue("Error: Ya existe un movimiento con el orden " + reprisa.orden);
                return;
            }

            try {
                dao.insertarReprisa(reprisa);
                mensajeStatus.postValue(null); // Éxito
            } catch (Exception e) {
                mensajeStatus.postValue("Error técnico al guardar el movimiento de la reprisa.");
            }
        });
    }
}