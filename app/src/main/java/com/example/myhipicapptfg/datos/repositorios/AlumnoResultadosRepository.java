package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.example.myhipicapptfg.model.PruebaAlumno;
import com.example.myhipicapptfg.model.RankingItem;

import java.util.List;

/**
 * Repositorio encargado de gestionar la consulta de resultados
 * y calificaciones de los alumnos.
 *
 * Esta clase implementa el patrón Repository dentro de la arquitectura MVVM,
 * actuando como intermediaria entre los ViewModel y los DAO necesarios para
 * recuperar la información relacionada con las pruebas evaluadas.
 *
 * Su finalidad principal es proporcionar acceso a los resultados publicados
 * de un alumno, incluyendo clasificaciones, puntuaciones obtenidas y hojas
 * de calificaciones detalladas de cada prueba.
 *
 * Todas las operaciones realizadas por este repositorio son consultas de
 * lectura, por lo que no se requieren operaciones asíncronas mediante
 * ExecutorService.
 */
public class AlumnoResultadosRepository {

    /**
     * DAO encargado de gestionar las consultas relacionadas
     * con las participaciones de los alumnos en las pruebas.
     */
    private final ParticipacionDao   participacionDao;

    /**
     * DAO utilizado para recuperar las notas asignadas
     * a los movimientos evaluados.
     */
    private final NotaMovimientoDao  notaDao;

    /**
     * Constructor del repositorio.
     *
     * Inicializa los DAO necesarios para acceder a la información
     * de participaciones y calificaciones almacenadas en la base
     * de datos.
     *
     * @param application Contexto global de la aplicación.
     */
    public AlumnoResultadosRepository(@NonNull Application application) {
        AppDatabase db   = AppDatabase.getInstance(application);
        participacionDao = db.participacionDao();
        notaDao          = db.notaMovimientoDao();
    }

    /**
     * Obtiene todas las pruebas publicadas en las que ha participado
     * un alumno determinado.
     *
     * La información devuelta incluye los datos básicos de cada prueba
     * junto con los resultados publicados para el alumno.
     *
     * @param idAlumno Identificador del alumno.
     * @return Lista observable de pruebas publicadas.
     */
    public LiveData<List<PruebaAlumno>> getPruebasPublicadas(int idAlumno) {
        return participacionDao.getPruebasPublicadasByAlumno(idAlumno);
    }

    /**
     * Recupera la clasificación o ranking de una prueba concreta.
     *
     * La información obtenida contiene la posición de los participantes
     * y sus puntuaciones finales, permitiendo mostrar la clasificación
     * general de la prueba.
     *
     * @param idPrueba Identificador de la prueba.
     * @return Lista observable con la clasificación de la prueba.
     */
    public LiveData<List<RankingItem>> getRanking(int idPrueba) {
        return participacionDao.getRankingByPrueba(idPrueba);
    }


    /**
     * Obtiene la hoja de calificaciones detallada de una participación.
     *
     * La consulta devuelve cada movimiento evaluado junto con la nota
     * asignada por el juez, permitiendo mostrar el desglose completo
     * de la evaluación realizada.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Lista observable de movimientos con sus respectivas notas.
     */
    public LiveData<List<MovimientoConNota>> getHojaCalificaciones(int idParticipacion) {
        return notaDao.getHojaCalificaciones(idParticipacion);
    }

    /**
     * Recupera la información completa de una participación.
     *
     * Esta consulta permite acceder a los datos generales de la
     * evaluación, incluyendo la nota final, porcentaje obtenido
     * y otros datos asociados al resultado.
     *
     * @param idParticipacion Identificador de la participación.
     * @return Participación correspondiente al identificador indicado.
     */
    public LiveData<Participacion> getParticipacion(int idParticipacion) {
        return participacionDao.buscarPorId(idParticipacion);
    }
}