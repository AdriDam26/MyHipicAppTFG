package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.model.ConteoParticipantes;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Participacion.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y el DAO,
 * centralizando las validaciones de negocio, las operaciones de
 * escritura en segundo plano y el acceso a los datos de las
 * inscripciones de alumnos y equinos en pruebas.
 */
public class ParticipacionRepository {

    /**
     * DAO utilizado para acceder a los datos de participación.
     */
    private final ParticipacionDao dao;

    /**
     * Executor utilizado para ejecutar operaciones de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executor;

    /**
     * LiveData que almacena el estado de la última operación realizada.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_ALUMNO_NO_EXISTE
     * - ERROR_EQUINO_NO_EXISTE
     * - ERROR_PRUEBA_NO_EXISTE
     * - ERROR_YA_INSCRITO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa el DAO y el ejecutor de tareas.
     *
     * @param application Contexto de la aplicación.
     */
    public ParticipacionRepository(Application application) {
        dao = AppDatabase.getInstance(application).participacionDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return Estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta una nueva participación en la base de datos.
     *
     * Antes de realizar la inserción se comprueban varias reglas
     * de negocio para garantizar la integridad de los datos:
     * - Que el alumno exista.
     * - Que el equino exista.
     * - Que la prueba exista.
     * - Que no exista una inscripción duplicada para la misma
     *   combinación alumno-equino-prueba.
     *
     * @param p Participación a insertar.
     */
    public void insertar(Participacion p) {

        estadoOperacion.postValue(null);

        executor.execute(() -> {

            // Validar Alumno
            if (!dao.existeAlumno(p.idAlumno)) {
                estadoOperacion.postValue("ERROR_ALUMNO_NO_EXISTE");
                return;
            }

            // Validar Equino
            if (!dao.existeEquino(p.idEquino)) {
                estadoOperacion.postValue("ERROR_EQUINO_NO_EXISTE");
                return;
            }

            // Validar Prueba
            if (!dao.existePrueba(p.idPrueba)) {
                estadoOperacion.postValue("ERROR_PRUEBA_NO_EXISTE");
                return;
            }

            // Evitar duplicados
            if (dao.existeParticipacion(p.idAlumno, p.idEquino, p.idPrueba)) {
                estadoOperacion.postValue("ERROR_YA_INSCRITO");
                return;
            }

            try {
                dao.insertarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza una participación existente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param p Participación con los datos modificados.
     */
    public void actualizar(Participacion p) {
        estadoOperacion.postValue(null);
        executor.execute(() -> {
            try {
                dao.actualizarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }



    /**
     * Elimina una participación de la base de datos.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param p Participación a eliminar.
     */
    public void eliminar(Participacion p) {

        estadoOperacion.postValue(null);
        executor.execute(() -> {
            try {
                dao.eliminarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Busca una participación a partir de su identificador.
     *
     * @param id Identificador de la participación.
     * @return Participación encontrada.
     */
    public LiveData<Participacion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    /**
     * Obtiene todas las participaciones asociadas
     * a una prueba concreta.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista observable de participaciones.
     */
    public LiveData<List<Participacion>> obtenerPorPrueba(int idPrueba) {
        return dao.obtenerPorPrueba(idPrueba);
    }

    /**
     * Obtiene el siguiente número de orden disponible
     * para una nueva participación dentro de una prueba.
     *
     * Este valor se utiliza para mantener la secuencia
     * de salida de los participantes.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Siguiente orden disponible.
     */
    public LiveData<Integer> obtenerSiguienteOrden(int idPrueba) {
        return dao.obtenerSiguienteOrden(idPrueba);
    }


    /**
     * Obtiene el número de participantes inscritos en cada prueba.
     *
     * La información se devuelve mediante el modelo
     * ConteoParticipantes, que agrupa los resultados
     * por prueba.
     *
     * @return Lista observable con los conteos de participantes.
     */
    public LiveData<List<ConteoParticipantes>> contarParticipantesPorTodasLasPruebas() {
        return dao.contarParticipantesPorTodasLasPruebas();
    }







}