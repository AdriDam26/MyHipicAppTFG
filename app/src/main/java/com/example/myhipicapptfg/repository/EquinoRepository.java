package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.EquinoDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Equino;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EquinoRepository {

    private final EquinoDao equinoDao;
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public EquinoRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);
        equinoDao = db.equinoDao();

        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR (ASYNC)
    // =====================================

    public void insertarEquino(Equino equino) {

        executorService.execute(() -> {

            // 1️⃣ Validar microchip único
            if (equinoDao.existeMicrochip(equino.numeroMicrochip)) {
                estadoOperacion.postValue("ERROR_MICROCHIP_DUPLICADO");
                return;
            }

            // 2️⃣ Validar cuadra
            if (equinoDao.esCuadraOcupada(equino.numeroCuadra)) {
                estadoOperacion.postValue("ERROR_CUADRA_OCUPADA");
                return;
            }

            // 3️⃣ Validar propietario (puede ser null)
            if (equino.idUsuario != null) {
                if (!equinoDao.esPropietarioValido(equino.idUsuario)) {
                    estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                    return;
                }
            }

            try {
                equinoDao.insertarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    public void actualizarEquino(Equino equino) {
        executorService.execute(() -> {
            // Nota: Si el microchip se puede editar, deberías validar aquí
            // que el nuevo microchip no pertenezca a OTRO caballo distinto.

            if (equinoDao.esCuadraOcupadaPorOtro(equino.numeroCuadra, equino.idEquino)) {
                estadoOperacion.postValue("ERROR_CUADRA_OCUPADA");
                return;
            }

            if (equino.idUsuario != null) {
                if (!equinoDao.esPropietarioValido(equino.idUsuario)) {
                    estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                    return;
                }
            }

            try {
                equinoDao.actualizarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 ELIMINAR (ASYNC)
    // =====================================
    public void eliminarEquino(Equino equino) {
        executorService.execute(() -> {
            try {
                equinoDao.eliminarEquino(equino);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public LiveData<List<Equino>> obtenerTodosEquinos() {
        return equinoDao.obtenerTodosEquinos();
    }

    public LiveData<Equino> buscarPorId(int id) {
        return equinoDao.buscarEquinoPorId(id);
    }

    public LiveData<List<Equino>> obtenerEquinosDoma() {
        return equinoDao.obtenerEquinosDoma();
    }


    public Equino buscarPorIdSync(int id) {
        return equinoDao.buscarPorIdSync(id);
    }

    public Equino buscarPorMicrochipSync(String microchip) {
        return equinoDao.buscarPorMicrochipSync(microchip);
    }


}