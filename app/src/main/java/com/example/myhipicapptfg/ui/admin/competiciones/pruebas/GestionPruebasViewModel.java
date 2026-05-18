package com.example.myhipicapptfg.ui.admin.competiciones.pruebas;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.ParticipacionRepository;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;
import com.example.myhipicapptfg.datos.repository.PruebaRepository;
import com.example.myhipicapptfg.model.ConteoParticipantes;

import java.util.List;

public class GestionPruebasViewModel extends AndroidViewModel {

    private final PruebaRepository repository;
    private final JuezRepository juezRepository;

    private final ParticipacionRepository participacionRepo;

    public GestionPruebasViewModel(@NonNull Application application) {
        super(application);
        repository = new PruebaRepository(application);
        juezRepository = new JuezRepository(application);
        participacionRepo  = new ParticipacionRepository(application);
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

    public LiveData<List<ConteoParticipantes>> getConteosParticipantes() {
        return participacionRepo.contarParticipantesPorTodasLasPruebas();
    }
}