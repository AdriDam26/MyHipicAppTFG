package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.AlumnoDao;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class AlumnoRepository {

    private final AlumnoDao alumnoDao;
    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;

    private final ParticipacionDao participacionDao;
    private final NotaMovimientoDao notaDao;


    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public AlumnoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        alumnoDao = db.alumnoDao();
        usuarioDao = db.usuarioDao();
        participacionDao = db.participacionDao();
        notaDao          = db.notaMovimientoDao();


        executorService = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarAlumno(Alumno alumno) {

        executorService.execute(() -> {

            // 1️⃣ Verificar que existe el Usuario
            Usuario usuarioExistente = usuarioDao.buscarPorIdSync(alumno.idAlumno);

            if (usuarioExistente == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }

            // 2️⃣ Verificar que el tipo es ALUMNO
            if (!Usuario.TIPO_ALUMNO.equals(usuarioExistente.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            try {
                alumnoDao.insertarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarAlumno(Alumno alumno) {
        executorService.execute(() -> {
            try {
                alumnoDao.actualizarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarAlumno(Alumno alumno) {
        executorService.execute(() -> {
            try {
                alumnoDao.eliminarAlumno(alumno);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 LECTURA (LiveData)
    // =====================================

    public LiveData<List<Alumno>> obtenerTodosAlumnos() {
        return alumnoDao.obtenerTodosAlumnos();
    }

    public LiveData<Alumno> buscarPorId(int id) {
        return alumnoDao.buscarPorId(id);
    }


}