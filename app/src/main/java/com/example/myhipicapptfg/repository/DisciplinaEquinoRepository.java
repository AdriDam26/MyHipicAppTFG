package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public void insertarSync(DisciplinaEquino de) {
        try {
            // 1. Verificación de integridad (¿Existen los IDs?)
            boolean equinoOk = dao.existeEquino(de.idEquino);
            boolean disciplinaOk = dao.existeDisciplina(de.idDisciplina);

            if (equinoOk && disciplinaOk) {
                dao.insertar(de);
                // No posteamos null aquí para no borrar mensajes de éxito del Equino
            } else {
                if (!equinoOk) mensajeStatus.postValue("Error integridad: El Equino no existe.");
                else mensajeStatus.postValue("Error integridad: La Disciplina no existe.");
            }
        } catch (Exception e) {
            mensajeStatus.postValue("Error al insertar relación Disciplina-Equino: " + e.getMessage());
        }
    }


}