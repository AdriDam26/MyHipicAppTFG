package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CoordenadaRutaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.CoordenadaRuta;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoordenadaRutaRepository {

    private final CoordenadaRutaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public CoordenadaRutaRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.coordenadaRutaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    // Insertar una lista de coordenadas (El trayecto del GPS)
    public void insertarTrayecto(List<CoordenadaRuta> trayecto) {
        if (trayecto == null || trayecto.isEmpty()) return;

        executor.execute(() -> {
            // Validamos con el primer punto si la ruta existe
            int idRuta = trayecto.get(0).idRutaPersonal;

            if (dao.existeRuta(idRuta)) {
                try {
                    dao.insertarLista(trayecto);
                    mensajeStatus.postValue(null);
                } catch (Exception e) {
                    mensajeStatus.postValue("Error al guardar las coordenadas.");
                }
            } else {
                mensajeStatus.postValue("Error: La ruta ID " + idRuta + " no existe.");
            }
        });
    }

    public LiveData<List<CoordenadaRuta>> obtenerCoordenadas(int idRuta) {
        return dao.obtenerPorRuta(idRuta);
    }
}