package com.example.myhipicapptfg.repository;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.dao.JuezDao;
import com.example.myhipicapptfg.dao.UsuarioDao;
import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Juez;
import com.example.myhipicapptfg.entities.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JuezRepository {

    private final JuezDao juezDao;
    private final UsuarioDao usuarioDao;
    private final ExecutorService executorService;
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();

    public JuezRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        juezDao = db.juezDao();
        usuarioDao = db.usuarioDao();

        executorService = Executors.newSingleThreadExecutor();
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

    // =====================================
    // 🔹 LECTURA (LiveData)
    // =====================================

    public LiveData<List<Juez>> obtenerTodosJueces() {
        return juezDao.obtenerTodosJueces();
    }

    public LiveData<Juez> buscarPorId(int id) {
        return juezDao.buscarPorId(id);
    }

    public LiveData<Integer> contarJueces() {
        return juezDao.contarJueces();
    }
}