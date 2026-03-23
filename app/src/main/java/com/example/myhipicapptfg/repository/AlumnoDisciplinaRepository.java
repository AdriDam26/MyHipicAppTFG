package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.AlumnoDisciplinaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlumnoDisciplinaRepository {

    private final AlumnoDisciplinaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public AlumnoDisciplinaRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.alumnoDisciplinaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(AlumnoDisciplina ad) {
        executor.execute(() -> {
            // VALIDACIÓN DOBLE
            boolean alumnoOk = dao.esAlumnoValido(ad.idAlumno);
            boolean disciplinaOk = dao.existeDisciplina(ad.idDisciplina);

            if (alumnoOk && disciplinaOk) {
                try {
                    dao.insertar(ad);
                    mensajeStatus.postValue(null); // Éxito
                } catch (Exception e) {
                    mensajeStatus.postValue("Error: Ya existe esta relación o error de DB.");
                }
            } else {
                if (!alumnoOk) mensajeStatus.postValue("Error: El Alumno no es válido o no existe.");
                else mensajeStatus.postValue("Error: La Disciplina no existe.");
            }
        });
    }
}