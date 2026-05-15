package com.example.myhipicapptfg.datos.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.CompeticionDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class CompeticionRepository {

    private final CompeticionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public CompeticionRepository(@NonNull Application application) {
        dao      = AppDatabase.getInstance(application).competicionDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() { return estadoOperacion; }

    public LiveData<List<Competicion>> obtenerTodas() {
        return dao.obtenerTodas();
    }

    public LiveData<Competicion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public void insertarCompeticion(Competicion c) {
        executor.execute(() -> {
            if (dao.existeNombreSync(c.nombre)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }
            try {
                dao.insertarCompeticion(c);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void actualizarCompeticion(Competicion c) {
        executor.execute(() -> {
            if (dao.existeNombreExcluyendoSync(c.nombre, c.idCompeticion)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }
            try {
                dao.actualizarCompeticion(c);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void eliminarCompeticion(Competicion c) {
        executor.execute(() -> {
            try {
                dao.eliminarCompeticion(c);
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}