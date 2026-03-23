package com.example.myhipicapptfg.repository;


import android.app.Application;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.myhipicapptfg.dao.CalificacionDao;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Calificacion;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CalificacionRepository {

    private final CalificacionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> mensajeStatus = new MutableLiveData<>();

    public CalificacionRepository(Application application) {
        dao = TestDatabase.getInstance(application).calificacionDao();
        executor = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeStatus() { return mensajeStatus; }

    public void insertar(Calificacion cal) {
        executor.execute(() -> {
            // 1. Validar rangos de nota
            if (cal.nota < 0 || cal.nota > 10) {
                mensajeStatus.postValue("Error: La nota debe estar entre 0 y 10.");
                return;
            }

            // 2. Validar Existencias
            if (!dao.existeReprisa(cal.idReprisa)) {
                mensajeStatus.postValue("Error: El movimiento de la reprisa no existe.");
                return;
            }
            if (!dao.existeParticipacion(cal.idParticipacion)) {
                mensajeStatus.postValue("Error: La participación no existe.");
                return;
            }

            // 3. Validar duplicidad
            if (dao.yaEstaCalificado(cal.idParticipacion, cal.idReprisa)) {
                mensajeStatus.postValue("Error: Este movimiento ya ha sido calificado para este binomio.");
                return;
            }

            try {
                dao.insertarCalificacion(cal);
                mensajeStatus.postValue(null);
            } catch (Exception e) {
                mensajeStatus.postValue("Error al guardar la calificación.");
            }
        });
    }
}
