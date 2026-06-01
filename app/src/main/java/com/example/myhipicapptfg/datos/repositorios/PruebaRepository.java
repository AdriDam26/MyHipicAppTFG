package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
                List<Movimiento> existentes = dao.obtenerMovimientosPorPruebaSync(prueba.idPrueba);

                Set<Integer> idsEntrantes = new HashSet<>();
                for (Movimiento m : movimientos) {
                    if (m.idMovimiento > 0) idsEntrantes.add(m.idMovimiento);
                }

                List<Movimiento> aEliminar   = new ArrayList<>();
                List<Movimiento> aActualizar = new ArrayList<>();
                List<Movimiento> aInsertar   = new ArrayList<>();

                for (Movimiento e : existentes) {
                    if (!idsEntrantes.contains(e.idMovimiento)) aEliminar.add(e);
                }
                for (Movimiento m : movimientos) {
                    m.idPrueba = prueba.idPrueba;
                    if (m.idMovimiento > 0) aActualizar.add(m);
                    else                    aInsertar.add(m);
                }

                dao.actualizarPruebaConMovimientos(prueba, aActualizar, aInsertar, aEliminar);
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