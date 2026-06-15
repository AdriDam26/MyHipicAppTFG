package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.ClaseDao;
import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.dao.ProfesorDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.ui.admin.clases.ClaseValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;


/**
 * Repositorio encargado de gestionar todas las operaciones relacionadas
 * con la entidad Clase.
 *
 *
 * Entre sus responsabilidades principales se encuentran:
 *
 * - Consulta de clases registradas.
 * - Gestión de altas, modificaciones y eliminaciones.
 * - Obtención de profesores, usuarios y pistas.
 * - Consulta de clases asignadas a un profesor.
 * - Filtrado de recursos disponibles según horario,
 *   disciplina y nivel solicitado.
 *
 */
public class ClaseRepository {

    /**
     * DAO encargado de las operaciones sobre la entidad Clase.
     */
    private final ClaseDao claseDao;

    /**
     * DAO utilizado para recuperar información de profesores.
     */
    private final ProfesorDao profesorDao;

    /**
     * DAO utilizado para recuperar información de profesores.
     */
    private final PistaDao pistaDao;


    /**
     * DAO utilizado para obtener información de los usuarios.
     */
    private final UsuarioDao usuarioDao;


    /**
     * Executor encargado de ejecutar tareas de base de datos
     * fuera del hilo principal.
     */
    private final ExecutorService executorService;

    /**
     * LiveData utilizado para comunicar el resultado de las
     * operaciones realizadas por el repositorio.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_PISTA_NO_EXISTE
     * - ERROR_PROFESOR_NO_EXISTE
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion =
            new MutableLiveData<>();


    /**
     * Constructor del repositorio.
     *
     * Inicializa los DAO necesarios para la gestión de clases,
     * profesores, pistas y usuarios, además de obtener la
     * instancia compartida del ExecutorService.
     *
     * @param application Contexto global de la aplicación.
     */
    public ClaseRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        claseDao = db.claseDao();
        profesorDao = db.profesorDao();
        pistaDao = db.pistaDao();
        usuarioDao = db.usuarioDao();

