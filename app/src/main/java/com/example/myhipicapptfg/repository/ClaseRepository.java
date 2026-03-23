package com.example.myhipicapptfg.repository;



import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ClaseDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Clase;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClaseRepository {

    private final ClaseDao claseDao;
    private final ExecutorService executorService;

    // Canal para errores de integridad (Solo se usa en insertar)
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ClaseRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        claseDao = db.claseDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    /**
     * Devuelve el canal de errores. La UI debe observar esto para saber si
     * la inserción falló por falta de alguna dependencia (Pista, Disciplina o Profesor).
     */
    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // --- MÉTODOS DE ESCRITURA ---

    /**
     * Inserta una clase validando que existan sus llaves foráneas.
     * Si falta alguna, el error se publica en errorLiveData.
     */
    public void insertarClase(Clase clase) {
        executorService.execute(() -> {
            // 1. Validar Pista
            if (!claseDao.existePistaSync(clase.idPista)) {
                errorLiveData.postValue("Error: No existe la Pista (ID: " + clase.idPista + ")");
                return;
            }

            // 2. Validar Disciplina
            if (!claseDao.existeDisciplinaSync(clase.idDisciplina)) {
                errorLiveData.postValue("Error: No existe la Disciplina (ID: " + clase.idDisciplina + ")");
                return;
            }

            // 3. Validar Profesor
            if (!claseDao.existeProfesorSync(clase.idProfesor)) {
                errorLiveData.postValue("Error: No existe el Profesor (ID: " + clase.idProfesor + ")");
                return;
            }

            // Si todo es correcto, procedemos a insertar
            try {
                claseDao.insertarClase(clase);
                errorLiveData.postValue(null); // Notificar éxito
            } catch (Exception e) {
                errorLiveData.postValue("Error técnico al guardar la clase.");
            }
        });
    }

    public void actualizarClase(Clase clase) {
        executorService.execute(() -> claseDao.actualizarClase(clase));
    }

    public void eliminarClase(Clase clase) {
        executorService.execute(() -> claseDao.eliminarClase(clase));
    }

    // --- MÉTODOS DE LECTURA (LiveData) ---

    public LiveData<List<Clase>> obtenerTodasClases() {
        return claseDao.obtenerTodasClases();
    }

    public LiveData<Clase> buscarPorId(int id) {
        return claseDao.buscarPorId(id);
    }
}
