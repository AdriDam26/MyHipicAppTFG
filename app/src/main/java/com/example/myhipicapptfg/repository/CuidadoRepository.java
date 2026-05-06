package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CuidadoDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Cuidado;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CuidadoRepository {

    private final CuidadoDao dao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public CuidadoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.cuidadoDao();

        executorService = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================
    public void insertar(Cuidado cuidado) {

        executorService.execute(() -> {

            // ✔ Validar que el equino exista
            if (!dao.existeEquino(cuidado.idEquino)) {
                estadoOperacion.postValue("ERROR_EQUINO_NO_EXISTE");
                return;
            }

            try {
                dao.insertarCuidado(cuidado);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ACTUALIZAR
    // =====================================
    public void actualizar(Cuidado cuidado) {

        executorService.execute(() -> {

            try {
                dao.actualizarCuidado(cuidado);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ELIMINAR
    // =====================================
    public void eliminar(Cuidado cuidado) {

        executorService.execute(() -> {

            try {
                dao.eliminarCuidado(cuidado);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }



}