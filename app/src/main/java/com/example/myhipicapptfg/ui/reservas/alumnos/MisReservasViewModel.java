package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.repositorios.ReservaClaseRepository;
import com.example.myhipicapptfg.model.ClaseModel;

import java.util.List;

/**
 * ViewModel encargado de gestionar la lógica de negocio relacionada
 * con la visualización y gestión de las reservas de clases del alumno.
 *
 * Responsabilidades:
 * - Obtener las clases en las que el alumno está inscrito
 * - Cancelar reservas de clases
 * - Exponer el estado de las operaciones del repositorio
 *
 */
public class MisReservasViewModel extends AndroidViewModel {

    /**
     * Repositorio que centraliza el acceso a datos de reservas.
     */
    private final ReservaClaseRepository repository;

    /**
     * Constructor del ViewModel.
     * Inicializa el repositorio de reservas.
     */
    public MisReservasViewModel(@NonNull Application application) {
        super(application);
        repository = new ReservaClaseRepository(application);
    }

    /**
     * Obtiene la lista de clases en las que un alumno está inscrito.
     *
     * @param idAlumno identificador del alumno
     * @return LiveData con la lista de clases reservadas
     */
    public LiveData<List<ClaseModel>> getClasesReservadas(int idAlumno) {
        return repository.obtenerClasesReservadas(idAlumno);
    }

    /**
     * Devuelve el estado de la última operación realizada
     * (por ejemplo: éxito o error al cancelar reserva).
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }

    /**
     * Cancela una reserva existente de una clase para un alumno.
     *
     * @param idAlumno identificador del alumno
     * @param idClase identificador de la clase a cancelar
     */
    public void cancelarReserva(int idAlumno, int idClase) {
        repository.cancelarReserva(idAlumno, idClase);
    }


    /**
     * Reinicia el estado de las operaciones del repositorio.
     */
    public void resetearEstado() {
        repository.resetearEstado();
    }
}