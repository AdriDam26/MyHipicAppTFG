package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CoordenadaRutaDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.CoordenadaRuta;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CoordenadaRutaRepository {

    private final CoordenadaRutaDao coordenadaRutaDao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public CoordenadaRutaRepository(Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        coordenadaRutaDao = db.coordenadaRutaDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    // =====================================
    // 🔹 ESTADO
    // =====================================

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR LISTA
    // =====================================

    public void insertarCoordenadas(List<CoordenadaRuta> coordenadas) {

        if (coordenadas == null || coordenadas.isEmpty()) return;

        executorService.execute(() -> {

            long idRuta = coordenadas.get(0).idRutaPersonal;

            if (!coordenadaRutaDao.existeRuta(idRuta)) {
                estadoOperacion.postValue("ERROR_RUTA_NO_EXISTE");
                return;
            }

            try {
                coordenadaRutaDao.insertarLista(coordenadas);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 OBTENER POR RUTA
    // =====================================

    public LiveData<List<CoordenadaRuta>> obtenerPorRuta(long idRuta) {
        return coordenadaRutaDao.obtenerPorRuta(idRuta);
    }

    // =====================================
    // 🔹 ELIMINAR POR RUTA
    // =====================================

    public void eliminarPorRuta(long idRuta) {

        executorService.execute(() -> {
            try {
                coordenadaRutaDao.eliminarPorRuta(idRuta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}