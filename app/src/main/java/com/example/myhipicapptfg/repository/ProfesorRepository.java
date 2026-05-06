package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ProfesorDao;
import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfesorRepository {

    private final ProfesorDao profesorDao;
    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ProfesorRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        profesorDao = db.profesorDao();
        usuarioDao = db.usuarioDao();

        executorService = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarProfesor(Profesor profesor) {

        executorService.execute(() -> {

            // 1️⃣ Verificar que existe el Usuario base
            Usuario usuarioBase = usuarioDao.buscarPorIdSync(profesor.idProfesor);

            if (usuarioBase == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }

            // 2️⃣ Verificar que el tipo sea PROFESOR
            if (!Usuario.TIPO_PROFESOR.equals(usuarioBase.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            try {
                profesorDao.insertarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarProfesor(Profesor profesor) {
        executorService.execute(() -> {
            try {
                profesorDao.actualizarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarProfesor(Profesor profesor) {
        executorService.execute(() -> {
            try {
                profesorDao.eliminarProfesor(profesor);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA (LiveData)
    // =====================================

    public LiveData<List<Profesor>> obtenerTodosProfesores() {
        return profesorDao.obtenerTodosProfesores();
    }

    public LiveData<Profesor> buscarPorId(int id) {
        return profesorDao.buscarPorId(id);
    }

    public LiveData<Integer> contarProfesores() {
        return profesorDao.contarProfesores();
    }
}