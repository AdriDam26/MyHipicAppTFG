package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CompeticionDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Competicion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompeticionRepository {

    private final CompeticionDao dao;
    private final ExecutorService executor;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public CompeticionRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.competicionDao();

        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================
    public void insertar(Competicion comp) {

        executor.execute(() -> {

            // 1. Validar nombre único
            if (dao.existeNombre(comp.nombre)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }

            try {
                dao.insertarCompeticion(comp);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================
    public void actualizar(Competicion comp) {

        executor.execute(() -> {
            try {
                dao.actualizarCompeticion(comp);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================
    public void eliminar(Competicion comp) {

        executor.execute(() -> {
            try {
                dao.eliminarCompeticion(comp);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


}