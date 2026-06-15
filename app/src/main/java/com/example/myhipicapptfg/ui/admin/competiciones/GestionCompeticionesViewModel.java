package com.example.myhipicapptfg.ui.admin.competiciones;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Competicion;
import com.example.myhipicapptfg.datos.repositorios.CompeticionRepository;

import java.util.List;


/**
 * ViewModel encargado de la gestión de competiciones.
 */
public class GestionCompeticionesViewModel extends AndroidViewModel {

    // Repositorio que maneja el acceso a datos.
    private final CompeticionRepository repository;


    /**
     * Constructor del ViewModel.
     * Se inicializa el repository con el contexto de la aplicación.
     */
    public GestionCompeticionesViewModel(@NonNull Application application) {
        super(application);
        repository = new CompeticionRepository(application);
    }

    /**
     * Devuelve el estado de las operaciones (éxito, error, mensajes, etc.)
     * útil para mostrar feedback en la UI.
     */
    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }

    /**
     * Obtiene la lista completa de competiciones.
     * LiveData permite que la UI se actualice automáticamente
     * cuando los datos cambian.
     */
    public LiveData<List<Competicion>> getCompeticiones() {
        return repository.obtenerTodas();
    }

    /**
     * Busca una competición por su ID.
     */
    public LiveData<Competicion> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    /**
     * Inserta una nueva competición en la base de datos.
     */
    public void insertar(Competicion c) {
        repository.insertarCompeticion(c);
    }

    /**
     * Actualiza una competición existente.
     */
    public void actualizar(Competicion c) {
        repository.actualizarCompeticion(c);
    }

    /**
     * Elimina una competición de la base de datos.
     */
    public void eliminar(Competicion c) {
        repository.eliminarCompeticion(c);
    }
}