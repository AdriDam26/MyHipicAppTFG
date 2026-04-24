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
    // 🔹 INSERTAR TRAYECTO
    // =====================================

    public void insertarTrayecto(List<CoordenadaRuta> trayecto) {

        if (trayecto == null || trayecto.isEmpty()) {
            estadoOperacion.postValue("ERROR_TRAYECTO_VACIO");
            return;
        }

        executorService.execute(() -> {

            int idRuta = trayecto.get(0).idRutaPersonal;

            // Validar ruta existe
            if (!coordenadaRutaDao.existeRuta(idRuta)) {
                estadoOperacion.postValue("ERROR_RUTA_NO_EXISTE");
                return;
            }

            try {
               // coordenadaRutaDao.insertarLista(trayecto);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 CONSULTA
    // =====================================

    public LiveData<List<CoordenadaRuta>> obtenerCoordenadas(int idRuta) {
        return coordenadaRutaDao.obtenerPorRuta(idRuta);
    }
}