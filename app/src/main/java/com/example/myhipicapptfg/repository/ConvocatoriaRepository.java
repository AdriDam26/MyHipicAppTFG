package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ConvocatoriaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Convocatoria;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConvocatoriaRepository {

    private final ConvocatoriaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public ConvocatoriaRepository(Application application) {
        dao = TestDatabase.getInstance(application).convocatoriaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() { return mensajeStatus; }

    public void insertar(Convocatoria conv) {
        executor.execute(() -> {
            // 1. Validar Existencias
            if (!dao.existeCompeticion(conv.idCompeticion)) {
                mensajeStatus.postValue("Error: La Competición no existe.");
                return;
            }
            if (!dao.existePrueba(conv.idPrueba)) {
                mensajeStatus.postValue("Error: La Plantilla de Prueba no existe.");
                return;
            }

            // 2. Validar Duplicidad de Categoría
            if (dao.existeCategoriaEnCompeticion(conv.idCompeticion, conv.categoria)) {
                mensajeStatus.postValue("Error: La categoría '" + conv.categoria + "' ya está convocada en esta competición.");
                return;
            }

            try {
                dao.insertarConvocatoria(conv);
                mensajeStatus.postValue(null);
            } catch (Exception e) {
                mensajeStatus.postValue("Error al crear la convocatoria.");
            }
        });
    }
}