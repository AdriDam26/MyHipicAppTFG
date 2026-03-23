package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.DisciplinaDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Disciplina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DisciplinaRepository {

    private final DisciplinaDao disciplinaDao;
    private final ExecutorService executorService;

    // Este LiveData solo informará de errores al INSERTAR
    private final MutableLiveData<String> errorInsertarLiveData = new MutableLiveData<>();

    public DisciplinaRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        disciplinaDao = db.disciplinaDao();
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorInsertarLiveData() {
        return errorInsertarLiveData;
    }

    // --- EL ÚNICO MÉTODO CON VALIDACIÓN Y ERRORES ---
    public void insertarDisciplinaSegura(Disciplina disciplina) {
        executorService.execute(() -> {
            // 1. REGLA DE NEGOCIO: ¿Ya existe en la DB?
            Disciplina existente = disciplinaDao.buscarPorNombreSync(disciplina.nombre);

            if (existente == null) {
                try {
                    disciplinaDao.insertarDisciplina(disciplina);
                    // Notificamos éxito mandando un null (sin errores)
                    errorInsertarLiveData.postValue(null);
                } catch (Exception e) {
                    errorInsertarLiveData.postValue("Error técnico: No se pudo guardar en la base de datos.");
                }
            } else {
                // Notificamos el error de duplicidad
                errorInsertarLiveData.postValue("La disciplina '" + disciplina.nombre + "' ya existe.");
            }
        });
    }

    public void actualizarDisciplina(Disciplina disciplina) {
        executorService.execute(() -> disciplinaDao.actualizarDisciplina(disciplina));
    }

    public void eliminarDisciplina(Disciplina disciplina) {
        executorService.execute(() -> disciplinaDao.eliminarDisciplina(disciplina));
    }

    // --- LECTURAS REACTIVAS ---

    public LiveData<List<Disciplina>> obtenerTodasDisciplinas() {
        return disciplinaDao.obtenerTodasDisciplinas();
    }

    public LiveData<Disciplina> buscarPorId(int id) {
        return disciplinaDao.buscarPorId(id);
    }
}