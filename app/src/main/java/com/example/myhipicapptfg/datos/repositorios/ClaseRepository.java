package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.ClaseDao;
import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.dao.ProfesorDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

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
        executorService = AppDatabase.getDatabaseExecutor();
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


    public LiveData<List<Clase>> getClasesPorProfesor(int idProfesor) {
        return claseDao.obtenerClasesPorProfesor(idProfesor);
    }
}