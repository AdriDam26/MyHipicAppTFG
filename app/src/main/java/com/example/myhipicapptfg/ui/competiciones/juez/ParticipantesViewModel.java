package com.example.myhipicapptfg.ui.competiciones.juez;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.model.ParticipacionDetalle;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;
import com.example.myhipicapptfg.datos.repository.PruebaRepository;

import java.util.List;

public class ParticipantesViewModel extends AndroidViewModel {

    private final JuezRepository   juezRepo;
    private final PruebaRepository pruebaRepo;

    private LiveData<List<ParticipacionDetalle>> participantes;
    private LiveData<Boolean>                    publicado;
    private int idPrueba;

    public ParticipantesViewModel(@NonNull Application application) {
        super(application);
        juezRepo   = new JuezRepository(application);
        pruebaRepo = new PruebaRepository(application);
    }

    public LiveData<List<ParticipacionDetalle>> getParticipantes(int idPrueba) {
        if (participantes == null) {
            this.idPrueba  = idPrueba;
            participantes  = juezRepo.getParticipantesByPrueba(idPrueba);
            publicado      = pruebaRepo.isPublicado(idPrueba);
        }
        return participantes;
    }

    public LiveData<Boolean> isPublicado() {
        return publicado;
    }

    public void actualizarPublicado(boolean nuevoEstado) {
        pruebaRepo.actualizarPublicado(idPrueba, nuevoEstado);
    }
}