package com.example.myhipicapptfg.ui.gps.seguir;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.example.myhipicapptfg.datos.repositorios.RutaPersonalRepository;

import java.util.List;


/**
 * ViewModel encargado de gestionar la lista de rutas
 * personales almacenadas en la aplicación.
 *
 * Permite:
 * - Obtener las rutas asociadas a un propietario.
 * - Eliminar rutas existentes.
 */
public class ListaRutasViewModel extends AndroidViewModel {

    /**
     * Repositorio encargado de acceder a la base de datos
     * y realizar las operaciones sobre las rutas personales.
     */
    private final RutaPersonalRepository repository;

    /**
     * Constructor del ViewModel
     */
    public ListaRutasViewModel(@NonNull Application application) {
        super(application);
        repository = new RutaPersonalRepository(application);
    }

    /**
     * Obtiene todas las rutas pertenecientes a un propietario.
     *
     * El resultado se devuelve mediante LiveData para que
     * la interfaz pueda observar los cambios automáticamente.
     *
     * @param idPropietario identificador del propietario.
     * @return lista observable de rutas personales.
     */
    public LiveData<List<RutaPersonal>> obtenerRutas(int idPropietario) {
        return repository.obtenerPorPropietario(idPropietario);
    }

    /**
     * Elimina una ruta de la base de datos.
     */
    public void eliminar(RutaPersonal ruta) {
        repository.eliminar(ruta);
    }
}