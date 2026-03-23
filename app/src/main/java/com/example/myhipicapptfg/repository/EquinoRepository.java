package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.EquinoDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Equino;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EquinoRepository {

    private final EquinoDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public EquinoRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.equinoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() { return mensajeStatus; }

    public void insertar(Equino equino) {
        executor.execute(() -> {
            // 1. Validar Cuadra (Obligatoria)
            if (!dao.existeCuadra(equino.idCuadra)) {
                mensajeStatus.postValue("Error: La Cuadra especificada no existe.");
                return;
            }

            // 2. Validar Propietario (Opcional)
            if (equino.idPropietario != null) {
                if (!dao.esPropietarioValido(equino.idPropietario)) {
                    mensajeStatus.postValue("Error: El Propietario no es válido.");
                    return;
                }
            }

            // 3. Validar Microchip Único
            if (dao.existeMicrochip(equino.numeroMicrochip)) {
                mensajeStatus.postValue("Error: El microchip " + equino.numeroMicrochip + " ya existe.");
                return;
            }

            try {
                dao.insertar(equino);
                mensajeStatus.postValue(null);
            } catch (Exception e) {
                mensajeStatus.postValue("Error técnico al insertar el caballo.");
            }
        });
    }
}