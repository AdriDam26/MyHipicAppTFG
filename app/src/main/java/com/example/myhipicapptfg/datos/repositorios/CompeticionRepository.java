package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.CompeticionDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar todas las operaciones relacionadas
 * con la entidad Competicion.
 *
 * Esta clase implementa el patrón Repository dentro de la arquitectura MVVM,
 * actuando como intermediaria entre los ViewModel y el DAO de competiciones.
 *
 * Su responsabilidad principal es centralizar la lógica de acceso a datos,
 * aplicar validaciones de negocio antes de realizar modificaciones en la
 * base de datos y comunicar el resultado de las operaciones a la interfaz.
 *
 * Entre las funcionalidades proporcionadas se encuentran:
 *
 * - Consulta de competiciones registradas.
 * - Búsqueda de competiciones por identificador.
 * - Inserción de nuevas competiciones.
 * - Actualización de competiciones existentes.
 * - Eliminación de competiciones.
 *
 * Todas las operaciones de escritura se ejecutan en segundo plano mediante
 * un ExecutorService para evitar bloqueos en el hilo principal de Android.
 */
public class CompeticionRepository {

    /**
     * DAO encargado de las operaciones de acceso a datos
     * relacionadas con la entidad Competicion.
     */
    private final CompeticionDao dao;

    /**
     * Executor utilizado para ejecutar operaciones de base
     * de datos fuera del hilo principal.
     */
    private final ExecutorService executor;

    /**
     * LiveData utilizado para comunicar a la interfaz el
     * resultado de las operaciones realizadas.
     *
     * Posibles valores:
     * - EXITO
     * - ERROR_NOMBRE_DUPLICADO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa el DAO de competiciones y obtiene la instancia
     * compartida del ExecutorService utilizada para ejecutar
     * tareas en segundo plano.
     *
     * @param application Contexto global de la aplicación.
     */
    public CompeticionRepository(@NonNull Application application) {
        dao      = AppDatabase.getInstance(application).competicionDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada
     * sobre las competiciones.
     *
     * Permite a los ViewModel y a la interfaz reaccionar
     * ante operaciones exitosas o errores.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() { return estadoOperacion; }

    /**
     * Obtiene todas las competiciones registradas en el sistema.
     *
     * El resultado se devuelve mediante LiveData para que
     * cualquier modificación en la base de datos se refleje
     * automáticamente en la interfaz de usuario.
     *
     * @return Lista observable de competiciones.
     */
    public LiveData<List<Competicion>> obtenerTodas() {
        return dao.obtenerTodas();
    }

    /**
     * Busca una competición a partir de su identificador.
     *
     * @param id Identificador de la competición.
     * @return Competición correspondiente al identificador indicado.
     */
    public LiveData<Competicion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    /**
     * Inserta una nueva competición en la base de datos.
     *
     * Antes de realizar la inserción se comprueba que no exista
     * otra competición registrada con el mismo nombre.
     *
     * Si se detecta un nombre duplicado, la operación se cancela
     * y se informa del error mediante estadoOperacion.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param c Competición que se desea registrar.
     */
    public void insertarCompeticion(Competicion c) {
        executor.execute(() -> {
            if (dao.existeNombreSync(c.nombre)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }
            try {
                dao.insertarCompeticion(c);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza la información de una competición existente.
     *
     * Antes de realizar la actualización se verifica que el
     * nuevo nombre no coincida con el de otra competición
     * distinta ya registrada en el sistema.
     *
     * Si se detecta un conflicto de nombres, la operación
     * se cancela y se informa del error correspondiente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param c Competición con los datos actualizados.
     */
    public void actualizarCompeticion(Competicion c) {
        executor.execute(() -> {
            if (dao.existeNombreExcluyendoSync(c.nombre, c.idCompeticion)) {
                estadoOperacion.postValue("ERROR_NOMBRE_DUPLICADO");
                return;
            }
            try {
                dao.actualizarCompeticion(c);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina una competición de la base de datos.
     *
     * La operación se ejecuta de forma asíncrona para
     * evitar bloquear el hilo principal de la aplicación.
     *
     * @param c Competición que se desea eliminar.
     */
    public void eliminarCompeticion(Competicion c) {
        executor.execute(() -> {
            try {
                dao.eliminarCompeticion(c);
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}