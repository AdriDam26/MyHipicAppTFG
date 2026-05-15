package com.example.myhipicapptfg.datos.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class ParticipacionRepository {

    private final ParticipacionDao dao;
    private final ExecutorService executor;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public ParticipacionRepository(Application application) {
        dao = AppDatabase.getInstance(application).participacionDao();
        executor = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // ==========================================
    // 🔹 INSERTAR
    // ==========================================
    public void insertar(Participacion p) {

        estadoOperacion.postValue(null);

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
        estadoOperacion.postValue(null);
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

        estadoOperacion.postValue(null);
        executor.execute(() -> {
            try {
                dao.eliminarParticipacion(p);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    public LiveData<Participacion> buscarPorId(int id) {
        return dao.buscarPorId(id);
    }

    public LiveData<List<Participacion>> obtenerPorPrueba(int idPrueba) {
        return dao.obtenerPorPrueba(idPrueba);
    }

    public LiveData<Integer> obtenerSiguienteOrden(int idPrueba) {
        return dao.obtenerSiguienteOrden(idPrueba);
    }






}