package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.dao.MovimientoDao;
import com.example.myhipicapptfg.entities.Movimiento;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MovimientoRepository {

    private final MovimientoDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public MovimientoRepository(Application application) {
        dao = AppDatabase.getInstance(application).movimientoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================
    public void insertar(Movimiento m) {

        executor.execute(() -> {

            // 🔹 Validar orden único
            if (dao.existeOrdenSync(m.orden) > 0) {
                estadoOperacion.postValue("ERROR_ORDEN_DUPLICADO");
                return;
            }

            try {
                dao.insertarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ACTUALIZAR
    // =====================================
    public void actualizar(Movimiento m) {

        executor.execute(() -> {

            try {
                dao.actualizarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ELIMINAR
    // =====================================
    public void eliminar(Movimiento m) {

        executor.execute(() -> {

            try {
                dao.eliminarMovimiento(m);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA
    // =====================================

    public LiveData<List<Movimiento>> obtenerTodos() {
        return dao.obtenerTodosMovimientos();
    }

    public LiveData<Movimiento> buscarPorId(int id) {
        return dao.buscarMovimientoPorId(id);
    }

    public LiveData<List<Movimiento>> obtenerOrdenados() {
        return dao.obtenerMovimientosOrdenados();
    }
}