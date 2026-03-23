package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myhipicapptfg.dao.RutaPersonalDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.RutaPersonal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutaPersonalRepository {

    private final RutaPersonalDao dao;
    private final ExecutorService executor;

    // Para comunicar errores o éxito a la Activity
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();
    private final LiveData<List<RutaPersonal>> listaRutas;

    public RutaPersonalRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.rutaPersonalDao();
        executor = Executors.newSingleThreadExecutor();
        listaRutas = dao.obtenerTodas();
    }

    public LiveData<List<RutaPersonal>> getListaRutas() {
        return listaRutas;
    }

    public LiveData<String> getMensajeStatus() {
        return mensajeStatus;
    }

    /**
     * Inserta una ruta tras verificar que el ID del propietario es válido y tiene el rol correcto.
     */
    public void insertar(RutaPersonal ruta) {
        executor.execute(() -> {
            // 1. Verificación de seguridad en el DAO
            boolean esValido = dao.esPropietarioValido(ruta.idPropietario);

            if (esValido) {
                try {
                    dao.insertar(ruta);
                    // null significa que todo salió bien
                    mensajeStatus.postValue(null);
                } catch (Exception e) {
                    mensajeStatus.postValue("Error técnico al guardar en la base de datos.");
                }
            } else {
                // Si el ID es de un Profesor, Alumno o no existe
                mensajeStatus.postValue("Denegado: El usuario con ID " + ruta.idPropietario + " no es un Propietario.");
            }
        });
    }

    public void eliminar(RutaPersonal ruta) {
        executor.execute(() -> dao.eliminar(ruta));
    }
}