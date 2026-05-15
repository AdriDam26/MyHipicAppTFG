package com.example.myhipicapptfg.ui.admin.equinos;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.datos.repository.EquinoRepository;
import com.example.myhipicapptfg.datos.repository.UsuarioRepository;

import java.util.List;

public class GestionEquinoViewModel extends AndroidViewModel {

    private final EquinoRepository repository;
    private final UsuarioRepository usuarioRepository;

    public GestionEquinoViewModel(@NonNull Application application) {
        super(application);
        // Inicializamos el repositorio con la aplicación
        repository = new EquinoRepository(application);
        usuarioRepository = new UsuarioRepository(application);
    }

    // =====================================
    // 🔹 MÉTODOS DE OPERACIÓN
    // =====================================

    /**
     * Llama al repositorio para insertar un equino con las validaciones
     * de microchip, cuadra y propietario.
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


    public void actualizar(Equino equino) {
        repository.actualizarEquino(equino);
    }

    public void eliminar(Equino equino) {
        repository.eliminarEquino(equino);
    }

    public LiveData<Equino> buscarPorId(int id) {
        return repository.buscarPorId(id);
    }
    public LiveData<List<Equino>> obtenerTodosEquinos() {
        return repository.obtenerTodosEquinos();
    }

    public LiveData<List<Usuario>> obtenerPropietarios() {
        return usuarioRepository.obtenerUsuariosPorTipo(Usuario.TIPO_PROPIETARIO);
    }

}