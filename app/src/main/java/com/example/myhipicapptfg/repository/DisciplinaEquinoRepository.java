package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.DisciplinaEquinoDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.DisciplinaEquino;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisciplinaEquinoRepository {

    private final DisciplinaEquinoDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public DisciplinaEquinoRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.disciplinaEquinoDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(DisciplinaEquino de) {
        executor.execute(() -> {
            boolean equinoOk = dao.existeEquino(de.idEquino);
            boolean disciplinaOk = dao.existeDisciplina(de.idDisciplina);

            if (equinoOk && disciplinaOk) {
                try {
                    dao.insertar(de);
                    mensajeStatus.postValue(null); // Éxito
                } catch (Exception e) {
                    mensajeStatus.postValue("Error: Esta relación ya existe.");
                }
            } else {
                if (!equinoOk) mensajeStatus.postValue("Error: El Equino no existe.");
                else mensajeStatus.postValue("Error: La Disciplina no existe.");
            }
        });
    }
}