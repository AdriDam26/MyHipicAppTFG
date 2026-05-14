package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.dao.ParticipacionDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Participacion;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.example.myhipicapptfg.model.PruebaAlumno;
import com.example.myhipicapptfg.model.RankingItem;

import java.util.List;

public class AlumnoResultadosRepository {

    private final ParticipacionDao   participacionDao;
    private final NotaMovimientoDao  notaDao;

    public AlumnoResultadosRepository(@NonNull Application application) {
        AppDatabase db   = AppDatabase.getInstance(application);
        participacionDao = db.participacionDao();
        notaDao          = db.notaMovimientoDao();
    }

    public LiveData<List<PruebaAlumno>> getPruebasPublicadas(int idAlumno) {
        return participacionDao.getPruebasPublicadasByAlumno(idAlumno);
    }

    public LiveData<List<RankingItem>> getRanking(int idPrueba) {
        return participacionDao.getRankingByPrueba(idPrueba);
    }




    public LiveData<List<MovimientoConNota>> getHojaCalificaciones(int idParticipacion) {
        return notaDao.getHojaCalificaciones(idParticipacion);
    }

    public LiveData<Participacion> getParticipacion(int idParticipacion) {
        return participacionDao.buscarPorId(idParticipacion);
    }
}