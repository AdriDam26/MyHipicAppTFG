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

                Usuario.class,
                Alumno.class,
                Profesor.class,
                Juez.class,

                Equino.class,
                Cuidado.class,

                Pista.class,
                Clase.class,
                ReservaClase.class,

                RutaPersonal.class,
                CoordenadaRuta.class,

                Competicion.class,
                Prueba.class,
                Participacion.class,

                Movimiento.class,
                NotaMovimiento.class
        },
        version = 1,
        exportSchema = false
)
public abstract class AppDatabase extends RoomDatabase {

    // ---------------- DAOs ----------------
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

    // ---------------- SINGLETON ----------------
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

                            // 🔥 CALLBACK: modo TEST (reinicio automático)
                            .addCallback(new RoomDatabase.Callback() {

                                @Override
                                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                                    super.onOpen(db);

                                    // 🔥 ORDEN IMPORTANTE (por FOREIGN KEYS)
                                    db.execSQL("DELETE FROM Nota_Movimiento");
                                    db.execSQL("DELETE FROM Movimiento");
                                    db.execSQL("DELETE FROM Participacion");
                                    db.execSQL("DELETE FROM Prueba");
                                    db.execSQL("DELETE FROM Competicion");

                                    db.execSQL("DELETE FROM ReservaClase");
                                    db.execSQL("DELETE FROM Clase");
                                    db.execSQL("DELETE FROM Pista");

                                    db.execSQL("DELETE FROM Cuidado");
                                    db.execSQL("DELETE FROM Equino");

                                    db.execSQL("DELETE FROM CoordenadaRuta");
                                    db.execSQL("DELETE FROM RutaPersonal");

                                    db.execSQL("DELETE FROM Juez");
                                    db.execSQL("DELETE FROM Profesor");
                                    db.execSQL("DELETE FROM Alumno");
                                    db.execSQL("DELETE FROM Usuario");
                                }


                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // 🔥 utilidad manual si la quieres usar desde app
    public void limpiarTodo() {
        clearAllTables();
    }
}