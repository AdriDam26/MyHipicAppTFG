package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ClaseDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Clase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClaseRepository {

    private final ClaseDao claseDao;
    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ClaseRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        claseDao = db.claseDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarClase(Clase clase) {

        executorService.execute(() -> {

            // Validar Pista
            if (!claseDao.existePistaSync(clase.idPista)) {
                estadoOperacion.postValue("ERROR_PISTA_NO_EXISTE");
                return;
            }


            // Validar Profesor
            if (!claseDao.existeProfesorSync(clase.idProfesor)) {
                estadoOperacion.postValue("ERROR_PROFESOR_NO_EXISTE");
                return;
            }

            try {
                claseDao.insertarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarClase(Clase clase) {

        executorService.execute(() -> {
            try {
                claseDao.actualizarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarClase(Clase clase) {

        executorService.execute(() -> {
            try {
                claseDao.eliminarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA
    // =====================================

    public LiveData<List<Clase>> obtenerTodasClases() {
        return claseDao.obtenerTodasClases();
    }

    public LiveData<Clase> buscarPorId(int id) {
        return claseDao.buscarPorId(id);
    }

    public LiveData<Integer> contarClases() {
        return claseDao.contarClases();
    }


}