        executorService = AppDatabase.getDatabaseExecutor();
    }

    /**
     * Devuelve el estado de la última operación realizada.
     *
     * Permite informar a la interfaz de usuario sobre el
     * resultado de las operaciones ejecutadas.
     *
     * @return Estado actual de la operación.
     */

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    /**
     * Obtiene todas las clases registradas en el sistema.
     *
     * @return Lista observable de clases.
     */
    public LiveData<List<Clase>> obtenerTodasClases() {
        return claseDao.obtenerTodasClases();
    }

    /**
     * Busca una clase a partir de su identificador.
     *
     * @param id Identificador de la clase.
     * @return Clase correspondiente al identificador indicado.
     */
    public LiveData<Clase> buscarPorId(int id) {
        return claseDao.buscarPorId(id);
    }

    /**
     * Recupera todos los profesores registrados.
     *
     * @return Lista observable de profesores.
     */
    public LiveData<List<Profesor>> obtenerTodosLosProfesores() {
        return profesorDao.obtenerTodosProfesores();
    }

    /**
     * Obtiene todas las pistas registradas en el sistema.
     *
     * @return Lista observable de pistas.
     */
    public LiveData<List<Pista>> obtenerTodasLasPistas() {
        return pistaDao.obtenerTodasPistas();
    }


    /**
     * Recupera todos los usuarios registrados.
     *
     * @return Lista observable de usuarios.
     */
    public LiveData<List<Usuario>> obtenerTodosLosUsuarios() {
        return usuarioDao.obtenerTodosUsuarios();
    }

    /**
     * Obtiene las clases asignadas a un profesor concreto.
     *
     * @param idProfesor Identificador del profesor.
     * @return Lista de clases asociadas al profesor.
     */
    public LiveData<List<Clase>> getClasesPorProfesor(int idProfesor) {
        return claseDao.obtenerClasesPorProfesor(idProfesor);
    }


    /**
     * Inserta una nueva clase en la base de datos.
     *
     * Antes de realizar la inserción se verifica:
     *
     * - La existencia de la pista asignada.
     * - La existencia del profesor asignado.
     *
     * Si alguna de las validaciones falla, se cancela
     * la operación y se informa mediante estadoOperacion.
     *
     * @param clase Clase que se desea registrar.
     */
    public void insertarClase(Clase clase) {

        executorService.execute(() -> {

            if (!claseDao.existePistaSync(clase.idPista)) {
                estadoOperacion.postValue("ERROR_PISTA_NO_EXISTE");
                return;
            }

            if (!claseDao.existeProfesorSync(clase.idProfesor)) {
                estadoOperacion.postValue("ERROR_PROFESOR_NO_EXISTE");
                return;
            }

            try {

                claseDao.insertarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Actualiza los datos de una clase existente.
     *
     * La operación se ejecuta en segundo plano para
     * evitar bloqueos de la interfaz.
     *
     * @param clase Clase con la información actualizada.
     */
    public void actualizarClase(Clase clase) {

        executorService.execute(() -> {

            try {

                claseDao.actualizarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina una clase de la base de datos.
     *
     * La operación se realiza de forma asíncrona.
     *
     * @param clase Clase que se desea eliminar.
     */
    public void eliminarClase(Clase clase) {

        executorService.execute(() -> {

            try {

                claseDao.eliminarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Filtra los profesores y pistas disponibles para impartir
     * una clase en un intervalo horario determinado.
     *
     * El proceso evalúa:
     *
     * 1. Que el profesor pueda impartir la disciplina y nivel
     *    solicitados.
     * 2. Que el profesor no tenga otra clase asignada en el
     *    mismo intervalo horario.
     * 3. Que la pista no esté ocupada por otra clase durante
     *    dicho periodo.
     *
     * Este método se utiliza principalmente durante la creación
     * o edición de clases para mostrar únicamente los recursos
     * realmente disponibles.
     *
     * @param inicio Fecha y hora de inicio de la clase.
     * @param fin Fecha y hora de finalización de la clase.
     * @param disciplina Disciplina ecuestre solicitada.
     * @param nivel Nivel de formación requerido.
     * @param usuarios Lista de usuarios candidatos.
     * @param profesores Lista de profesores disponibles.
     * @param pistas Lista de pistas registradas.
     * @param clases Lista de clases existentes.
     * @param claseEditando Identificador de la clase que se está
     *                      editando. Se ignora durante las
     *                      comprobaciones de disponibilidad.
     * @param profesoresDisponibles Resultado con los profesores válidos.
     * @param pistasDisponibles Resultado con las pistas disponibles.
     */
    public void filtrarDisponibilidad(
            long inicio,
            long fin,
            String disciplina,
            String nivel,
            List<Usuario> usuarios,
            List<Profesor> profesores,
            List<Pista> pistas,
            List<Clase> clases,
            int claseEditando,
            MutableLiveData<List<Usuario>> profesoresDisponibles,
            MutableLiveData<List<Pista>> pistasDisponibles
    ) {

        executorService.execute(() -> {

            List<Usuario> profesoresOk = new ArrayList<>();

            for (Usuario usuario : usuarios) {

                Profesor detalle = buscarProfesor(
                        usuario.idUsuario,
                        profesores
                );

                if (detalle == null)
                    continue;

                boolean puedeDar =
                        ClaseValidator.profesorPuedeDarClase(
                                detalle,
                                disciplina,
                                nivel
                        );

                boolean disponible =
                        ClaseValidator.profesorDisponible(
                                usuario.idUsuario,
                                inicio,
                                fin,
                                clases,
                                claseEditando
                        );

                if (puedeDar && disponible) {
                    profesoresOk.add(usuario);
                }
            }

            List<Pista> pistasOk = new ArrayList<>();

            for (Pista pista : pistas) {

                boolean disponible =
                        ClaseValidator.pistaDisponible(
                                pista.idPista,
                                inicio,
                                fin,
                                clases,
                                claseEditando
                        );

                if (disponible) {
                    pistasOk.add(pista);
                }
            }

            profesoresDisponibles.postValue(profesoresOk);
            pistasDisponibles.postValue(pistasOk);
        });
    }

    /**
     * Busca un profesor dentro de una colección a partir
     * de su identificador.
     *
     * Este método auxiliar se utiliza durante el proceso
     * de filtrado de disponibilidad para localizar la
     * información detallada de un profesor asociado a un usuario.
     *
     * @param idProfesor Identificador del profesor.
     * @param profesores Lista de profesores donde realizar la búsqueda.
     * @return Profesor encontrado o null si no existe.
     */
    private Profesor buscarProfesor(
            int idProfesor,
            List<Profesor> profesores
    ) {

        for (Profesor profesor : profesores) {

            if (profesor.idProfesor == idProfesor) {
                return profesor;
            }
        }

        return null;
    }
}