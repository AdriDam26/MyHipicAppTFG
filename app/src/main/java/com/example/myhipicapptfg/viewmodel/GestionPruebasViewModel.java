package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Movimiento;
import com.example.myhipicapptfg.entities.Prueba;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.JuezRepository;
import com.example.myhipicapptfg.repository.PruebaRepository;

import java.util.List;

public class GestionPruebasViewModel extends AndroidViewModel {

    private final PruebaRepository repository;
    private final JuezRepository juezRepository;
    public GestionPruebasViewModel(@NonNull Application application) {
        super(application);
        repository = new PruebaRepository(application);
        juezRepository = new JuezRepository(application);
    }

    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }

    public LiveData<List<Prueba>> getPruebasPorCompeticion(int idCompeticion) {
        return repository.obtenerPorCompeticion(idCompeticion);
    }

    public LiveData<Prueba> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    public LiveData<List<Movimiento>> getMovimientos(int idPrueba) {
        return repository.obtenerMovimientosPorPrueba(idPrueba);
    }

    public void guardarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        repository.insertarPruebaConMovimientos(prueba, movimientos);
    }

    public void actualizarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        repository.actualizarPruebaConMovimientos(prueba, movimientos);
    }

    public LiveData<List<Usuario>> getJuecesActivos() {
        return juezRepository.obtenerJuecesActivosConNombre();
    }

    public void eliminarPrueba(Prueba p) {
        repository.eliminarPrueba(p);
    }
}