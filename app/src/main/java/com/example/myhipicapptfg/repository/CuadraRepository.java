package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CuadraDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Cuadra;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuadraRepository {

    private final CuadraDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();
    private final LiveData<List<Cuadra>> todasLasCuadras;

    public CuadraRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.cuadraDao();
        executor = Executors.newSingleThreadExecutor();
        todasLasCuadras = dao.obtenerTodas();
    }

    public LiveData<List<Cuadra>> getTodasLasCuadras() {
        return todasLasCuadras;
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(Cuadra cuadra) {
        executor.execute(() -> {
            // Validamos si el Numero_Cuadra ya existe
            if (dao.existeNumero(cuadra.numeroCuadra)) {
                mensajeStatus.postValue("Error: El número de cuadra " + cuadra.numeroCuadra + " ya existe.");
            } else {
                try {
                    dao.insertarCuadra(cuadra);
                    mensajeStatus.postValue(null); // Éxito
                } catch (Exception e) {
                    mensajeStatus.postValue("Error técnico al insertar la cuadra.");
                }
            }
        });
    }

    public void eliminar(Cuadra cuadra) {
        executor.execute(() -> dao.eliminarCuadra(cuadra));
    }
}