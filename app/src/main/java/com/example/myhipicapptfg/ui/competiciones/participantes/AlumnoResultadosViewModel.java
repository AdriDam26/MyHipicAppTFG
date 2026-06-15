package com.example.myhipicapptfg.ui.competiciones.participantes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.example.myhipicapptfg.model.PruebaAlumno;
import com.example.myhipicapptfg.model.RankingItem;
import com.example.myhipicapptfg.datos.repositorios.AlumnoResultadosRepository;

import java.util.List;

/**
 * ViewModel encargado de gestionar los resultados de los alumnos en las competiciones.
 * Actúa como intermediario entre la interfaz de usuario y el repositorio,
 * proporcionando acceso a las pruebas, rankings, hojas de calificaciones
 * y datos de participación.
 */
public class AlumnoResultadosViewModel extends AndroidViewModel {

    // Repositorio que centraliza el acceso a los datos de resultados
    private final AlumnoResultadosRepository repo;

    // Lista de pruebas publicadas del alumno
    private LiveData<List<PruebaAlumno>>      pruebas;

    // Ranking asociado a una prueba
    private LiveData<List<RankingItem>>        ranking;

    // Hoja de calificaciones de una participación
    private LiveData<List<MovimientoConNota>>  hoja;
    // Información de una participación concreta
    private LiveData<Participacion>            participacion;


    /**
     * Constructor del ViewModel.
     * Inicializa el repositorio encargado de obtener los datos.
     */
    public AlumnoResultadosViewModel(@NonNull Application application) {
        super(application);
        repo = new AlumnoResultadosRepository(application);
    }

    /**
     * Devuelve las pruebas publicadas en las que participa el alumno.
     */
    public LiveData<List<PruebaAlumno>> getPruebas(int idAlumno) {
        if (pruebas == null){
            pruebas = repo.getPruebasPublicadas(idAlumno);
        }
        return pruebas;
    }

    /**
     * Devuelve el ranking de una prueba.
     */
    public LiveData<List<RankingItem>> getRanking(int idPrueba) {
        if (ranking == null){
            ranking = repo.getRanking(idPrueba);
        }
        return ranking;
    }

    /**
     * Devuelve la hoja de calificaciones asociada a una participación.
     */
    public LiveData<List<MovimientoConNota>> getHoja(int idParticipacion) {
        if (hoja == null){
            hoja = repo.getHojaCalificaciones(idParticipacion);
        }
        return hoja;
    }

    /**
     * Devuelve la información de una participación concreta.
     */
    public LiveData<Participacion> getParticipacion(int idParticipacion) {
        if (participacion == null){
            participacion = repo.getParticipacion(idParticipacion);
        }
        return participacion;
    }
}