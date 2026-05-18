package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.RutaPersonalDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class RutaPersonalRepository {

    private final AppDatabase db;                   // ✅ campo para runInTransaction
    private final RutaPersonalDao dao;
    private final UsuarioDao usuarioDao;            // ✅ para validar propietario desde su DAO
    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();
    private final MutableLiveData<Long> idInsertado = new MutableLiveData<>();

    public RutaPersonalRepository(@NonNull Application application) {
        db = AppDatabase.getInstance(application);
        dao = db.rutaPersonalDao();
        usuarioDao = db.usuarioDao();               // ✅ validación en el DAO correcto
        executorService = AppDatabase.getDatabaseExecutor();
    }

    // =====================================
    // GUARDAR RUTA COMPLETA
    // =====================================

    public void guardarRutaCompleta(RutaPersonal ruta, List<CoordenadaRuta> puntos) {
        executorService.execute(() -> {

            // ✅ Validación usando UsuarioDao, no RutaPersonalDao
            Usuario propietario = usuarioDao.buscarPorIdSync(ruta.idPropietario);
            if (propietario == null || !Usuario.TIPO_PROPIETARIO.equals(propietario.tipo)) {
                estadoOperacion.postValue("ERROR_PROPIETARIO_NO_VALIDO");
                return;
            }

            try {
                // ✅ Transacción en el Repository con db.runInTransaction
                final long[] idRuta = {-1};

                db.runInTransaction(() -> {
                    idRuta[0] = dao.insertar(ruta);

                    if (puntos != null && !puntos.isEmpty()) {
                        for (CoordenadaRuta p : puntos) {
                            p.idRutaPersonal = (int) idRuta[0];
                        }
                        dao.insertarCoordenadas(puntos);
                    }
                });

                idInsertado.postValue(idRuta[0]);
                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // ACTUALIZAR
    // =====================================

    public void actualizar(RutaPersonal ruta) {
        executorService.execute(() -> {
            try {
                dao.actualizar(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // ELIMINAR
    // =====================================

    public void eliminar(RutaPersonal ruta) {
        executorService.execute(() -> {
            try {
                dao.eliminar(ruta);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // LECTURA (LiveData)
    // =====================================

    public LiveData<List<RutaPersonal>> obtenerTodas() {
        return dao.obtenerTodas();
    }

    public LiveData<List<RutaPersonal>> obtenerPorPropietario(int idPropietario) {
        return dao.obtenerPorPropietario(idPropietario);
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    public LiveData<Long> getIdInsertado() {
        return idInsertado;
    }
}