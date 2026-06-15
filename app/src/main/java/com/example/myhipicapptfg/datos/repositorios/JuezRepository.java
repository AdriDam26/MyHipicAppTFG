package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.JuezDao;
import com.example.myhipicapptfg.datos.local.dao.MovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.model.ParticipacionDetalle;
import com.example.myhipicapptfg.model.PruebaConCompeticion;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Juez y las funcionalidades asociadas al proceso
 * de evaluación de pruebas de doma.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y los DAO,
 * centralizando la lógica de negocio, las validaciones de integridad
 * y el acceso a la información necesaria para que un juez pueda
 * consultar pruebas, evaluar participantes y registrar puntuaciones.
 *
 * Además de las operaciones CRUD sobre jueces, el repositorio integra
 * funcionalidades de varias entidades relacionadas como Prueba,
 * Participacion, Movimiento y NotaMovimiento.
 */
public class JuezRepository {

    /**
     * DAO para operaciones sobre la entidad Juez.
     */
    private final JuezDao juezDao;

    /**
     * DAO utilizado para validar la existencia y el tipo
     * del usuario asociado al juez.
     */
    private final UsuarioDao usuarioDao;


    /**
     * DAO para consultas relacionadas con pruebas.
     */
    private final PruebaDao pruebaDao;

    /**
     * DAO para consultas y actualizaciones de participaciones.
     */
    private final ParticipacionDao participacionDao;

    /**
     * DAO para la gestión de movimientos de las pruebas.
     */
    private final MovimientoDao movimientoDao;

    /**
     * DAO para la gestión de notas asignadas a movimientos.
     */
    private final NotaMovimientoDao notaMovimientoDao;

    /**
     * Executor encargado de ejecutar operaciones de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executorService;

    /**
     * LiveData utilizado para comunicar el resultado de las
     * operaciones realizadas.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_USUARIO_NO_EXISTE
     * - ERROR_TIPO_USUARIO_INVALIDO
     * - ERROR_LICENCIA_DUPLICADA
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa todos los DAO necesarios para la gestión de jueces,
     * pruebas y evaluaciones.
     *
     * @param application Contexto de la aplicación.
     */
    public JuezRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        juezDao = db.juezDao();
        usuarioDao = db.usuarioDao();

        pruebaDao         = db.pruebaDao();
        participacionDao  = db.participacionDao();

        movimientoDao      = db.movimientoDao();
        notaMovimientoDao  = db.notaMovimientoDao();



        executorService = AppDatabase.getDatabaseExecutor();
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
     * Inserta un nuevo juez en la base de datos.
     *
     * Antes de realizar la inserción se verifican:
     * - La existencia del usuario asociado.
     * - Que el usuario tenga tipo JUEZ.
     * - Que el número de licencia no esté duplicado.
     *
     * @param juez Juez a insertar.
     */
    public void insertarJuez(Juez juez) {

        executorService.execute(() -> {

            // Verificar que existe el Usuario base
            Usuario usuarioBase = usuarioDao.buscarPorIdSync(juez.idJuez);

            if (usuarioBase == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }

            // Verificar que el tipo sea JUEZ
            if (!Usuario.TIPO_JUEZ.equals(usuarioBase.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            // 3Verificar que no exista ya la licencia
            if (juezDao.buscarPorLicenciaSync(juez.numeroLicencia) != null) {
                estadoOperacion.postValue("ERROR_LICENCIA_DUPLICADA");
                return;
            }

            try {
                juezDao.insertarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza los datos de un juez existente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param juez Juez con la información actualizada.
     */
    public void actualizarJuez(Juez juez) {
        executorService.execute(() -> {
            try {
                juezDao.actualizarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina un juez de la base de datos.
     *
     * La operación se ejecuta de forma asíncrona
     * para no bloquear la interfaz.
     *
     * @param juez Juez que se desea eliminar.
     */
    public void eliminarJuez(Juez juez) {
        executorService.execute(() -> {
            try {
                juezDao.eliminarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Recupera todos los jueces registrados en el sistema.
     *
     * @return Lista observable de jueces.
     */
    public LiveData<List<Juez>> obtenerTodosJueces() {
        return juezDao.obtenerTodosJueces();
    }

    /**
     * Busca un juez por su identificador.
     *
     * @param id Identificador del juez.
     * @return Juez correspondiente al identificador indicado.
     */
    public LiveData<Juez> buscarPorId(int id) {
        return juezDao.buscarPorId(id);
    }


    /**
     * Obtiene el número total de jueces registrados.
     *
     * @return Cantidad total de jueces.
     */
    public LiveData<Integer> contarJueces() {
        return juezDao.contarJueces();
    }

    /**
     * Obtiene los jueces activos junto con la información
     * básica del usuario asociado.
     *
     * Esta consulta suele utilizarse para poblar
     * listas de selección de jueces.
     *
     * @return Lista de jueces activos.
     */
    public LiveData<List<Usuario>> obtenerJuecesActivosConNombre() {
        return juezDao.obtenerJuecesActivosConNombre();
    }



    /**
     * Obtiene todas las pruebas asignadas a un juez.
     *
     * La información incluye tanto los datos de la prueba
     * como los de la competición asociada.
     *
     * @param idJuez Identificador del juez.
     * @return Lista de pruebas asignadas.
     */
    public LiveData<List<PruebaConCompeticion>> getPruebasByJuez(int idJuez) {
        return pruebaDao.getPruebasByJuez(idJuez);
    }

    /**
     * Recupera los participantes inscritos en una prueba.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista de participantes con información ampliada.
     */
    public LiveData<List<ParticipacionDetalle>> getParticipantesByPrueba(int idPrueba) {
        return participacionDao.getParticipantesByPrueba(idPrueba);
    }


    /** Devuelve los movimientos de la prueba asociada a una participación */
    public LiveData<List<Movimiento>> getMovimientosByPrueba(int idPrueba) {
        return movimientoDao.getMovimientosByPrueba(idPrueba);
    }

    /** Notas ya guardadas para una participación */
    public LiveData<List<NotaMovimiento>> getNotasByParticipacion(int idParticipacion) {
        return notaMovimientoDao.getNotasByParticipacion(idParticipacion);
    }

    /**
     * Guarda todas las notas + recalcula nota final, porcentaje
     * y aplica la corrección. Todo en un solo hilo background.
     */
    public void guardarPuntuacion(int idParticipacion,
                                  List<NotaMovimiento> notas,
                                  double notaFinal,
                                  double porcentaje,
                                  double correccion,
                                  boolean eliminado) {

        executorService.execute(() -> {
            notaMovimientoDao.insertOrUpdateAll(notas);
            participacionDao.updateResultado(
                    idParticipacion,
                    notaFinal,
                    porcentaje,
                    correccion,
                    eliminado
            );
        });
    }



}