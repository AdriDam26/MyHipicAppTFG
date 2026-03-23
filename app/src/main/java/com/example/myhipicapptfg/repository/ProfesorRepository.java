package com.example.myhipicapptfg.repository;



import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ProfesorDao;
import com.example.myhipicapptfg.dao.UsuarioDao; // Importante para la validación
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ProfesorRepository {

    private final ProfesorDao profesorDao;
    private final UsuarioDao usuarioDao; // El otro DAO necesario
    private final ExecutorService executorService;

    // Canal para errores de lógica de negocio
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public ProfesorRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        profesorDao = db.profesorDao();
        usuarioDao = db.usuarioDao(); // Inicializamos ambos
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // --- OPERACIÓN CON REGLA DE NEGOCIO ---

    public void insertarProfesorSeguro(Profesor profesor) {
        executorService.execute(() -> {
            // 1. REGLA: ¿Existe el usuario base?
            Usuario usuarioBase = usuarioDao.buscarPorIdSync(profesor.idProfesor);

            if (usuarioBase != null) {
                // 2. Verificamos si el tipo de usuario es correcto (OPCIONAL pero recomendado)
                if (!Usuario.TIPO_PROFESOR.equals(usuarioBase.tipo)) {
                    errorLiveData.postValue("Error: El usuario existe pero no está marcado como tipo PROFESOR.");
                    return;
                }

                // 3. Si todo ok, insertamos
                try {
                    profesorDao.insertarProfesor(profesor);
                    errorLiveData.postValue(null); // Limpiamos errores previos
                } catch (Exception e) {
                    errorLiveData.postValue("Error técnico al insertar en la tabla Profesor.");
                }
            } else {
                // 4. Fallo de integridad
                errorLiveData.postValue("Error: No se puede crear el Profesor. El ID de Usuario "
                        + profesor.idProfesor + " no existe en el sistema.");
            }
        });
    }

    // --- RESTO DE OPERACIONES ---

    public void actualizarProfesor(Profesor profesor) {
        executorService.execute(() -> profesorDao.actualizarProfesor(profesor));
    }

    public void eliminarProfesor(Profesor profesor) {
        executorService.execute(() -> profesorDao.eliminarProfesor(profesor));
    }

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