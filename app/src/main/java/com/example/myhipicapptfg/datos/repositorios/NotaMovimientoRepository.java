package com.example.myhipicapptfg.datos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class NotaMovimientoRepository {

    private final NotaMovimientoDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public NotaMovimientoRepository(Application application) {
        dao = AppDatabase.getInstance(application).notaMovimientoDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================
    public void insertar(NotaMovimiento n) {

        executor.execute(() -> {


            try {
                dao.insertarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ACTUALIZAR
    // =====================================
    public void actualizar(NotaMovimiento n) {

        executor.execute(() -> {
            try {
                dao.actualizarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ELIMINAR
    // =====================================
    public void eliminar(NotaMovimiento n) {

        executor.execute(() -> {
            try {
                dao.eliminarNotaMovimiento(n);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA
    // =====================================

    public LiveData<List<NotaMovimiento>> obtenerTodas() {
        return dao.obtenerTodasNotas();
    }

    public LiveData<List<NotaMovimiento>> obtenerPorParticipacion(int idParticipacion) {
        return dao.obtenerPorParticipacion(idParticipacion);
    }

    public LiveData<List<NotaMovimiento>> obtenerPorMovimiento(int idMovimiento) {
        return dao.obtenerPorMovimiento(idMovimiento);
    }



    // =====================================
    // 🔹 SYNC (UTILIDADES)
    // =====================================

    public double calcularTotalParticipacion(int idParticipacion) {
        return dao.totalParticipacionSync(idParticipacion);
    }
}