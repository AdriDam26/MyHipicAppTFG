package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.myhipicapptfg.entities.*;
import com.example.myhipicapptfg.repository.*;
import com.example.myhipicapptfg.util.SeleccionDisciplina;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepo;
    private final AlumnoRepository alumnoRepo;
    private final ProfesorRepository profesorRepo;
    private final PropietarioRepository propietarioRepo;
    private final AlumnoDisciplinaRepository aluDiscRepo;
    private final ProfesorDisciplinaRepository profDiscRepo; // Añadido

    private final ExecutorService executorService;
    private final MediatorLiveData<String> estadoFormulario = new MediatorLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        usuarioRepo = new UsuarioRepository(application);
        alumnoRepo = new AlumnoRepository(application);
        profesorRepo = new ProfesorRepository(application);
        propietarioRepo = new PropietarioRepository(application);
        aluDiscRepo = new AlumnoDisciplinaRepository(application);
        profDiscRepo = new ProfesorDisciplinaRepository(application); // Inicializado

        executorService = Executors.newSingleThreadExecutor();

        // Escuchamos posibles errores del repositorio de usuarios
        estadoFormulario.addSource(usuarioRepo.getErrorProgreso(), error -> {
            estadoFormulario.setValue(error);
        });
    }

    public LiveData<String> getEstadoFormulario() {
        return estadoFormulario;
    }

    // --- MÉTODOS DE REGISTRO SEGUROS (SÍNCRONOS DENTRO DEL EXECUTOR) ---

    public void registrarAlumno(Usuario usuario, List<SeleccionDisciplina> selecciones) {
        prepararRegistro();
        executorService.execute(() -> {
            // PASO 1: Insertar usuario y obtener ID
            long idGenerado = usuarioRepo.insertarUsuarioSync(usuario);

            if (idGenerado > 0) {
                // PASO 2: Insertar en la tabla Alumno
                Alumno alu = new Alumno();
                alu.idAlumno = (int) idGenerado;
                alumnoRepo.insertarAlumnoSync(alu);

                // PASO 3: Insertar las disciplinas del alumno
                for (SeleccionDisciplina sel : selecciones) {
                    AlumnoDisciplina ad = new AlumnoDisciplina();
                    ad.idAlumno = (int) idGenerado;
                    ad.idDisciplina = sel.id;
                    ad.nivel = sel.nivel;
                    aluDiscRepo.insertarSync(ad);
                }
                notificarExito("Alumno");
            } else {
                manejarErrorInsercion(idGenerado);
            }
        });
    }

    public void registrarProfesor(Usuario usuario, List<SeleccionDisciplina> selecciones) {
        prepararRegistro();
        executorService.execute(() -> {
            // PASO 1: Insertar usuario y obtener ID
            long idGenerado = usuarioRepo.insertarUsuarioSync(usuario);

            if (idGenerado > 0) {
                // PASO 2: Insertar en la tabla Profesor
                // Nota: Asegúrate de tener insertarProfesorSync en tu ProfesorRepository
                Profesor prof = new Profesor();
                prof.idProfesor = (int) idGenerado;
                profesorRepo.insertarProfesorSync(prof);

                // PASO 3: Insertar las disciplinas que enseña
                for (SeleccionDisciplina sel : selecciones) {
                    ProfesorDisciplina pd = new ProfesorDisciplina();
                    pd.idProfesor = (int) idGenerado;
                    pd.idDisciplina = sel.id;
                    profDiscRepo.insertarSync(pd);
                }
                notificarExito("Profesor");
            } else {
                manejarErrorInsercion(idGenerado);
            }
        });
    }

    public void registrarPropietario(Usuario usuario) {
        prepararRegistro();
        executorService.execute(() -> {
            // PASO 1: Insertar usuario y obtener ID
            long idGenerado = usuarioRepo.insertarUsuarioSync(usuario);

            if (idGenerado > 0) {
                // PASO 2: Insertar en la tabla Propietario
                Propietario prop = new Propietario();
                prop.idPropietario = (int) idGenerado;
                propietarioRepo.insertarPropietarioSync(prop);

                notificarExito("Propietario");
            } else {
                manejarErrorInsercion(idGenerado);
            }
        });
    }

    // --- UTILIDADES ---

    private void prepararRegistro() {
        estadoFormulario.postValue(null);
    }

    private void manejarErrorInsercion(long codigo) {
        if (codigo == -1) estadoFormulario.postValue("Error: El email ya existe");
        else if (codigo == -2) estadoFormulario.postValue("Error: El DNI ya existe");
        else estadoFormulario.postValue("Error al guardar en la base de datos");
    }

    private void notificarExito(String tipo) {
        estadoFormulario.postValue("Éxito: " + tipo + " registrado correctamente.");
    }
}