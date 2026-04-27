package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ClaseDao;
import com.example.myhipicapptfg.dao.PistaDao;
import com.example.myhipicapptfg.dao.ProfesorDao;
import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClaseRepository {

    private final ClaseDao claseDao;
    private final ExecutorService executorService;

    private ProfesorDao profesorDao;
    private PistaDao pistaDao;

    private UsuarioDao usuarioDao;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ClaseRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        claseDao = db.claseDao();
        profesorDao = db.profesorDao();
        pistaDao = db.pistaDao();
        usuarioDao = db.usuarioDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    public LiveData<List<Profesor>> obtenerTodosLosProfesores() {
        return profesorDao.obtenerTodosProfesores(); // Llama al método del DAO de Profesores
    }

    public LiveData<List<Pista>> obtenerTodasLasPistas() {
        return pistaDao.obtenerTodasPistas(); // Llama al método del DAO de Pistas
    }

    public LiveData<List<Usuario>> obtenerTodosLosUsuarios() {
        return usuarioDao.obtenerTodosUsuarios();
    }


    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarClase(Clase clase) {

        executorService.execute(() -> {

            // Validar Pista
            if (!claseDao.existePistaSync(clase.idPista)) {
                estadoOperacion.postValue("ERROR_PISTA_NO_EXISTE");
                return;
            }


            // Validar Profesor
            if (!claseDao.existeProfesorSync(clase.idProfesor)) {
                estadoOperacion.postValue("ERROR_PROFESOR_NO_EXISTE");
                return;
            }

            try {
                claseDao.insertarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarClase(Clase clase) {

        executorService.execute(() -> {
            try {
                claseDao.actualizarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarClase(Clase clase) {

        executorService.execute(() -> {
            try {
                claseDao.eliminarClase(clase);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA
    // =====================================

    public LiveData<List<Clase>> obtenerTodasClases() {
        return claseDao.obtenerTodasClases();
    }

    public LiveData<Clase> buscarPorId(int id) {
        return claseDao.buscarPorId(id);
    }

    public LiveData<Integer> contarClases() {
        return claseDao.contarClases();
    }


}