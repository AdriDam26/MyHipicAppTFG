package com.example.myhipicapptfg.datos.repositorios;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.dao.UsuarioDao;
import com.example.myhipicapptfg.datos.local.database.AppDatabase;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;

import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * Repositorio encargado de gestionar las operaciones relacionadas
 * con la entidad Usuario y sus posibles perfiles asociados
 * (Alumno, Profesor y Juez).
 *
 * Esta clase actúa como intermediaria entre la capa ViewModel
 * y la capa de acceso a datos (DAO), centralizando la lógica
 * de negocio, validaciones y ejecución de operaciones en segundo plano.
 */
public class UsuarioRepository {


    /**
     * Instancia de la base de datos Room.
     */
    private final AppDatabase db;

    /**
     * DAO utilizado para acceder a los datos de usuarios.
     */
    private final UsuarioDao usuarioDao;

    /**
     * Executor utilizado para ejecutar operaciones de base de datos
     * en segundo plano y evitar bloqueos del hilo principal.
     */
    private final ExecutorService executorService;

    /**
     * LiveData utilizado para comunicar el resultado de las operaciones
     * realizadas sobre la base de datos.
     *
     * Valores posibles:
     * - EXITO
     * - ERROR_EMAIL_DUPLICADO
     * - ERROR_DNI_DUPLICADO
     * - ERROR_BD
     */
    private final MutableLiveData<String> estadoOperacion = new MutableLiveData<>();


    /**
     * Constructor del repositorio.
     *
     * Inicializa la base de datos, el DAO y el ejecutor de tareas.
     *
     * @param application Contexto de la aplicación.
     */
    public UsuarioRepository(@NonNull Application application) {
        db = AppDatabase.getInstance(application);
        usuarioDao = db.usuarioDao();
        executorService = AppDatabase.getDatabaseExecutor();
    }


    /**
     * Devuelve el estado de la última operación realizada.
     *
     * @return LiveData con el resultado de la operación.
     */
    public LiveData<String> getEstadoOperacion() {
        return estadoOperacion;
    }


    /**
     * Inserta un usuario junto con los perfiles asociados
     * (Alumno, Profesor y/o Juez) dentro de una única transacción.
     *
     * De esta forma se garantiza la integridad de los datos:
     * si alguna operación falla, ninguna modificación se guarda.
     *
     * @param u Usuario principal.
     * @param a Perfil de alumno (opcional).
     * @param p Perfil de profesor (opcional).
     * @param j Perfil de juez (opcional).
     */
    public void insertarUsuarioCompleto(Usuario u, Alumno a, Profesor p, Juez j) {
        executorService.execute(() -> {

            if (usuarioDao.buscarPorEmailSync(u.email) != null) {
                estadoOperacion.postValue("ERROR_EMAIL_DUPLICADO");
                return;
            }
            if (usuarioDao.buscarPorDNISync(u.dni) != null) {
                estadoOperacion.postValue("ERROR_DNI_DUPLICADO");
                return;
            }

            try {
                db.runInTransaction(() -> {
                    long id = usuarioDao.insertarUsuario(u);

                    if (a != null) {
                        a.idAlumno    = (int) id; usuarioDao.insertarAlumno(a);
                    }
                    if (p != null) {
                        p.idProfesor  = (int) id; usuarioDao.insertarProfesor(p);
                    }
                    if (j != null) {
                        j.idJuez      = (int) id; usuarioDao.insertarJuez(j);
                    }
                });
                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }


    /**
     * Actualiza un usuario y sus perfiles asociados
     * dentro de una única transacción.
     *
     * @param u Usuario principal.
     * @param a Perfil de alumno.
     * @param p Perfil de profesor.
     * @param j Perfil de juez.
     */
    public void actualizarUsuarioCompleto(Usuario u, Alumno a, Profesor p, Juez j) {
        executorService.execute(() -> {
            try {
                db.runInTransaction(() -> {                                // ✅ usa el campo db
                    usuarioDao.actualizarUsuario(u);
                    if (a != null) {
                        usuarioDao.actualizarAlumno(a);
                    }
                    if (p != null) {
                        usuarioDao.actualizarProfesor(p);
                    }
                    if (j != null) {
                        usuarioDao.actualizarJuez(j);
                    }
                });

                estadoOperacion.postValue("EXITO");

            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Elimina un usuario de la base de datos.
     *
     * @param usuario Usuario a eliminar.
     */
    public void eliminarUsuario(Usuario usuario) {
        executorService.execute(() -> {
            try {
                usuarioDao.eliminarUsuario(usuario);
                estadoOperacion.postValue("EXITO");
            } catch (Exception e) {
                estadoOperacion.postValue("ERROR_BD");
            }
        });
    }

    /**
     * Obtiene todos los usuarios registrados.
     *
     * @return Lista observable de usuarios.
     */
    public LiveData<List<Usuario>> obtenerTodosUsuarios() {
        return usuarioDao.obtenerTodosUsuarios();
    }

    /**
     * Busca un usuario por su identificador.
     *
     * @param id Identificador del usuario.
     * @return Usuario encontrado.
     */
    public LiveData<Usuario> buscarPorId(int id) {
        return usuarioDao.buscarPorId(id);
    }


    /**
     * Obtiene todos los usuarios de un tipo concreto.
     *
     * @param tipo Tipo de usuario.
     * @return Lista observable de usuarios.
     */
    public LiveData<List<Usuario>> obtenerUsuariosPorTipo(String tipo) {
        return usuarioDao.obtenerUsuariosPorTipo(tipo);
    }

    /**
     * Obtiene los usuarios con perfil de alumno
     * pertenecientes a la disciplina de doma.
     *
     * @return Lista observable de alumnos.
     */
    public LiveData<List<Usuario>> obtenerAlumnosDoma() {
        return usuarioDao.obtenerAlumnosDoma();
    }


    /**
     * Realiza una búsqueda avanzada de usuarios aplicando
     * filtros por nombre y tipo de usuario.
     *
     * @param texto Texto de búsqueda.
     * @param alumno Incluir alumnos.
     * @param profesor Incluir profesores.
     * @param juez Incluir jueces.
     * @param propietario Incluir propietarios.
     * @return Lista observable de usuarios filtrados.
     */
    public LiveData<List<Usuario>> buscarUsuariosFiltrado(
            String texto,
            boolean alumno,
            boolean profesor,
            boolean juez,
            boolean propietario
    ) {
        return usuarioDao.buscarUsuariosFiltrado(
                texto,
                alumno,
                profesor,
                juez,
                propietario
        );
    }
}