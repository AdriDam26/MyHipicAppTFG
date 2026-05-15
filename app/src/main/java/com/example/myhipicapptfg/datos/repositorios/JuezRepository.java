package com.example.myhipicapptfg.datos.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.JuezDao;
import com.example.myhipicapptfg.datos.local.dao.MovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.NotaMovimientoDao;
import com.example.myhipicapptfg.datos.local.dao.ParticipacionDao;
import com.example.myhipicapptfg.datos.local.dao.PruebaDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.model.ParticipacionDetalle;
import com.example.myhipicapptfg.model.PruebaConCompeticion;

import java.util.List;
import java.util.concurrent.ExecutorService;

public class JuezRepository {

    private final JuezDao juezDao;
    private final UsuarioDao usuarioDao;

    private final PruebaDao pruebaDao;
    private final ParticipacionDao participacionDao;

    private final MovimientoDao movimientoDao;
    private final NotaMovimientoDao notaMovimientoDao;


    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public JuezRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        juezDao = db.juezDao();
        usuarioDao = db.usuarioDao();

        pruebaDao         = db.pruebaDao();
        participacionDao  = db.participacionDao();

        movimientoDao      = db.movimientoDao();
        notaMovimientoDao  = db.notaMovimientoDao();



        executorService = AppDatabase.getDatabaseExecutor();
    }

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =====================================
    // 🔹 INSERTAR
    // =====================================

    public void insertarJuez(Juez juez) {

        executorService.execute(() -> {

            // 1️⃣ Verificar que existe el Usuario base
            Usuario usuarioBase = usuarioDao.buscarPorIdSync(juez.idJuez);

            if (usuarioBase == null) {
                estadoOperacion.postValue("ERROR_USUARIO_NO_EXISTE");
                return;
            }

            // 2️⃣ Verificar que el tipo sea JUEZ
            if (!Usuario.TIPO_JUEZ.equals(usuarioBase.tipo)) {
                estadoOperacion.postValue("ERROR_TIPO_USUARIO_INVALIDO");
                return;
            }

            // 3️⃣ Verificar que no exista ya la licencia
            if (juezDao.buscarPorLicenciaSync(juez.numeroLicencia) != null) {
                estadoOperacion.postValue("ERROR_LICENCIA_DUPLICADA");
                return;
            }

            try {
                juezDao.insertarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 UPDATE
    // =====================================

    public void actualizarJuez(Juez juez) {
        executorService.execute(() -> {
            try {
                juezDao.actualizarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =====================================
    // 🔹 DELETE
    // =====================================

    public void eliminarJuez(Juez juez) {
        executorService.execute(() -> {
            try {
                juezDao.eliminarJuez(juez);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    public LiveData<List<Juez>> obtenerTodosJueces() {
        return juezDao.obtenerTodosJueces();
    }

    public LiveData<Juez> buscarPorId(int id) {
        return juezDao.buscarPorId(id);
    }

    public LiveData<Integer> contarJueces() {
        return juezDao.contarJueces();
    }

    public LiveData<List<Usuario>> obtenerJuecesActivosConNombre() {
        return juezDao.obtenerJuecesActivosConNombre();
    }


    // ── Pruebas del juez ──────────────────────────────────────────────────────
    public LiveData<List<PruebaConCompeticion>> getPruebasByJuez(int idJuez) {
        return pruebaDao.getPruebasByJuez(idJuez);
    }

    // ── Participantes de una prueba ───────────────────────────────────────────
    public LiveData<List<ParticipacionDetalle>> getParticipantesByPrueba(int idPrueba) {
        return participacionDao.getParticipantesByPrueba(idPrueba);
    }


    /** Devuelve los movimientos de la prueba asociada a una participación */
    public LiveData<List<Movimiento>> getMovimientosByPrueba(int idPrueba) {
        return movimientoDao.getMovimientosByPrueba(idPrueba);
    }

    /** Notas ya guardadas para una participación */
    public LiveData<List<NotaMovimiento>> getNotasByParticipacion(int idParticipacion) {
        return notaMovimientoDao.getNotasByParticipacion(idParticipacion);
    }

    /**
     * Guarda todas las notas + recalcula nota final, porcentaje
     * y aplica la corrección. Todo en un solo hilo background.
     */
    public void guardarPuntuacion(int idParticipacion,
                                  List<NotaMovimiento> notas,
                                  double notaFinal,
                                  double porcentaje,
                                  double correccion,
                                  boolean eliminado) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            notaMovimientoDao.insertOrUpdateAll(notas);
            participacionDao.updateResultado(
                    idParticipacion, notaFinal, porcentaje, correccion, eliminado);
        });
    }



}