package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.app.Application;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.repositorios.ReservaClaseRepository;
import com.example.myhipicapptfg.model.ClaseModel;

import java.util.List;
import java.util.Objects;

/**
 * ViewModel encargado de gestionar la lógica de negocio relacionada
 * con la reserva de clases por parte de los alumnos.
 *
 * Su función principal es:
 * - Mantener el estado del alumno seleccionado
 * - Gestionar la fecha de filtrado
 * - Obtener clases disponibles
 * - Realizar reservas
 */
public class ReservaClaseViewModel extends AndroidViewModel {

    /**
     * Repositorio que gestiona el acceso a datos de reservas de clases.
     */
    private final ReservaClaseRepository repository;

    /**
     * Fecha seleccionada para filtrar las clases disponibles.
     * Inicializada con la fecha actual del sistema.
     */
    private final MutableLiveData<Long> fechaSeleccionada =
            new MutableLiveData<>(System.currentTimeMillis());

    /**
     * ID del alumno actualmente activo.
     * Se inicializa mediante el método init().
     */
    private int idAlumno;


    /**
     * Constructor del ViewModel.
     * Inicializa el repositorio.
     */
    public ReservaClaseViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservaClaseRepository(application);
    }

    /**
     * Inicializa el ViewModel con el ID del alumno.
     * Debe llamarse antes de realizar cualquier consulta.
     */
    public void init(int idAlumno) {
        this.idAlumno = idAlumno;
    }

    /**
     * Establece la fecha para filtrar las clases disponibles.
     * Evita actualizaciones innecesarias si la fecha no cambia.
     */
    public void setFechaFiltro(long nuevaFecha) {
        if (Objects.equals(fechaSeleccionada.getValue(), nuevaFecha)) return;
        fechaSeleccionada.setValue(nuevaFecha);
    }


    /**
     * Obtiene la lista de clases disponibles para el alumno
     * en la fecha seleccionada.
     *
     * La consulta se actualiza automáticamente cuando cambia la fecha.
     */
    public LiveData<List<ClaseModel>> getClases() {
        return Transformations.switchMap(fechaSeleccionada, fecha ->
                repository.obtenerClasesPorAlumnoYFecha(idAlumno, fecha)
        );
    }

    /**
     * Devuelve el estado de la última operación realizada en el repositorio
     * (éxito, error, en progreso, etc.).
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }

    /**
     * Reinicia el estado de las operaciones del repositorio.
     */
    public void resetearEstado() {
        repository.resetearEstado();
    }

    /**
     * Realiza una reserva de clase para el alumno actual.
     *
     * @param idClase identificador de la clase a reservar
     */
    public void reservarClase(int idClase) {
        repository.insertarReserva(
                new ReservaClase(idAlumno, idClase, System.currentTimeMillis())
        );
    }
}