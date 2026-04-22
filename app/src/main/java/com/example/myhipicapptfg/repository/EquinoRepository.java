package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.EquinoDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Equino;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EquinoRepository {

    private final EquinoDao equinoDao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public EquinoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        equinoDao = db.equinoDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR (ASYNC)
    // =====================================

    public void insertarEquino(Equino equino) {

        executorService.execute(() -> {

            // 1️⃣ Validar microchip único
            if (equinoDao.existeMicrochip(equino.numeroMicrochip)) {
                estadoOperacion.postValue("ERROR_MICROCHIP_DUPLICADO");
                return;
            }

            // 2️⃣ Validar cuadra
            if (!equinoDao.existeCuadra(equino.numeroCuadra)) {
                estadoOperacion.postValue("ERROR_CUADRA_NO_EXISTE");
                return;
            }

            // 3️⃣ Validar propietario (puede ser null)
            if (equino.idUsuario != null) {
                if (!equinoDao.esPropietarioValido(equino.idUsuario)) {
                    estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                    return;
                }
            }

            try {
                equinoDao.insertarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }




}