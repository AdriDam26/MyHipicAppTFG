package com.example.myhipicapptfg.database;



import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myhipicapptfg.dao.AlumnoDao;
import com.example.myhipicapptfg.dao.AlumnoDisciplinaDao;
import com.example.myhipicapptfg.dao.CalificacionDao;
import com.example.myhipicapptfg.dao.ClaseDao;
import com.example.myhipicapptfg.dao.CompeticionDao;
import com.example.myhipicapptfg.dao.ConvocatoriaDao;
import com.example.myhipicapptfg.dao.CoordenadaRutaDao;
import com.example.myhipicapptfg.dao.CuadraDao;
import com.example.myhipicapptfg.dao.CuidadoDao;
import com.example.myhipicapptfg.dao.DisciplinaDao;
import com.example.myhipicapptfg.dao.DisciplinaEquinoDao;
import com.example.myhipicapptfg.dao.EquinoDao;
import com.example.myhipicapptfg.dao.ParticipacionDao;
import com.example.myhipicapptfg.dao.PistaDao;
import com.example.myhipicapptfg.dao.PlantillaPruebaDao;
import com.example.myhipicapptfg.dao.ProfesorDao;
import com.example.myhipicapptfg.dao.ProfesorDisciplinaDao;
import com.example.myhipicapptfg.dao.PropietarioDao;
import com.example.myhipicapptfg.dao.ReprisaDao;
import com.example.myhipicapptfg.dao.ReservaClaseDao;
import com.example.myhipicapptfg.dao.RutaPersonalDao;
import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import com.example.myhipicapptfg.entities.Calificacion;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Competicion;
import com.example.myhipicapptfg.entities.Convocatoria;
import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.entities.Cuidado;
import com.example.myhipicapptfg.entities.Disciplina;
import com.example.myhipicapptfg.entities.DisciplinaEquino;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Participacion;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.PlantillaPrueba;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.ProfesorDisciplina;
import com.example.myhipicapptfg.entities.Propietario;
import com.example.myhipicapptfg.entities.Reprisa;
import com.example.myhipicapptfg.entities.ReservaClase;
import com.example.myhipicapptfg.entities.RutaPersonal;
import com.example.myhipicapptfg.entities.Usuario;

@Database(
        entities = {
                Usuario.class,
                Alumno.class,
                Profesor.class,
                Propietario.class,
                Disciplina.class,
                Pista.class,
                Clase.class,
                RutaPersonal.class,
                CoordenadaRuta.class,
                AlumnoDisciplina.class,
                ProfesorDisciplina.class,
                Equino.class,
                Cuadra.class,
                Cuidado.class,
                DisciplinaEquino.class,
                ReservaClase.class,
                PlantillaPrueba.class,
                Reprisa.class,
                Competicion.class,
                Convocatoria.class,
                Participacion.class,
                Calificacion.class
        },
        version = 1
)
public abstract class TestDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract AlumnoDao alumnoDao();
    public abstract ProfesorDao profesorDao();
    public abstract PropietarioDao propietarioDao();
    public abstract DisciplinaDao disciplinaDao();
    public abstract PistaDao pistaDao();
    public abstract ClaseDao claseDao();
    public abstract RutaPersonalDao rutaPersonalDao();
    public abstract CoordenadaRutaDao coordenadaRutaDao();
    public abstract AlumnoDisciplinaDao alumnoDisciplinaDao();
    public abstract ProfesorDisciplinaDao profesorDisciplinaDao();
    public abstract EquinoDao equinoDao();
    public abstract CuadraDao cuadraDao();
    public abstract CuidadoDao cuidadoDao();
    public abstract DisciplinaEquinoDao disciplinaEquinoDao();
    public abstract ReservaClaseDao reservaClaseDao();
    public abstract PlantillaPruebaDao plantillaPruebaDao();
    public abstract ReprisaDao reprisaDao();
    public abstract CompeticionDao competicionDao();
    public abstract ConvocatoriaDao convocatoriaDao();
    public abstract ParticipacionDao participacionDao();
    public abstract CalificacionDao calificacionDao();

    private static TestDatabase INSTANCE;

    public static TestDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            TestDatabase.class,
                            "myhipicapp_test.db" // nombre de la base de datos
                    ).fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANCE;
    }

    public void limpiarTablas() {
        this.clearAllTables();
    }
}