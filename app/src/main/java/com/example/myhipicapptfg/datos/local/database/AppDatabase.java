package com.example.myhipicapptfg.datos.local.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myhipicapptfg.datos.local.dao.AlumnoDao;
import com.example.myhipicapptfg.datos.local.dao.ClaseDao;
import com.example.myhipicapptfg.datos.local.dao.CompeticionDao;
import com.example.myhipicapptfg.datos.local.dao.CoordenadaRutaDao;
import com.example.myhipicapptfg.datos.local.dao.EquinoDao;
import com.example.myhipicapptfg.datos.local.dao.JuezDao;
import com.example.myhipicapptfg.datos.local.dao.MovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.dao.ProfesorDao;
import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.dao.ReservaClaseDao;
import com.example.myhipicapptfg.datos.local.dao.RutaPersonalDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;
import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.datos.local.entidades.ReservaClase;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Base de datos principal de la aplicación MyHipica.
 *
 * Utiliza Room como capa de abstracción sobre SQLite.
 * Define todas las entidades, DAOs y configuración global.
 */
@Database(
        entities = {
                Usuario.class, Alumno.class, Profesor.class, Juez.class,
                Equino.class,
                Pista.class, Clase.class, ReservaClase.class,
                RutaPersonal.class, CoordenadaRuta.class,
                Competicion.class, Prueba.class, Participacion.class,
                Movimiento.class, NotaMovimiento.class
        },
        version = 28, // Versión actual de la BD (cambiar al modificar esquema)
        exportSchema = false // No exporta esquema a archivos JSON
)
public abstract class AppDatabase extends RoomDatabase {


    /**
     * Pool de hilos compartido para operaciones en base de datos.
     * Evita bloquear el hilo principal (UI).
     */
    private static final ExecutorService DATABASE_EXECUTOR =
            Executors.newFixedThreadPool(4);


    /**
     * Permite acceder al executor desde otras capas (repositorios).
     */
    public static ExecutorService getDatabaseExecutor() {
        return DATABASE_EXECUTOR;
    }


    // DAOs de acceso a datos
    public abstract UsuarioDao usuarioDao();
    public abstract AlumnoDao alumnoDao();
    public abstract ProfesorDao profesorDao();
    public abstract JuezDao juezDao();
    public abstract EquinoDao equinoDao();
    public abstract PistaDao pistaDao();
    public abstract ClaseDao claseDao();
    public abstract ReservaClaseDao reservaClaseDao();
    public abstract RutaPersonalDao rutaPersonalDao();
    public abstract CoordenadaRutaDao coordenadaRutaDao();
    public abstract CompeticionDao competicionDao();
    public abstract PruebaDao pruebaDao();
    public abstract ParticipacionDao participacionDao();
    public abstract MovimientoDao movimientoDao();
    public abstract NotaMovimientoDao notaMovimientoDao();

    // Instancia única (Singleton)
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    "myhipica_app.db"
                            )
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }




}