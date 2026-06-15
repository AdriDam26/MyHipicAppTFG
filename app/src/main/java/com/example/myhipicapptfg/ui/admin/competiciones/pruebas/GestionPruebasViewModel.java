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
import com.example.myhipicapptfg.datos.repositorios.PruebaRepository;
import com.example.myhipicapptfg.model.ConteoParticipantes;

import java.util.List;

/**
 * ViewModel encargado de la gestión de Pruebas dentro de una Competición.
 *
 * Forma parte de la arquitectura MVVM y actúa como intermediario entre:
 * - UI (Activity / Fragment)
 * - Capa de datos (Repositories)
 *
 * Responsabilidades:
 * - Gestionar pruebas
 * - Gestionar movimientos asociados
 * - Obtener jueces activos
 * - Contabilizar participantes
 * - Exponer estado de operaciones mediante LiveData
 */
public class GestionPruebasViewModel extends AndroidViewModel {

    // Repositorio principal de pruebas (CRUD + movimientos)
    private final PruebaRepository repository;

    // Repositorio de jueces
    private final JuezRepository juezRepository;

    // Repositorio de participaciones
    private final ParticipacionRepository participacionRepo;


    /**
     * Constructor del ViewModel.
     * Se inicializan los repositorios con el contexto de la aplicación.
     */
    public GestionPruebasViewModel(@NonNull Application application) {
        super(application);
        repository = new PruebaRepository(application);
        juezRepository = new JuezRepository(application);
        participacionRepo  = new ParticipacionRepository(application);
    }

    /**
     * Estado de operaciones (éxito / error / mensajes)
     */
    public LiveData<String> getEstado() {
        return repository.getEstadoOperacion();
    }

    /**
     * Obtiene todas las pruebas asociadas a una competición concreta
     */
    public LiveData<List<Prueba>> getPruebasPorCompeticion(int idCompeticion) {
        return repository.obtenerPorCompeticion(idCompeticion);
    }

    /**
     * Busca una prueba por su ID
     */
    public LiveData<Prueba> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }


    /**
     * Obtiene los movimientos asociados a una prueba
     * (ej: ejercicios o fases de la prueba)
     */
    public LiveData<List<Movimiento>> getMovimientos(int idPrueba) {
        return repository.obtenerMovimientosPorPrueba(idPrueba);
    }

    /**
     * Inserta una prueba junto con sus movimientos asociados
     * Operación transaccional gestionada en el Repository
     */
    public void guardarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        repository.insertarPruebaConMovimientos(prueba, movimientos);
    }

    /**
     * Actualiza una prueba junto con sus movimientos asociados
     */
    public void actualizarPruebaConMovimientos(Prueba prueba, List<Movimiento> movimientos) {
        repository.actualizarPruebaConMovimientos(prueba, movimientos);
    }

    /**
     * Obtiene la lista de jueces activos con su información
     * (ej: para asignarlos a una prueba)
     */
    public LiveData<List<Usuario>> getJuecesActivos() {
        return juezRepository.obtenerJuecesActivosConNombre();
    }

    /**
     * Elimina una prueba concreta
     */
    public void eliminarPrueba(Prueba p) {
        repository.eliminarPrueba(p);
    }

    /**
     * Obtiene el número de participantes por cada prueba
     * usado para estadísticas o visualización en UI
     */
    public LiveData<List<ConteoParticipantes>> getConteosParticipantes() {
        return participacionRepo.contarParticipantesPorTodasLasPruebas();
    }
}