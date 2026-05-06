package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.PistaDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Pista;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PistaRepository {

    private final PistaDao pistaDao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estado = new MutableLiveData<>();

    public PistaRepository(Application app) {
        AppDatabase db = AppDatabase.getInstance(app);
        pistaDao = db.pistaDao();
        executor= AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estado;
    }

    // =========================
    // INSERTAR
    // =========================
    public void insertarPista(Pista pista) {

        executor.execute(() -> {

            if (pistaDao.buscarPorNombreSync(pista.nombre) != null) {
                estado.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }

            try {
                pistaDao.insertarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

    // =========================
    // ACTUALIZAR
    // =========================
    public void actualizarPista(Pista pista) {

        executor.execute(() -> {

            try {
                pistaDao.actualizarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

    public LiveData<List<Pista>> obtenerTodasPistas() {
        return pistaDao.obtenerTodasPistas();
    }

    public LiveData<Pista> buscarPorId(int id) {
        return pistaDao.buscarPorId(id);
    }

    public void eliminarPista(Pista pista) {

        executor.execute(() -> {
            try {
                pistaDao.eliminarPista(pista);
                estado.postValue("EXITO");
            } catch (Exception e) {
                estado.postValue("ERROR_BD");
            }
        });
    }

}