package com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.EquinoRepository;
import com.example.myhipicapptfg.datos.repositorios.ParticipacionRepository;
import com.example.myhipicapptfg.datos.repositorios.UsuarioRepository;

import java.util.List;


/**
 * ViewModel encargado de la gestión de Participaciones en pruebas.
 *
 * Responsabilidades:
 * - Obtener alumnos (usuarios) que participan en doma
 * - Obtener equinos disponibles para doma
 * - Gestionar CRUD de participaciones
 * - Exponer LiveData a la UI (Activity/Fragment)
 *
 */
public class GestionParticipacionesViewModel extends AndroidViewModel {

    // Repositorios
    private final ParticipacionRepository participacionRepo;
    private final UsuarioRepository       usuarioRepo;
    private final EquinoRepository        equinoRepo;

    // Constructor
    public GestionParticipacionesViewModel(@NonNull Application application) {
        super(application);
        participacionRepo = new ParticipacionRepository(application);
        usuarioRepo       = new UsuarioRepository(application);
        equinoRepo        = new EquinoRepository(application);

    }

    /**
     * Obtiene alumnos que practican doma.
     * Se usa para asignar participantes a una prueba.
     */
    public LiveData<List<Usuario>> obtenerAlumnosDomaConNombre() {
        return usuarioRepo.obtenerAlumnosDoma();
    }

    /**
     * Obtiene equinos aptos para doma.
     * Se usan como montura en participaciones.
     */
    public LiveData<List<Equino>> obtenerEquinosDoma() {
        return equinoRepo.obtenerEquinosDoma();
    }

    // CRUD participaciones
    public void insertar(Participacion p)  {
        participacionRepo.insertar(p);
    }

    public void actualizar(Participacion p) {
        participacionRepo.actualizar(p);
    }
    public void eliminar(Participacion p)  {
        participacionRepo.eliminar(p);
    }

    /**
     * Busca una participación por su ID
     */
    public LiveData<Participacion> buscarPorId(int id) {
        return participacionRepo.buscarPorId(id);
    }

    /**
     * Obtiene todas las participaciones de una prueba concreta
     */
    public LiveData<List<Participacion>> getParticipacionesPorPrueba(int idPrueba) {
        return participacionRepo.obtenerPorPrueba(idPrueba);
    }

    /**
     * Obtiene el siguiente orden disponible dentro de una prueba
     * (útil para ordenar participantes automáticamente)
     */
    public LiveData<Integer> obtenerSiguienteOrden(int idPrueba) {
        return participacionRepo.obtenerSiguienteOrden(idPrueba);
    }


    /**
     * Estado de operaciones (éxito, error, etc.)
     */
    public LiveData<String> getEstadoOperacion() {
        return participacionRepo.getEstadoOperacion();
    }


}