package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.ClaseDao;
import com.example.myhipicapptfg.datos.local.dao.PistaDao;
import com.example.myhipicapptfg.datos.local.dao.ProfesorDao;
import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.ui.admin.clases.ClaseValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ClaseRepository {

    private final ClaseDao claseDao;
    private final ProfesorDao profesorDao;
    private final PistaDao pistaDao;
    private final UsuarioDao usuarioDao;

    private final ExecutorService executorService;

    private final MutableLiveData<String> estadoOperacion =
            new MutableLiveData<>();

    public ClaseRepository(@NonNull Application application) {

        AppDatabase db = AppDatabase.getInstance(application);

        claseDao = db.claseDao();
        profesorDao = db.profesorDao();
        pistaDao = db.pistaDao();
        usuarioDao = db.usuarioDao();

        executorService = AppDatabase.getDatabaseExecutor();
    }

    // =========================================================
    // ESTADO
    // =========================================================

    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }

    // =========================================================
    // LECTURA
    // =========================================================

    public LiveData<List<Clase>> obtenerTodasClases() {
        return claseDao.obtenerTodasClases();
    }

    public LiveData<Clase> buscarPorId(int id) {
        return claseDao.buscarPorId(id);
    }

    public LiveData<List<Profesor>> obtenerTodosLosProfesores() {
        return profesorDao.obtenerTodosProfesores();
    }

    public LiveData<List<Pista>> obtenerTodasLasPistas() {
        return pistaDao.obtenerTodasPistas();
    }

    public LiveData<List<Usuario>> obtenerTodosLosUsuarios() {
        return usuarioDao.obtenerTodosUsuarios();
    }

    public LiveData<List<Clase>> getClasesPorProfesor(int idProfesor) {
        return claseDao.obtenerClasesPorProfesor(idProfesor);
    }

    // =========================================================
    // INSERT
    // =========================================================

    public void insertarClase(Clase clase) {

        executorService.execute(() -> {

            if (!claseDao.existePistaSync(clase.idPista)) {
                estadoOperacion.postValue("ERROR_PISTA_NO_EXISTE");
                return;
            }

            if (!claseDao.existeProfesorSync(clase.idProfesor)) {
                estadoOperacion.postValue("ERROR_PROFESOR_NO_EXISTE");
                return;
            }

            try {

                claseDao.insertarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public void actualizarClase(Clase clase) {

        executorService.execute(() -> {

            try {

                claseDao.actualizarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void eliminarClase(Clase clase) {

        executorService.execute(() -> {

            try {

                claseDao.eliminarClase(clase);

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {

                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    // =========================================================
    // FILTRAR DISPONIBILIDAD
    // =========================================================

    public void filtrarDisponibilidad(
            long inicio,
            long fin,
            String disciplina,
            String nivel,
            List<Usuario> usuarios,
            List<Profesor> profesores,
            List<Pista> pistas,
            List<Clase> clases,
            int claseEditando,
            MutableLiveData<List<Usuario>> profesoresDisponibles,
            MutableLiveData<List<Pista>> pistasDisponibles
    ) {

        executorService.execute(() -> {

            List<Usuario> profesoresOk = new ArrayList<>();

            for (Usuario usuario : usuarios) {

                Profesor detalle = buscarProfesor(
                        usuario.idUsuario,
                        profesores
                );

                if (detalle == null)
                    continue;

                boolean puedeDar =
                        ClaseValidator.profesorPuedeDarClase(
                                detalle,
                                disciplina,
                                nivel
                        );

                boolean disponible =
                        ClaseValidator.profesorDisponible(
                                usuario.idUsuario,
                                inicio,
                                fin,
                                clases,
                                claseEditando
                        );

                if (puedeDar && disponible) {
                    profesoresOk.add(usuario);
                }
            }

            List<Pista> pistasOk = new ArrayList<>();

            for (Pista pista : pistas) {

                boolean disponible =
                        ClaseValidator.pistaDisponible(
                                pista.idPista,
                                inicio,
                                fin,
                                clases,
                                claseEditando
                        );

                if (disponible) {
                    pistasOk.add(pista);
                }
            }

            profesoresDisponibles.postValue(profesoresOk);
            pistasDisponibles.postValue(pistasOk);
        });
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private Profesor buscarProfesor(
            int idProfesor,
            List<Profesor> profesores
    ) {

        for (Profesor profesor : profesores) {

            if (profesor.idProfesor == idProfesor) {
                return profesor;
            }
        }

        return null;
    }
}