package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.*;
import com.example.myhipicapptfg.repository.*;
import com.example.myhipicapptfg.util.SeleccionDisciplina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltaUsuarioViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepo;
    private final AlumnoRepository alumnoRepo;
    private final ProfesorRepository profesorRepo;
    private final PropietarioRepository propietarioRepo;
    private final AlumnoDisciplinaRepository aluDiscRepo;
    private final ProfesorDisciplinaRepository profDiscRepo;

    private final ExecutorService executorService;
    private final MutableLiveData<String> mensajeEstado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>();

    public AltaUsuarioViewModel(@NonNull Application application) {
        super(application);
        usuarioRepo = new UsuarioRepository(application);
        alumnoRepo = new AlumnoRepository(application);
        profesorRepo = new ProfesorRepository(application);
        propietarioRepo = new PropietarioRepository(application);
        aluDiscRepo = new AlumnoDisciplinaRepository(application);
        profDiscRepo = new ProfesorDisciplinaRepository(application);
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<String> getMensajeEstado() { return mensajeEstado; }
    public LiveData<Boolean> getCargando() { return cargando; }

    /**
     * MÉTODO PARA ACTUALIZAR USUARIO EXISTENTE
     */
    public void actualizarUsuario(int idUsuario, String nombre, String ap1, String ap2, String dni,
                                  String email, String sexo, String tipo,
                                  List<SeleccionDisciplina> selecciones) {

        // 1. Validaciones de negocio (Reutilizadas)
        if (!tipo.equals("Propietario")) {
            if (selecciones == null || selecciones.isEmpty()) {
                mensajeEstado.setValue("Error: Selecciona al menos una disciplina");
                return;
            }
            if (tipo.equals("Alumno")) {
                for (SeleccionDisciplina s : selecciones) {
                    if (s.nivel == null || s.nivel.isEmpty() || s.nivel.equalsIgnoreCase("Seleccionar nivel")) {
                        mensajeEstado.setValue("Error: Indica el nivel para las disciplinas seleccionadas");
                        return;
                    }
                }
            }
        }

        // 2. Preparar objeto con el ID original
        Usuario u = new Usuario();
        u.idUsuario = idUsuario;
        u.nombre = nombre;
        u.apellido1 = ap1;
        u.apellido2 = ap2;
        u.dni = dni.toUpperCase();
        u.email = email;
        u.sexo = sexo.equals("Masculino") ? "M" : "F";
        u.tipo = tipo;

        cargando.setValue(true);
        mensajeEstado.setValue(null);

        // 3. Proceso asíncrono
        executorService.execute(() -> {
            // Actualizamos el usuario base
            int resultado = usuarioRepo.actualizarUsuarioSync(u);

            if (resultado >= 0) {
                // Si el usuario se actualizó, refrescamos sus disciplinas según el tipo
                switch (tipo) {
                    case "Alumno":
                        procesarActualizacionAlumno(idUsuario, selecciones);
                        break;
                    case "Profesor":
                        procesarActualizacionProfesor(idUsuario, selecciones);
                        break;
                    case "Propietario":
                        finalizarExitoActualizacion("Propietario");
                        break;
                }
            } else {
                cargando.postValue(false);
                manejarErrorBaseDatos(resultado);
            }
        });
    }

    private void procesarActualizacionAlumno(int id, List<SeleccionDisciplina> selecciones) {
        // Borramos las disciplinas viejas e insertamos las nuevas (Limpieza total)
        aluDiscRepo.eliminarDisciplinasPorAlumnoSync(id);
        for (SeleccionDisciplina sel : selecciones) {
            AlumnoDisciplina ad = new AlumnoDisciplina();
            ad.idAlumno = id;
            ad.idDisciplina = sel.id;
            ad.nivel = sel.nivel;
            aluDiscRepo.insertarSync(ad);
        }
        finalizarExitoActualizacion("Alumno");
    }

    private void procesarActualizacionProfesor(int id, List<SeleccionDisciplina> selecciones) {
        // Borramos las disciplinas viejas e insertamos las nuevas
        profDiscRepo.eliminarDisciplinasPorProfesorSync(id);
        for (SeleccionDisciplina sel : selecciones) {
            ProfesorDisciplina pd = new ProfesorDisciplina();
            pd.idProfesor = id;
            pd.idDisciplina = sel.id;
            profDiscRepo.insertarSync(pd);
        }
        finalizarExitoActualizacion("Profesor");
    }

    private void finalizarExitoActualizacion(String tipo) {
        cargando.postValue(false);
        mensajeEstado.postValue("Éxito: " + tipo + " actualizado correctamente");
    }

    // --- MÉTODOS DE REGISTRO (EXISTENTES) ---

    public void registrarNuevoUsuario(String nombre, String ap1, String ap2, String dni,
                                      String email, String sexo, String tipo,
                                      List<SeleccionDisciplina> selecciones) {

        if (!tipo.equals("Propietario")) {
            if (selecciones == null || selecciones.isEmpty()) {
                mensajeEstado.setValue("Error: Selecciona al menos una disciplina");
                return;
            }
            if (tipo.equals("Alumno")) {
                for (SeleccionDisciplina s : selecciones) {
                    if (s.nivel == null || s.nivel.isEmpty() || s.nivel.equalsIgnoreCase("Seleccionar nivel")) {
                        mensajeEstado.setValue("Error: Indica el nivel para las disciplinas seleccionadas");
                        return;
                    }
                }
            }
        }

        Usuario u = new Usuario();
        u.nombre = nombre;
        u.apellido1 = ap1;
        u.apellido2 = ap2;
        u.dni = dni.toUpperCase();
        u.email = email;
        u.sexo = sexo.equals("Masculino") ? "M" : "F";
        u.tipo = tipo;

        cargando.setValue(true);
        mensajeEstado.setValue(null);

        executorService.execute(() -> {
            long idGenerado = usuarioRepo.insertarUsuarioSync(u);
            if (idGenerado > 0) {
                switch (tipo) {
                    case "Alumno": procesarRegistroAlumno(idGenerado, selecciones); break;
                    case "Profesor": procesarRegistroProfesor(idGenerado, selecciones); break;
                    case "Propietario": procesarRegistroPropietario(idGenerado); break;
                }
            } else {
                cargando.postValue(false);
                manejarErrorBaseDatos(idGenerado);
            }
        });
    }

    private void manejarErrorBaseDatos(long codigo) {
        if (codigo == -1) {
            mensajeEstado.postValue("Error: El email ya está registrado");
        } else if (codigo == -2) {
            mensajeEstado.postValue("Error: El DNI ya está registrado");
        } else {
            mensajeEstado.postValue("Error crítico al guardar en la base de datos");
        }
    }

    private void procesarRegistroAlumno(long id, List<SeleccionDisciplina> selecciones) {
        Alumno alu = new Alumno();
        alu.idAlumno = (int) id;
        alumnoRepo.insertarAlumnoSync(alu);
        for (SeleccionDisciplina sel : selecciones) {
            AlumnoDisciplina ad = new AlumnoDisciplina();
            ad.idAlumno = (int) id;
            ad.idDisciplina = sel.id;
            ad.nivel = sel.nivel;
            aluDiscRepo.insertarSync(ad);
        }
        finalizarExito("Alumno");
    }

    private void procesarRegistroProfesor(long id, List<SeleccionDisciplina> selecciones) {
        Profesor prof = new Profesor();
        prof.idProfesor = (int) id;
        profesorRepo.insertarProfesorSync(prof);
        for (SeleccionDisciplina sel : selecciones) {
            ProfesorDisciplina pd = new ProfesorDisciplina();
            pd.idProfesor = (int) id;
            pd.idDisciplina = sel.id;
            profDiscRepo.insertarSync(pd);
        }
        finalizarExito("Profesor");
    }

    private void procesarRegistroPropietario(long id) {
        Propietario prop = new Propietario();
        prop.idPropietario = (int) id;
        propietarioRepo.insertarPropietarioSync(prop);
        finalizarExito("Propietario");
    }

    private void finalizarExito(String tipo) {
        cargando.postValue(false);
        mensajeEstado.postValue("Éxito: " + tipo + " registrado correctamente");
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}