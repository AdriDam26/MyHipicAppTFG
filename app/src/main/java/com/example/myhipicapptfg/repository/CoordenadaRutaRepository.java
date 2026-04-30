package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
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

    public CoordenadaRutaRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        coordenadaRutaDao = db.coordenadaRutaDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarCoordenada(CoordenadaRuta coordenada) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.insertar(coordenada);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void insertarListaCoordenadas(List<CoordenadaRuta> lista) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.insertarLista(lista);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarCoordenadasPorRuta(int idRuta) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.eliminarPorRuta(idRuta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA (LiveData)
    // =====================================

    public LiveData<List<CoordenadaRuta>> obtenerCoordenadasPorRuta(int idRuta) {
        return coordenadaRutaDao.obtenerPorRuta(idRuta);
    }


}