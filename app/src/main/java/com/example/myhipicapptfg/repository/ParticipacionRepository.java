package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.dao.ParticipacionDao;
import com.example.myhipicapptfg.entities.Participacion;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ParticipacionRepository {

    private final ParticipacionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ParticipacionRepository(Application application) {
        dao = AppDatabase.getInstance(application).participacionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // ==========================================
    // 🔹 INSERTAR
    // ==========================================
    public void insertar(Participacion p) {

        executor.execute(() -> {

            // 🔹 Validar Alumno
            if (!dao.existeAlumno(p.idAlumno)) {
                estadoOperacion.postValue("ERROR_ALUMNO_NO_EXISTE");
                return;
            }

            // 🔹 Validar Equino
            if (!dao.existeEquino(p.idEquino)) {
                estadoOperacion.postValue("ERROR_EQUINO_NO_EXISTE");
                return;
            }

            // 🔹 Validar Prueba
            if (!dao.existePrueba(p.idPrueba)) {
                estadoOperacion.postValue("ERROR_PRUEBA_NO_EXISTE");
                return;
            }

            // 🔹 Evitar duplicados
            if (dao.existeParticipacion(p.idAlumno, p.idEquino, p.idPrueba)) {
                estadoOperacion.postValue("ERROR_YA_INSCRITO");
                return;
            }

            try {
                dao.insertarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // ==========================================
    // 🔹 ACTUALIZAR
    // ==========================================
    public void actualizar(Participacion p) {

        executor.execute(() -> {
            try {
                dao.actualizarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // ==========================================
    // 🔹 ELIMINAR
    // ==========================================
    public void eliminar(Participacion p) {

        executor.execute(() -> {
            try {
                dao.eliminarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }
}