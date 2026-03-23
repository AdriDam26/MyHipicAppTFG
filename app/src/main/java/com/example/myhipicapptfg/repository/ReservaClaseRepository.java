package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myhipicapptfg.dao.ReservaClaseDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.ReservaClase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaClaseRepository {

    private final ReservaClaseDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public ReservaClaseRepository(Application application) {
        dao = TestDatabase.getInstance(application).reservaClaseDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() { return mensajeStatus; }

    public void insertar(ReservaClase reserva) {
        executor.execute(() -> {
            // 1. Validar identidades
            if (!dao.esAlumnoValido(reserva.idAlumno)) {
                mensajeStatus.postValue("Error: El usuario no es un Alumno válido.");
                return;
            }
            if (!dao.existeClase(reserva.idClase)) {
                mensajeStatus.postValue("Error: La Clase no existe.");
                return;
            }

            // 2. Validar si ya está reservado (Evitar duplicados)
            if (dao.yaEstaReservado(reserva.idAlumno, reserva.idClase)) {
                mensajeStatus.postValue("Error: El alumno ya está inscrito en esta clase.");
                return;
            }

            // 3. Validar aforo máximo (10 alumnos)
            int alumnosActuales = dao.contarAlumnosEnClase(reserva.idClase);
            if (alumnosActuales >= 10) {
                mensajeStatus.postValue("Error: La clase está llena (Máximo 10 alumnos).");
                return;
            }

            // 4. Inserción
            try {
                dao.insertar(reserva);
                mensajeStatus.postValue(null); // Éxito
            } catch (Exception e) {
                mensajeStatus.postValue("Error técnico al procesar la reserva.");
            }
        });
    }
}