package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.EquinoDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Equino;

import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * Repositorio encargado de gestionar el acceso a los datos de la entidad Equino.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y la capa de
 * persistencia (Room), centralizando la lógica de negocio relacionada
 * con los caballos de la aplicación.
 *
 * Responsabilidades principales:
 * - Acceder a los datos mediante EquinoDao.
 * - Ejecutar operaciones de base de datos en segundo plano.
 * - Realizar validaciones antes de insertar o actualizar registros.
 * - Notificar el resultado de las operaciones mediante LiveData.
 *
 */
public class EquinoRepository {


    /**
     * DAO encargado de las operaciones sobre la tabla Equino.
     */
    private final EquinoDao equinoDao;

    /**
     * Servicio de ejecución utilizado para realizar operaciones
     * en segundo plano y evitar bloqueos en la interfaz.
     */
    private final ExecutorService executorService;

    /**
     * LiveData utilizado para informar del resultado de las operaciones
     * realizadas sobre la base de datos.
     *
     * Posibles valores:
     * - EXITO
     * - ERROR_MICROCHIP_DUPLICADO
     * - ERROR_CUADRA_OCUPADA
     * - ERROR_PROPIETARIO_NO_VALIDO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Obtiene la instancia de la base de datos y recupera
     * el DAO correspondiente a la entidad Equino.
     *
     * También inicializa el ExecutorService compartido
     * para la ejecución de tareas en segundo plano.
     *
     */
    public EquinoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        equinoDao = db.equinoDao();
        executorService = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve un LiveData con el estado de la última operación realizada.
     *
     * Permite que la interfaz observe el resultado de inserciones,
     * modificaciones o eliminaciones y actúe en consecuencia.
     *
     * @return Estado observable de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Inserta un nuevo equino en la base de datos.
     *
     * Antes de realizar la inserción se ejecutan varias validaciones:
     *
     * 1. Comprobar que el microchip no exista previamente.
     * 2. Verificar que la cuadra no esté ocupada.
     * 3. Validar que el propietario exista y tenga rol de propietario.
     *
     * La operación se ejecuta en segundo plano mediante ExecutorService.
     *
     * @param equino Equino que se desea insertar.
     */
    public void insertarEquino(Equino equino) {

        executorService.execute(() -> {

            if (equinoDao.existeMicrochip(equino.numeroMicrochip)) {
                estadoOperacion.postValue("ERROR_MICROCHIP_DUPLICADO");
                return;
            }

            if (equinoDao.esCuadraOcupada(equino.numeroCuadra)) {
                estadoOperacion.postValue("ERROR_CUADRA_OCUPADA");
                return;
            }
            if (equino.idUsuario != null) {
                if (!equinoDao.esPropietarioValido(equino.idUsuario)) {
                    estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                    return;
                }
            }

            try {
                equinoDao.insertarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    /**
     * Actualiza los datos de un equino existente.
     *
     * Antes de realizar la actualización se comprueba:
     *
     * - Que la cuadra no esté ocupada por otro caballo.
     * - Que el propietario asignado sea válido.
     *
     * La operación se ejecuta de forma asíncrona.
     *
     * @param equino Equino con los datos actualizados.
     */
    public void actualizarEquino(Equino equino) {
        executorService.execute(() -> {

            if (equinoDao.esCuadraOcupadaPorOtro(equino.numeroCuadra, equino.idEquino)) {
                estadoOperacion.postValue("ERROR_CUADRA_OCUPADA");
                return;
            }

            if (equino.idUsuario != null) {
                if (!equinoDao.esPropietarioValido(equino.idUsuario)) {
                    estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                    return;
                }
            }

            try {
                equinoDao.actualizarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina un equino de la base de datos.
     *
     * La operación se ejecuta en segundo plano para evitar
     * bloqueos en el hilo principal.
     *
     * @param equino Equino que se desea eliminar.
     */
    public void eliminarEquino(Equino equino) {
        executorService.execute(() -> {
            try {
                equinoDao.eliminarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Recupera todos los equinos almacenados.
     *
     * El resultado se devuelve mediante LiveData para que
     * cualquier cambio en la base de datos se refleje
     * automáticamente en la interfaz.
     *
     * @return Lista observable de equinos.
     */
    public LiveData<List<Equino>> obtenerTodosEquinos() {
        return equinoDao.obtenerTodosEquinos();
    }

    /**
     * Recupera un equino por su identificador.
     *
     * @param id Identificador del equino.
     * @return Equino observable.
     */
    public LiveData<Equino> buscarPorId(int id) {
        return equinoDao.buscarEquinoPorId(id);
    }

    /**
     * Recupera todos los caballos entrenados en doma.
     *
     * @return Lista observable de equinos de doma.
     */
    public LiveData<List<Equino>> obtenerEquinosDoma() {
        return equinoDao.obtenerEquinosDoma();
    }


    /**
     * Recupera un equino utilizando su número de microchip.
     *
     * Este método es utilizado por el módulo de realidad aumentada
     * tras la lectura de un código QR asociado al caballo.
     *
     * @param microchip Número de microchip.
     * @return Equino encontrado o null.
     */
    public Equino buscarPorMicrochipSync(String microchip) {
        return equinoDao.buscarPorMicrochipSync(microchip);
    }

    /**
     * Recupera el nombre completo del propietario asociado
     * a un caballo determinado.
     *
     * @param idUsuario Identificador del propietario.
     * @return Nombre completo del propietario.
     */
    public String obtenerNombrePropietarioSync(int idUsuario) {
        return equinoDao.obtenerNombrePropietarioSync(idUsuario);
    }


}