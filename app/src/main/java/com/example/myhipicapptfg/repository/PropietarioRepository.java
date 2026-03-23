package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.PropietarioDao;
import com.example.myhipicapptfg.dao.UsuarioDao; // Necesario para validar
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Propietario;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PropietarioRepository {

    private final PropietarioDao propietarioDao;
    private final UsuarioDao usuarioDao; // Referencia al DAO de Usuario
    private final ExecutorService executorService;

    // LiveData para notificar errores a la Activity/ViewModel
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    public PropietarioRepository(@NonNull Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        propietarioDao = db.propietarioDao();
        usuarioDao = db.usuarioDao(); // Inicialización
        executorService = Executors.newFixedThreadPool(4);
    }

    public LiveData<String> getErrorLiveData() {
        return errorLiveData;
    }

    // --- MÉTODOS DE ESCRITURA CON VALIDACIÓN ---

    public void insertarPropietarioSeguro(Propietario propietario) {
        executorService.execute(() -> {
            // 1. Validar que el usuario existe
            Usuario usuarioAsociado = usuarioDao.buscarPorIdSync(propietario.idPropietario);

            if (usuarioAsociado != null) {
                // 2. Opcional: Validar que sea tipo PROPIETARIO
                if (!Usuario.TIPO_PROPIETARIO.equals(usuarioAsociado.tipo)) {
                    errorLiveData.postValue("Error: El usuario no está registrado como Propietario.");
                    return;
                }

                // 3. Proceder con la inserción
                try {
                    propietarioDao.insertarPropietario(propietario);
                    errorLiveData.postValue(null); // Limpiar errores
                } catch (Exception e) {
                    errorLiveData.postValue("Error al insertar en la tabla Propietario.");
                }
            } else {
                // 4. Fallo de integridad
                errorLiveData.postValue("Error: No existe un Usuario con ID " + propietario.idPropietario);
            }
        });
    }

    // --- MÉTODOS RESTANTES ---

    public void actualizarPropietario(Propietario propietario) {
        executorService.execute(() -> propietarioDao.actualizarPropietario(propietario));
    }

    public void eliminarPropietario(Propietario propietario) {
        executorService.execute(() -> propietarioDao.eliminarPropietario(propietario));
    }

    public LiveData<List<Propietario>> obtenerTodosPropietarios() {
        return propietarioDao.obtenerTodosPropietarios();
    }

    public LiveData<Propietario> buscarPorId(int id) {
        return propietarioDao.buscarPorId(id);
    }

    public LiveData<Integer> contarPropietarios() {
        return propietarioDao.contarPropietarios();
    }
}
