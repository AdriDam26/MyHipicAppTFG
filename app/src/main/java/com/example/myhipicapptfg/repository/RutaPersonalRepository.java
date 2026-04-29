package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
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

    private final RutaPersonalDao rutaPersonalDao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    private final LiveData<List<RutaPersonal>> rutas;

    public RutaPersonalRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        rutaPersonalDao = db.rutaPersonalDao();

        executorService = Executors.newSingleThreadExecutor();

        rutas = rutaPersonalDao.obtenerTodas();
    }

    // =====================================
    // 🔹 ESTADO
    // =====================================

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    public LiveData<List<RutaPersonal>> getRutas() {
        return rutas;
    }

    // =====================================
    // 🔹 INSERTAR RUTA + COORDENADAS
    // =====================================

    public void insertarRutaCompleta(RutaPersonal ruta, List<CoordenadaRuta> coordenadas) {

        executorService.execute(() -> {

            // Validación propietario
            if (!rutaPersonalDao.esPropietarioValido(ruta.idPropietario)) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_PROPIETARIO");
                return;
            }

            try {
                rutaPersonalDao.guardarRutaConPuntos(ruta, coordenadas);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarRuta(RutaPersonal ruta) {

        executorService.execute(() -> {
            try {
                rutaPersonalDao.actualizar(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarRuta(RutaPersonal ruta) {

        executorService.execute(() -> {
            try {
                rutaPersonalDao.eliminar(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 FILTRO POR PROPIETARIO
    // =====================================

    public LiveData<List<RutaPersonal>> obtenerPorPropietario(long idPropietario) {
        return rutaPersonalDao.obtenerPorPropietario(idPropietario);
    }
}