package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.PruebaDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Prueba;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PruebaRepository {

    private final PruebaDao dao;
    private final ExecutorService executor;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public PruebaRepository(@NonNull Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        dao = db.pruebaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================
    public void insertarPrueba(Prueba prueba) {

        executor.execute(() -> {

            // 1. Validar nombre único
            if (dao.existeNombreSync(prueba.nombre)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }

            // 2. Validar competición existente
            if (!dao.existeCompeticionSync(prueba.idCompeticion)) {
                estadoOperacion.postValue("ERROR_COMPETICION_NO_EXISTE");
                return;
            }

            try {
                dao.insertarPrueba(prueba);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ACTUALIZAR
    // =====================================
    public void actualizarPrueba(Prueba prueba) {

        executor.execute(() -> {
            try {
                dao.actualizarPrueba(prueba);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ELIMINAR
    // =====================================
    public void eliminarPrueba(Prueba prueba) {

        executor.execute(() -> {
            try {
                dao.eliminarPrueba(prueba);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA (LIVE DATA)
    // =====================================

    public LiveData<List<Prueba>> obtenerTodas() {
        return dao.obtenerTodasPruebas();
    }

    public LiveData<Prueba> buscarPorId(int id) {
        return dao.buscarPruebaPorId(id);
    }

    public LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion) {
        return dao.obtenerPorCompeticion(idCompeticion);
    }


}