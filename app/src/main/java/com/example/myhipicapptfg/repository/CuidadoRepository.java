package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CuidadoDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Cuidado;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuidadoRepository {

    private final CuidadoDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public CuidadoRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.cuidadoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(Cuidado cuidado) {
        executor.execute(() -> {
            // Validamos que el caballo exista
            if (dao.existeEquino(cuidado.idEquino)) {
                try {
                    dao.insertar(cuidado);
                    mensajeStatus.postValue(null); // Éxito
                } catch (Exception e) {
                    mensajeStatus.postValue("Error al guardar el registro de cuidado.");
                }
            } else {
                mensajeStatus.postValue("Error: El Equino con ID " + cuidado.idEquino + " no existe.");
            }
        });
    }

    public LiveData<List<Cuidado>> obtenerCuidadosCaballo(int idEquino) {
        return dao.obtenerPorEquino(idEquino);
    }
}