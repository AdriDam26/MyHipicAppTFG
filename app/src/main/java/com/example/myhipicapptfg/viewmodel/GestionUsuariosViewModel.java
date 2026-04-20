package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.UsuarioRepository;
import java.util.List;
import java.util.concurrent.Executors;

public class GestionUsuariosViewModel extends AndroidViewModel {
    private final UsuarioRepository usuarioRepo;

    public GestionUsuariosViewModel(@NonNull Application application) {
        super(application);
        usuarioRepo = new UsuarioRepository(application);
    }

    public LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo) {
        return usuarioRepo.obtenerUsuariosPorTipo(tipo);
    }

    public void eliminarUsuario(Usuario u) {
        Executors.newSingleThreadExecutor().execute(() -> {
            usuarioRepo.eliminarUsuario(u);
        });
    }
}