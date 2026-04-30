package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.RutaPersonalDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.RutaPersonal;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutaPersonalRepository {

    private final RutaPersonalDao dao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();

    private final LiveData<List<RutaPersonal>> listaRutas;

    public RutaPersonalRepository(Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.rutaPersonalDao();

        executorService = Executors.newSingleThreadExecutor();

        listaRutas = dao.obtenerTodas();
    }

    // =====================================
    // 🔹 GUARDAR RUTA COMPLETA
    // =====================================

    public void guardarRutaCompleta(RutaPersonal ruta, List<CoordenadaRuta> puntos) {

        executorService.execute(() -> {

            if (!dao.esPropietarioValido(ruta.idPropietario)) {
                estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                return;
            }

            try {
                long id = dao.guardarRutaConPuntos(ruta, puntos);

                idInsertado.postValue(id);
                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminar(RutaPersonal ruta) {
        executorService.execute(() -> {
            try {
                dao.eliminar(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 READ
    // =====================================

    public LiveData<List<RutaPersonal>> getListaRutas() {
        return listaRutas;
    }

    public LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario) {
        return dao.obtenerPorPropietario(idPropietario);
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    public LiveData<Long> getIdInsertado() {
        return idInsertado;
    }
}