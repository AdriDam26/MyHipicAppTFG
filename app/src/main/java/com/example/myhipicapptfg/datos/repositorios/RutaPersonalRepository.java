package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.RutaPersonalDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar el acceso a los datos de la entidad RutaPersonal.
 *
 * Esta clase actúa como intermediaria entre los ViewModel y la capa de persistencia (Room),
 * centralizando la lógica de negocio relacionada con las rutas GPS de la aplicación.
 *
 * Responsabilidades principales:
 * - Acceder a los datos mediante RutaPersonalDao y UsuarioDao.
 * - Ejecutar operaciones de base de datos en segundo plano.
 * - Validar datos antes de insertar o actualizar registros.
 * - Gestionar transacciones complejas (ruta + coordenadas).
 * - Notificar el resultado de las operaciones mediante LiveData.
 *
 */
public class RutaPersonalRepository {

    /**
     * Instancia de la base de datos.
     * Necesaria para ejecutar transacciones (runInTransaction).
     */
    private final AppDatabase db;

    /**
     * DAO encargado de las operaciones sobre la entidad RutaPersonal.
     */
    private final RutaPersonalDao dao;

    /**
     * DAO de usuarios utilizado para validar el propietario de la ruta.
     */
    private final UsuarioDao usuarioDao;

    /**
     * ExecutorService utilizado para ejecutar operaciones
     * en segundo plano y evitar bloqueos en la interfaz de usuario.
     */
    private final ExecutorService executorService;

    /**
     * LiveData que informa del estado de las operaciones.
     *
     * Posibles valores:
     * - EXITO
     * - ERROR_PROPIETARIO_NO_VALIDO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    /**
     * LiveData que almacena el ID de la ruta recién insertada.
     */
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();

    /**
     * Constructor del repositorio.
     *
     * Inicializa la base de datos, los DAOs necesarios
     * y el ExecutorService compartido del sistema.
     *
     * @param application Contexto de la aplicación.
     */
    public RutaPersonalRepository(@NonNull Application application) {
        db = AppDatabase.getInstance(application);
        dao = db.rutaPersonalDao();
        usuarioDao = db.usuarioDao();               // ✅ validación en el DAO correcto
        executorService = AppDatabase.getDatabaseExecutor();
    }


    /**
     * Guarda una ruta completa junto con sus coordenadas GPS.
     *
     * Este método realiza:
     *
     * 1. Validación del propietario.
     * 2. Inserción de la ruta.
     * 3. Inserción de las coordenadas asociadas.
     * 4. Todo dentro de una transacción para garantizar integridad.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param ruta Datos de la ruta.
     * @param puntos Lista de coordenadas GPS asociadas.
     */
    public void guardarRutaCompleta(RutaPersonal ruta, List<CoordenadaRuta> puntos) {
        executorService.execute(() -> {


            Usuario propietario = usuarioDao.buscarPorIdSync(ruta.idPropietario);
            if (propietario == null || !Usuario.TIPO_PROPIETARIO.equals(propietario.tipo)) {
                estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                return;
            }

            try {

                db.runInTransaction(() -> {

                    long id = dao.insertarRuta(ruta);

                    if (puntos != null && !puntos.isEmpty()) {

                        for (CoordenadaRuta p : puntos) {
                            p.idRutaPersonal = (int) id;
                        }

                        dao.insertarCoordenadas(puntos);
                    }

                    idInsertado.postValue(id);
                });


                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza una ruta existente.
     *
     * La operación se ejecuta en segundo plano.
     *
     * @param ruta Ruta con los datos actualizados.
     */
    public void actualizar(RutaPersonal ruta) {
        executorService.execute(() -> {
            try {
                dao.actualizarRuta(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina una ruta almacenada.
     *
     * Gracias a la relación de clave foránea con
     * eliminación en cascada, las coordenadas asociadas
     * también se eliminan automáticamente.
     *
     * @param ruta Ruta que se desea eliminar.
     */
    public void eliminar(RutaPersonal ruta) {
        executorService.execute(() -> {
            try {
                dao.eliminarRuta(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    /**
     * Recupera todas las rutas pertenecientes
     * a un propietario determinado.
     *
     * El resultado se devuelve mediante LiveData,
     * permitiendo actualizar automáticamente la interfaz
     * cuando cambien los datos.
     *
     * @param idPropietario Identificador del propietario.
     * @return Lista observable de rutas.
     */
    public LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario) {
        return dao.obtenerPorPropietario(idPropietario);
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return Estado observable.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Devuelve el ID de la última ruta insertada.
     *
     * Este valor puede utilizarse posteriormente para
     * navegar a otra pantalla o realizar operaciones
     * relacionadas con la ruta recién creada.
     *
     * @return ID observable de la ruta insertada.
     */
    public LiveData<Long> getIdInsertado() {
        return idInsertado;
    }
}