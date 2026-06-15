// com/example/myhipicapptfg/viewmodel/PruebasJuezViewModel.java
package com.example.myhipicapptfg.ui.competiciones.juez.pruebas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.model.PruebaConCompeticion;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;

import java.util.List;

/**
 * ViewModel encargado de gestionar las pruebas asignadas a un juez.
 *
 * Actúa como intermediario entre la interfaz de usuario y el repositorio,
 * proporcionando acceso a las pruebas asociadas al juez autenticado.
 */
public class PruebasJuezViewModel extends AndroidViewModel {

    // Repositorio encargado de obtener los datos de las pruebas
    private final JuezRepository repositorio;
    // Lista de pruebas asignadas al juez
    private LiveData<List<PruebaConCompeticion>> pruebas;

    /**
     * Constructor del ViewModel.
     * Inicializa el repositorio encargado de acceder a los datos.
     */
    public PruebasJuezViewModel(@NonNull Application application) {
        super(application);
        repositorio = new JuezRepository(application);
    }

    /**
     * Devuelve las pruebas asignadas a un juez.
     */
    public LiveData<List<PruebaConCompeticion>> getPruebas(int idJuez) {
        if (pruebas == null) {
            pruebas = repositorio.getPruebasByJuez(idJuez);
        }
        return pruebas;
    }
}