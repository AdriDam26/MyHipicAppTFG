package com.example.myhipicapptfg.repository;



import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.PlantillaPruebaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.PlantillaPrueba;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PlantillaPruebaRepository {

    private final PlantillaPruebaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public PlantillaPruebaRepository(Application application) {
        dao = TestDatabase.getInstance(application).plantillaPruebaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(PlantillaPrueba prueba) {
        executor.execute(() -> {
            // 1. Validar que la disciplina existe
            if (!dao.existeDisciplina(prueba.idDisciplina)) {
                mensajeStatus.postValue("Error: La Disciplina no existe.");
                return;
            }

            // 2. Validar nombre duplicado en esa disciplina
            if (dao.existeNombreEnDisciplina(prueba.nombre, prueba.idDisciplina)) {
                mensajeStatus.postValue("Error: Ya existe una plantilla con ese nombre para esta disciplina.");
                return;
            }

            try {
                dao.insertarPrueba(prueba);
                mensajeStatus.postValue(null); // Éxito
            } catch (Exception e) {
                mensajeStatus.postValue("Error al guardar la plantilla.");
            }
        });
    }

    public LiveData<List<PlantillaPrueba>> getTodas() {
        return dao.obtenerTodas();
    }
}