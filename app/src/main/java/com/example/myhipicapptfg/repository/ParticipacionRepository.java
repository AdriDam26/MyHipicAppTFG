package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ParticipacionDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Participacion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipacionRepository {

    private final ParticipacionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public ParticipacionRepository(Application application) {
        dao = TestDatabase.getInstance(application).participacionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() { return mensajeStatus; }

    public void insertar(Participacion p) {
        executor.execute(() -> {
            // 1. Validar Existencias
            if (!dao.existeAlumno(p.idAlumno)) {
                mensajeStatus.postValue("Error: El Alumno no existe.");
                return;
            }
            if (!dao.existeEquino(p.idEquino)) {
                mensajeStatus.postValue("Error: El Equino no existe.");
                return;
            }
            if (!dao.existeConvocatoria(p.idConvocatoria)) {
                mensajeStatus.postValue("Error: La Convocatoria no existe.");
                return;
            }

            // 2. Validar Duplicidad
            if (dao.existeBinomioEnConvocatoria(p.idAlumno, p.idEquino, p.idConvocatoria)) {
                mensajeStatus.postValue("Error: Este binomio ya está inscrito en esta convocatoria.");
                return;
            }

            try {
                dao.insertarParticipacion(p);
                mensajeStatus.postValue(null);
            } catch (Exception e) {
                mensajeStatus.postValue("Error al procesar la inscripción.");
            }
        });
    }
}