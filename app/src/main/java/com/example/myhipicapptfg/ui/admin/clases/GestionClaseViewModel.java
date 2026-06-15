package com.example.myhipicapptfg.ui.admin.clases;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.ClaseRepository;

import java.util.List;

/**
 * ViewModel encargado de gestionar las operaciones relacionadas
 * con las clases desde el módulo de administración.
 *
 * Sus responsabilidades principales son:
 * - Obtener información de clases, profesores, usuarios y pistas.
 * - Filtrar recursos disponibles para una clase según fecha y horario.
 * - Gestionar operaciones CRUD sobre las clases.
 * - Exponer datos observables a la interfaz mediante LiveData.
 *
 */
public class GestionClaseViewModel extends AndroidViewModel {

    /**
     * Repositorio encargado de realizar las operaciones
     * de acceso a datos relacionadas con las clases.
     */
    private final ClaseRepository repository;

    /**
     * Lista observable de profesores disponibles para una franja horaria.
     */
    private final MutableLiveData<List<Usuario>>
            profesoresDisponibles = new MutableLiveData<>();


    /**
     * Lista observable de pistas disponibles para una franja horaria.
     */
    private final MutableLiveData<List<Pista>>
            pistasDisponibles = new MutableLiveData<>();

    /**
     * Constructor del ViewModel.
     *
     * Inicializa el repositorio que gestionará
     * todas las operaciones de acceso a datos.
     *
     * @param application Contexto global de la aplicación.
     */
    public GestionClaseViewModel(@NonNull Application application) {

        super(application);

        repository = new ClaseRepository(application);
    }

    /**
     * Devuelve la lista observable de profesores disponibles.
     *
     * Esta información se actualiza tras ejecutar
     * el filtrado de disponibilidad.
     *
     * @return LiveData con los profesores disponibles.
     */
    public LiveData<List<Usuario>> getProfesoresDisponibles() {
        return profesoresDisponibles;
    }

    /**
     * Devuelve la lista observable de pistas disponibles.
     *
     * Esta información se actualiza tras ejecutar
     * el filtrado de disponibilidad.
     *
     * @return LiveData con las pistas disponibles.
     */
    public LiveData<List<Pista>> getPistasDisponibles() {
        return pistasDisponibles;
    }

    /**
     * Filtra los profesores y pistas disponibles para una clase
     * según el horario y las características seleccionadas.
     *
     * El cálculo de disponibilidad se delega al repositorio.
     * Los resultados se publican en los LiveData
     * profesoresDisponibles y pistasDisponibles.
     *
     * @param inicio         Fecha y hora de inicio de la clase.
     * @param fin            Fecha y hora de finalización de la clase.
     * @param disciplina     Disciplina de la clase.
     * @param nivel          Nivel de la clase.
     * @param usuarios       Lista completa de usuarios.
     * @param profesores     Lista completa de profesores.
     * @param pistas         Lista completa de pistas.
     * @param clases         Lista de clases existentes.
     * @param claseEditando  Identificador de la clase que se está editando.
     *                       Se utiliza para ignorarla durante las comprobaciones
     *                       de disponibilidad.
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
            int claseEditando
    ) {

        repository.filtrarDisponibilidad(
                inicio,
                fin,
                disciplina,
                nivel,
                usuarios,
                profesores,
                pistas,
                clases,
                claseEditando,
                profesoresDisponibles,
                pistasDisponibles
        );
    }

    /**
     * Obtiene todas las clases registradas en el sistema.
     *
     * @return LiveData con la lista completa de clases.
     */
    public LiveData<List<Clase>> obtenerTodasLasClases() {
        return repository.obtenerTodasClases();
    }

    /**
     * Busca una clase a partir de su identificador.
     *
     * @param id Identificador de la clase.
     * @return LiveData con la clase encontrada.
     */
    public LiveData<Clase> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    /**
     * Obtiene todos los profesores registrados.
     *
     * @return LiveData con la lista de profesores.
     */
    public LiveData<List<Profesor>> obtenerTodosLosProfesores() {
        return repository.obtenerTodosLosProfesores();
    }

    /**
     * Obtiene todas las pistas registradas.
     *
     * @return LiveData con la lista de pistas.
     */
    public LiveData<List<Pista>> obtenerTodasLasPistas() {
        return repository.obtenerTodasLasPistas();
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return LiveData con la lista de usuarios.
     */
    public LiveData<List<Usuario>> obtenerTodosLosUsuarios() {
        return repository.obtenerTodosLosUsuarios();
    }


    /**
     * Inserta una nueva clase en la base de datos.
     *
     * @param clase Clase a registrar.
     */
    public void insertar(Clase clase) {
        repository.insertarClase(clase);
    }

    /**
     * Actualiza una clase existente.
     *
     * @param clase Clase con los datos modificados.
     */
    public void actualizar(Clase clase) {
        repository.actualizarClase(clase);
    }


    /**
     * Elimina una clase de la base de datos.
     *
     * @param clase Clase que se desea eliminar.
     */
    public void eliminar(Clase clase) {
        repository.eliminarClase(clase);
    }

    /**
     * Devuelve el estado de la última operación realizada
     * sobre las clases (insertar, actualizar o eliminar).
     *
     * Este LiveData puede utilizarse para mostrar mensajes
     * de éxito o error en la interfaz.
     *
     * @return LiveData con el estado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }
}