package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myhipicapptfg.dao.ProfesorDisciplinaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.ProfesorDisciplina;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfesorDisciplinaRepository {

    private final ProfesorDisciplinaDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public ProfesorDisciplinaRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.profesorDisciplinaDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    public void insertar(ProfesorDisciplina pd) {
        executor.execute(() -> {
            boolean profeOk = dao.esProfesorValido(pd.idProfesor);
            boolean discOk = dao.existeDisciplina(pd.idDisciplina);

            if (profeOk && discOk) {
                try {
                    dao.insertar(pd);
                    mensajeStatus.postValue(null); // Éxito
                } catch (Exception e) {
                    mensajeStatus.postValue("Error: Esta vinculación ya existe.");
                }
            } else {
                if (!profeOk) mensajeStatus.postValue("Error: El usuario no es un Profesor válido.");
                else mensajeStatus.postValue("Error: La Disciplina no existe.");
            }
        });
    }
}