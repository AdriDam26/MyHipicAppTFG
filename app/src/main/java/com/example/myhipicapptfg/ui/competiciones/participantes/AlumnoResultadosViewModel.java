package com.example.myhipicapptfg.ui.competiciones.participantes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.example.myhipicapptfg.model.PruebaAlumno;
import com.example.myhipicapptfg.model.RankingItem;
import com.example.myhipicapptfg.datos.repository.AlumnoResultadosRepository;

import java.util.List;

public class AlumnoResultadosViewModel extends AndroidViewModel {

    private final AlumnoResultadosRepository repo;

    private LiveData<List<PruebaAlumno>>      pruebas;
    private LiveData<List<RankingItem>>        ranking;
    private LiveData<List<MovimientoConNota>>  hoja;
    private LiveData<Participacion>            participacion;

    public AlumnoResultadosViewModel(@NonNull Application application) {
        super(application);
        repo = new AlumnoResultadosRepository(application);
    }

    public LiveData<List<PruebaAlumno>> getPruebas(int idAlumno) {
        if (pruebas == null)
            pruebas = repo.getPruebasPublicadas(idAlumno);
        return pruebas;
    }

    public LiveData<List<RankingItem>> getRanking(int idPrueba) {
        if (ranking == null)
            ranking = repo.getRanking(idPrueba);
        return ranking;
    }

    public LiveData<List<MovimientoConNota>> getHoja(int idParticipacion) {
        if (hoja == null)
            hoja = repo.getHojaCalificaciones(idParticipacion);
        return hoja;
    }

    public LiveData<Participacion> getParticipacion(int idParticipacion) {
        if (participacion == null)
            participacion = repo.getParticipacion(idParticipacion);
        return participacion;
    }
}