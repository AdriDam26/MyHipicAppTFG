package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.CompeticionDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Competicion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompeticionRepository {

    private final CompeticionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public CompeticionRepository(Application application) {
        dao = TestDatabase.getInstance(application).competicionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(Competicion comp) {
        executor.execute(() -> {
            // 1. Validar Disciplina
            if (!dao.existeDisciplina(comp.idDisciplina)) {
                mensajeStatus.postValue("Error: La Disciplina no existe.");
                return;
            }

            // 2. Validar Nombre Único (Evitar crash por Unique Constraint)
            if (dao.existeNombre(comp.nombre)) {
                mensajeStatus.postValue("Error: Ya existe una competición con el nombre '" + comp.nombre + "'.");
                return;
            }

            try {
                dao.insertarCompeticion(comp);
                mensajeStatus.postValue(null); // Éxito
            } catch (Exception e) {
                mensajeStatus.postValue("Error técnico al guardar la competición.");
            }
        });
    }
}