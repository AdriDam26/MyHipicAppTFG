package com.example.myhipicapptfg.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Participacion;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.EquinoRepository;
import com.example.myhipicapptfg.repository.ParticipacionRepository;
import com.example.myhipicapptfg.repository.UsuarioRepository;

import java.util.List;

public class GestionParticipacionesViewModel extends AndroidViewModel {

    private final ParticipacionRepository participacionRepo;
    private final UsuarioRepository       usuarioRepo;
    private final EquinoRepository        equinoRepo;

    public GestionParticipacionesViewModel(@NonNull Application application) {
        super(application);
        participacionRepo = new ParticipacionRepository(application);
        usuarioRepo       = new UsuarioRepository(application);
        equinoRepo        = new EquinoRepository(application);
    }

    // Alumnos que practican doma
    public LiveData<List<Usuario>> obtenerAlumnosDomaConNombre() {
        return usuarioRepo.obtenerAlumnosDoma();
    }

    // Equinos aptos para doma
    public LiveData<List<Equino>> obtenerEquinosDoma() {
        return equinoRepo.obtenerEquinosDoma();
    }

    // CRUD participaciones
    public void insertar(Participacion p)  { participacionRepo.insertar(p); }
    public void actualizar(Participacion p) { participacionRepo.actualizar(p); }
    public void eliminar(Participacion p)  { participacionRepo.eliminar(p); }

    public LiveData<Participacion> buscarPorId(int id) {
        return participacionRepo.buscarPorId(id);
    }

    public LiveData<List<Participacion>> getParticipacionesPorPrueba(int idPrueba) {
        return participacionRepo.obtenerPorPrueba(idPrueba);
    }

    public LiveData<Integer> obtenerSiguienteOrden(int idPrueba) {
        return participacionRepo.obtenerSiguienteOrden(idPrueba);
    }

    public LiveData<String> getEstadoOperacion() {
        return participacionRepo.getEstadoOperacion();
    }
}