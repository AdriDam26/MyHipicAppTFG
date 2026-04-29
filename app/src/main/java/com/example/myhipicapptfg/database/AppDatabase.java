package com.example.myhipicapptfg.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myhipicapptfg.dao.*;
import com.example.myhipicapptfg.entities.*;

@Database(
        entities = {
                Usuario.class, Alumno.class, Profesor.class, Juez.class,
                Equino.class, Cuidado.class,
                Pista.class, Clase.class, ReservaClase.class,
                RutaPersonal.class, CoordenadaRuta.class,
                Competicion.class, Prueba.class, Participacion.class,
                Movimiento.class, NotaMovimiento.class
        },
        version = 12,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UsuarioDao usuarioDao();
    public abstract AlumnoDao alumnoDao();
    public abstract ProfesorDao profesorDao();
    public abstract JuezDao juezDao();
    public abstract EquinoDao equinoDao();
    public abstract CuidadoDao cuidadoDao();
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
                            /* ⚠️ CUIDADO: .fallbackToDestructiveMigration() borrará la base de datos
                               SI cambias la versión (ej. de 7 a 8) y no has definido una migración.
                               Para un TFG está bien, pero no cambies el número de versión a la ligera.
                            */
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Mantén esto por si necesitas borrar datos manualmente desde un botón de ajustes,
    // pero NO lo llames automáticamente al iniciar.
    public void limpiarTodo() {
        new Thread(() -> clearAllTables()).start();
    }
}