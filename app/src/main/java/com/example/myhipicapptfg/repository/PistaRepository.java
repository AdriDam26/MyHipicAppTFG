package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.PistaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Pista;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PistaRepository {

    private final PistaDao pistaDao;
    private final ExecutorService executorService;

    // Canal de comunicación para errores (Nombre duplicado, etc.)
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PistaRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        pistaDao = db.pistaDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // --- MÉTODOS DE ESCRITURA CON LÓGICA ---

    public void insertarPista(Pista pista) {
        executorService.execute(() -> {
            // REGLA DE NEGOCIO: El nombre de la pista debe ser único
            Pista existente = pistaDao.buscarPorNombreSync(pista.nombre);

            if (existente == null) {
                try {
                    pistaDao.insertarPista(pista);
                    errorLiveData.postValue(null);
                } catch (Exception e) {
                    errorLiveData.postValue("Error técnico al guardar la pista.");
                }
            } else {
                errorLiveData.postValue("Error: Ya existe una pista llamada '" + pista.nombre + "'.");
            }
        });
    }

    public void actualizarPista(Pista pista) {
        executorService.execute(() -> pistaDao.actualizarPista(pista));
    }

    public void eliminarPista(Pista pista) {
        executorService.execute(() -> pistaDao.eliminarPista(pista));
    }

    // --- MÉTODOS DE LECTURA ---

    public LiveData<List<Pista>> obtenerTodasPistas() {
        return pistaDao.obtenerTodasPistas();
    }

    public LiveData<Pista> buscarPorId(int id) {
        return pistaDao.buscarPorId(id);
    }

    public LiveData<List<Pista>> obtenerPistasPorEstado(String estado) {
        return pistaDao.obtenerPistasPorEstado(estado);
    }
}