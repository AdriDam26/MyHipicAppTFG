package com.example.myhipicapptfg.datos.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class PruebaRepository {

    private final PruebaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public PruebaRepository(@NonNull Application application) {
        dao      = AppDatabase.getInstance(application).pruebaDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() { return estadoOperacion; }

    public LiveData<List<Prueba>> obtenerTodas() {
        return dao.obtenerTodasPruebas();
    }

    public LiveData<Prueba> buscarPorId(int id) {
        return dao.buscarPruebaPorId(id);
    }

    public LiveData<List<Prueba>> obtenerPorCompeticion(int idCompeticion) {
        return dao.obtenerPorCompeticion(idCompeticion);
    }

    public LiveData<List<Movimiento>> obtenerMovimientosPorPrueba(int idPrueba) {
        return dao.obtenerMovimientosPorPrueba(idPrueba);
    }

    public LiveData<Boolean> isPublicado(int idPrueba) {
        return dao.isPublicado(idPrueba);
    }

    public void actualizarPublicado(int idPrueba, boolean publicado) {
        executor.execute(() -> dao.actualizarPublicado(idPrueba, publicado));
    }


    public void insertarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        executor.execute(() -> {

            if (!dao.existeCompeticionSync(prueba.idCompeticion)) {
                estadoOperacion.postValue("ERROR_COMPETICION_NO_EXISTE");
                return;
            }
            try {
                dao.insertarPruebaConMovimientos(prueba, movimientos);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void actualizarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        executor.execute(() -> {
            if (dao.existeNombreExcluyendoSync(prueba.nombre, prueba.idPrueba)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }
            try {
                dao.actualizarPruebaConMovimientos(prueba, movimientos);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public void eliminarPrueba(Prueba prueba) {
        executor.execute(() -> {
            try {
                dao.eliminarPrueba(prueba);
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}