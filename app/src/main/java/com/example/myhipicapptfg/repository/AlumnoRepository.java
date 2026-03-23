package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.AlumnoDao;
import com.example.myhipicapptfg.dao.UsuarioDao; // Necesitamos este DAO
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AlumnoRepository {

    private final AlumnoDao alumnoDao;
    private final UsuarioDao usuarioDao; // Añadimos el DAO de Usuario
    private final ExecutorService executorService;

    // Para comunicar errores a la UI
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public AlumnoRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        alumnoDao = db.alumnoDao();
        usuarioDao = db.usuarioDao(); // Inicializamos el DAO de Usuario
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }



    public void insertarAlumno(Alumno alumno) {
        executorService.execute(() -> {
            // 1. REGLA DE NEGOCIO: ¿Existe el ID en la tabla Usuario?
            // Nota: buscarPorIdSync debe estar definido en tu UsuarioDao
            Usuario usuarioExistente = usuarioDao.buscarPorIdSync(alumno.idAlumno);

            if (usuarioExistente != null) {

                if (!Usuario.TIPO_ALUMNO.equals(usuarioExistente.tipo)) {
                    errorLiveData.postValue("Error: El usuario existe pero no está marcado como tipo PROFESOR.");
                    return;
                }

                // 2. Si existe, procedemos a insertar en la tabla Alumno
                try {
                    alumnoDao.insertarAlumno(alumno);
                    errorLiveData.postValue(null); // Todo ok
                } catch (Exception e) {
                    errorLiveData.postValue("Error al insertar Alumno");
                }
            } else {
                // 3. Si no existe, avisamos del error de integridad
                errorLiveData.postValue("Error: No se puede crear el Alumno. El ID de Usuario "
                        + alumno.idAlumno + " no existe.");
            }
        });
    }

    // --- RESTO DE MÉTODOS ---

    public void actualizarAlumno(Alumno alumno) {
        executorService.execute(() -> alumnoDao.actualizarAlumno(alumno));
    }

    public void eliminarAlumno(Alumno alumno) {
        executorService.execute(() -> alumnoDao.eliminarAlumno(alumno));
    }

    public LiveData<List<Alumno>> obtenerTodosAlumnos() {
        return alumnoDao.obtenerTodosAlumnos();
    }

    public LiveData<Alumno> buscarPorId(int id) {
        return alumnoDao.buscarPorId(id);
    }

    public Alumno buscarPorIdSync(int id) {
        return alumnoDao.buscarPorIdSync(id);
    }
}