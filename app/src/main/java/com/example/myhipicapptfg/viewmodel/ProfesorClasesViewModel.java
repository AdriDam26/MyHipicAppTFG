package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.ClaseRepository;
import com.example.myhipicapptfg.repository.ReservaClaseRepository;

import java.util.List;

public class ProfesorClasesViewModel extends AndroidViewModel {

    // Centralizamos la lógica en los repositorios
    private final ClaseRepository claseRepositorio;
    private final ReservaClaseRepository reservaRepositorio;

    public ProfesorClasesViewModel(@NonNull Application application) {
        super(application);
        // Inicializamos ambos repositorios
        this.claseRepositorio = new ClaseRepository(application);
        this.reservaRepositorio = new ReservaClaseRepository(application);
    }

    /**
     * Devuelve las clases asignadas a un profesor desde el ClaseRepository.
     */
    public LiveData<List<Clase>> getClasesDelProfesor(int idProfesor) {
        return claseRepositorio.getClasesPorProfesor(idProfesor);
    }

    /**
     * Devuelve los alumnos inscritos en una clase desde el ReservaClaseRepository.
     * Esto utiliza la Query con INNER JOIN que acabamos de configurar.
     */
    public LiveData<List<Usuario>> getAlumnosDeClase(int idClase) {
        return reservaRepositorio.obtenerAlumnosDeClase(idClase);
    }
}