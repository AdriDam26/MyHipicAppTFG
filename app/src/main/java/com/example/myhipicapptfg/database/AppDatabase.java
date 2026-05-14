package com.example.myhipicapptfg.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.myhipicapptfg.dao.*;
import com.example.myhipicapptfg.entities.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {
                Usuario.class, Alumno.class, Profesor.class, Juez.class,
                Equino.class, Cuidado.class,
                Pista.class, Clase.class, ReservaClase.class,
                RutaPersonal.class, CoordenadaRuta.class,
                Competicion.class, Prueba.class, Participacion.class,
                Movimiento.class, NotaMovimiento.class
        },
        version = 24,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // Pool de hilos compartido por todos los repositorios
    private static final ExecutorService DATABASE_EXECUTOR =
            Executors.newFixedThreadPool(4);

    public static ExecutorService getDatabaseExecutor() {
        return DATABASE_EXECUTOR;
    }

    // DAOs
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
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                                    super.onCreate(db);
                                    android.util.Log.e("CADENA", "⚠️ BASE DE DATOS RECREADA - todos los datos borrados");
                                }

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);
                                    android.util.Log.d("CADENA", "BD abierta - versión: " + db.getVersion());
                                }

                                @Override
                                public void onDestructiveMigration(@NonNull SupportSQLiteDatabase db) {
                                    super.onDestructiveMigration(db);
                                    android.util.Log.e("CADENA", "⚠️ MIGRACIÓN DESTRUCTIVA EJECUTADA");
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Limpia todas las tablas y reinicia los contadores autoincrementales.
     * Llama a este método SOLO cuando realmente quieras borrar todos los datos
     * (por ejemplo, desde un botón de "Resetear" en ajustes).
     */
    public void limpiarManual() {
        DATABASE_EXECUTOR.execute(() -> {
            clearAllTables();
            getOpenHelper().getWritableDatabase()
                    .execSQL("DELETE FROM sqlite_sequence");
        });
    }

    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);
}