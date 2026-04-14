package com.example.myhipicapptfg.repository;

import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myhipicapptfg.dao.RutaPersonalDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.RutaPersonal;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RutaPersonalRepository {

    private final RutaPersonalDao dao;
    private final ExecutorService executor;

    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();
    private final LiveData<List<RutaPersonal>> listaRutas;

    public RutaPersonalRepository(Application application) {
        TestDatabase db = TestDatabase.getInstance(application);
        dao = db.rutaPersonalDao();
        executor = Executors.newSingleThreadExecutor();
        listaRutas = dao.obtenerTodas();
    }

    /**
     * Método unificado para guardar todo de una vez.
     * Si el usuario cancela en el diálogo, este método nunca se llama.
     */
    public void guardarRutaCompleta(RutaPersonal ruta, List<CoordenadaRuta> puntos) {
        executor.execute(() -> {

            // Verificación de seguridad
            if (!dao.esPropietarioValido(ruta.idPropietario)) {
                mensajeStatus.postValue("Denegado: El usuario con ID " + ruta.idPropietario + " no es un Propietario.");
                return;
            }

            try {
                // Ejecutamos la transacción atómica
                long id = dao.guardarRutaConPuntos(ruta, puntos);

                // Notificamos a la UI que ya terminó (ahora es seguro hacer finish())
                idInsertado.postValue(id);
                mensajeStatus.postValue(null);
            } catch (Exception e) {
                mensajeStatus.postValue("Error al guardar la ruta: " + e.getMessage());
            }
        });
    }

    public LiveData<List<RutaPersonal>> getListaRutas() { return listaRutas; }
    public LiveData<String> getMensajeStatus() { return mensajeStatus; }
    public LiveData<Long> getIdInsertado() { return idInsertado; }

    public void eliminar(RutaPersonal ruta) {
        executor.execute(() -> dao.eliminar(ruta));
    }
    public LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario) {
        return dao.obtenerPorPropietario(idPropietario);
    }
}