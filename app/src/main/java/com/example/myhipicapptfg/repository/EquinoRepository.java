package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.EquinoDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Equino;

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

    public long insertar(Equino equino) {
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
        return 0;
    }


    public long insertarSync(Equino equino) {
        try {
            if (dao.existeMicrochip(equino.numeroMicrochip)) {
                // Usamos el nombre unificado
                mensajeStatus.postValue("Error: El microchip " + equino.numeroMicrochip + " ya existe.");
                return -1;
            }

            if (!dao.existeCuadra(equino.idCuadra)) {
                mensajeStatus.postValue("Error: La Cuadra especificada no existe.");
                return -1;
            }

            if (equino.idPropietario != null) {
                if (!dao.esPropietarioValido(equino.idPropietario)) {
                    mensajeStatus.postValue("Error: El Propietario no es válido.");
                    return -1;
                }
            }

            long idGenerado = dao.insertar(equino);

            if (idGenerado > 0) {
                return idGenerado;
            } else {
                mensajeStatus.postValue("Error al insertar en la tabla Equino.");
                return -1;
            }

        } catch (Exception e) {
            mensajeStatus.postValue("Error crítico: " + e.getMessage());
            return -1;
        }
    }
}