package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.CoordenadaRutaDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar el acceso a los datos de la entidad CoordenadaRuta.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y la base de datos Room,
 * centralizando la lógica de acceso a los puntos GPS de las rutas.
 *
 * Responsabilidades principales:
 * - Acceder a los datos mediante CoordenadaRutaDao.
 * - Ejecutar operaciones de base de datos en segundo plano.
 * - Insertar, eliminar y consultar coordenadas GPS.
 * - Notificar el estado de las operaciones mediante LiveData.
 *
 */
public class CoordenadaRutaRepository {

    /**
     * DAO encargado de las operaciones sobre la tabla CoordenadaRuta.
     */
    private final CoordenadaRutaDao coordenadaRutaDao;

    /**
     * ExecutorService utilizado para ejecutar operaciones
     * en segundo plano sin bloquear la interfaz de usuario.
     */
    private final ExecutorService executorService;

    /**
     * LiveData que informa del resultado de las operaciones.
     *
     * Posibles valores:
     * - EXITO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa la base de datos, obtiene el DAO correspondiente
     * y prepara el ExecutorService para operaciones en background.
     *
     * @param application Contexto de la aplicación.
     */
    public CoordenadaRutaRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        coordenadaRutaDao = db.coordenadaRutaDao();
        executorService = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * Permite a la UI observar si la operación fue correcta o falló.
     *
     * @return LiveData con el estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }


    /**
     * Inserta una única coordenada en la base de datos.
     *
     * Se ejecuta en segundo plano mediante ExecutorService.
     *
     * @param coordenada Punto GPS a insertar.
     */
    public void insertarCoordenada(CoordenadaRuta coordenada) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.insertar(coordenada);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Inserta una lista completa de coordenadas GPS.
     *
     * Útil cuando se guarda una ruta completa de una sola vez.
     *
     * @param lista Lista de coordenadas.
     */
    public void insertarListaCoordenadas(List<CoordenadaRuta> lista) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.insertarLista(lista);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina todas las coordenadas asociadas a una ruta.
     *
     * @param idRuta Identificador de la ruta.
     */
    public void eliminarCoordenadasPorRuta(int idRuta) {
        executorService.execute(() -> {
            try {
                coordenadaRutaDao.eliminarPorRuta(idRuta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todas las coordenadas asociadas a una ruta.
     *
     * El resultado es observable para actualizar la UI automáticamente
     * cuando los datos cambien.
     *
     * @param idRuta ID de la ruta.
     * @return Lista observable de coordenadas GPS.
     */
    public LiveData<List<CoordenadaRuta>> obtenerCoordenadasPorRuta(int idRuta) {
        return coordenadaRutaDao.obtenerPorRuta(idRuta);
    }


}