package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.ReservaClaseDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.ReservaClase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReservaClaseRepository {

    private final ReservaClaseDao reservaClaseDao;
    private final ExecutorService executorService;

    // Canal para comunicar estado a la UI
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ReservaClaseRepository(@NonNull Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        reservaClaseDao = db.reservaClaseDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // ===============================
    // 🔹 INSERTAR RESERVA
    // ===============================

    public void insertarReserva(ReservaClase reserva) {
        executorService.execute(() -> {

            // 1️⃣ Validar que el alumno exista
            if (!reservaClaseDao.esAlumnoValido(reserva.idAlumno)) {
                estadoOperacion.postValue("ERROR_ALUMNO_NO_VALIDO");
                return;
            }

            // 2️⃣ Validar que la clase exista
            if (!reservaClaseDao.existeClase(reserva.idClase)) {
                estadoOperacion.postValue("ERROR_CLASE_NO_EXISTE");
                return;
            }

            // 3️⃣ Evitar duplicado (misma clase, mismo alumno)
            if (reservaClaseDao.yaEstaReservado(reserva.idAlumno, reserva.idClase)) {
                estadoOperacion.postValue("ERROR_YA_RESERVADO");
                return;
            }

            // 4️⃣ Validar aforo máximo (10 alumnos)
            int alumnosActuales = reservaClaseDao.contarAlumnosEnClase(reserva.idClase);
            if (alumnosActuales >= 10) {
                estadoOperacion.postValue("ERROR_CLASE_LLENA");
                return;
            }

            // 5️⃣ Insertar
            try {
                reservaClaseDao.insertarReserva(reserva);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // ===============================
    // 🔹 ELIMINAR RESERVA
    // ===============================

    public void eliminarReserva(ReservaClase reserva) {
        executorService.execute(() -> {
            try {
                reservaClaseDao.eliminarReserva(reserva);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }



}