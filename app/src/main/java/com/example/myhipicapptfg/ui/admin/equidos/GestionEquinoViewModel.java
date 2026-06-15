package com.example.myhipicapptfg.ui.admin.equidos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repositorios.EquinoRepository;
import com.example.myhipicapptfg.datos.repositorios
        .UsuarioRepository;

import java.util.List;

/**
 * ViewModel encargado de la gestión de Equinos en el módulo de administración.
 *
 * Responsabilidades:
 * - Insertar, actualizar y eliminar equinos
 * - Consultar equinos por ID o lista completa
 * - Obtener propietarios disponibles
 * - Exponer el estado de operaciones al UI
 *
 */
public class GestionEquinoViewModel extends AndroidViewModel {

    // Repositorios
    private final EquinoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public GestionEquinoViewModel(@NonNull Application application) {
        super(application);
        // Inicializamos el repositorio con la aplicación
        repository = new EquinoRepository(application);
        usuarioRepository = new UsuarioRepository(application);
    }

    /**
     * Inserta un nuevo equino aplicando validaciones en el repositorio
     * (microchip, cuadra, propietario, etc.)
     */
    public void insertar(Equino equino) {
        repository.insertarEquino(equino);
    }

    /**
     * Devuelve el LiveData que informa sobre el resultado de la operación
     * (EXITO, ERROR_MICROCHIP_DUPLICADO, etc.)
     */
    public LiveData<String> getEstadoOperacion() {
        return repository.getEstadoOperacion();
    }


    /**
     * Actualiza un equino existente
     */
    public void actualizar(Equino equino) {
        repository.actualizarEquino(equino);
    }

    /**
     * Elimina un equino del sistema
     */
    public void eliminar(Equino equino) {
        repository.eliminarEquino(equino);
    }

    /**
     * Obtiene un equino por su ID
     */
    public LiveData<Equino> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }

    /**
     * Obtiene la lista completa de equinos
     */
    public LiveData<List<Equino>> obtenerTodosEquinos() {
        return repository.obtenerTodosEquinos();
    }

    /**
     * Obtiene los usuarios con rol de propietario
     * para asignarlos a un equino
     */
    public LiveData<List<Usuario>> obtenerPropietarios() {
        return usuarioRepository.obtenerUsuariosPorTipo(Usuario.TIPO_PROPIETARIO);
    }

}