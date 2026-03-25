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

    private final ExecutorService executorService;
    private final MediatorLiveData<String> estadoFormulario = new MediatorLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        usuarioRepo = new UsuarioRepository(application);
        alumnoRepo = new AlumnoRepository(application);
        profesorRepo = new ProfesorRepository(application);
        propietarioRepo = new PropietarioRepository(application);
        aluDiscRepo = new AlumnoDisciplinaRepository(application);

        executorService = Executors.newSingleThreadExecutor();

        estadoFormulario.addSource(usuarioRepo.getErrorProgreso(), error -> {
            estadoFormulario.setValue(error);
        });
    }

    public LiveData<String> getEstadoFormulario() {
        return estadoFormulario;
    }

    // --- MÉTODOS DE REGISTRO ---

    public void registrarAlumno(Usuario usuario, List<SeleccionDisciplina> selecciones) {
        prepararRegistro();

        executorService.execute(() -> {
            // PASO 1: Insertar usuario y obtener ID (BLOQUEANTE)
            long idGenerado = usuarioRepo.insertarUsuarioSync(usuario);

            if (idGenerado > 0) {
                // PASO 2: El usuario ya existe en la DB. Ahora creamos el Alumno.
                Alumno alu = new Alumno();
                alu.idAlumno = (int) idGenerado;
                alumnoRepo.insertarAlumnoSync(alu); // Crea este método sync en AlumnoRepo

                // PASO 3: Insertar las disciplinas
                for (SeleccionDisciplina sel : selecciones) {
                    AlumnoDisciplina ad = new AlumnoDisciplina();
                    ad.idAlumno = (int) idGenerado;
                    ad.idDisciplina = sel.id;
                    ad.nivel = sel.nivel;
                    aluDiscRepo.insertarSync(ad); // Crea este método sync en AluDiscRepo
                }

                notificarExito("Alumno");
            } else {
                // Manejo de errores según el código devuelto (-1, -2, -3)
                if (idGenerado == -1) estadoFormulario.postValue("Error: Email duplicado");
                else if (idGenerado == -2) estadoFormulario.postValue("Error: DNI duplicado");
                else estadoFormulario.postValue("Error al guardar el usuario");
            }
        });
    }

    public void registrarProfesor(Usuario usuario) {
        prepararRegistro();
        executorService.execute(() -> {
            if (insertarUsuarioBase(usuario)) {
                Usuario uCreado = usuarioRepo.buscarPorDNISync(usuario.dni);
                if (uCreado != null) {
                    profesorRepo.insertarProfesor(new Profesor(uCreado.idUsuario));
                    notificarExito("Profesor");
                }
            }
        });
    }

    public void registrarPropietario(Usuario usuario) {
        prepararRegistro();
        executorService.execute(() -> {
            if (insertarUsuarioBase(usuario)) {
                Usuario uCreado = usuarioRepo.buscarPorDNISync(usuario.dni);
                if (uCreado != null) {
                    propietarioRepo.insertarPropietario(new Propietario(uCreado.idUsuario));
                    notificarExito("Propietario");
                }
            }
        });
    }

    // --- LÓGICA DE VALIDACIÓN Y APOYO ---

    private void prepararRegistro() {
        estadoFormulario.postValue(null);
    }

    private boolean insertarUsuarioBase(Usuario usuario) {
        // 1. VALIDACIÓN DNI: Comprobar si ya existe
        Usuario porDni = usuarioRepo.buscarPorDNISync(usuario.dni);
        if (porDni != null) {
            estadoFormulario.postValue("Error: El DNI ya está registrado");
            return false;
        }

        // 2. VALIDACIÓN EMAIL: Comprobar si ya existe
        // Asegúrate de tener el método buscarPorEmailSync en tu UsuarioRepository
        Usuario porEmail = usuarioRepo.buscarPorEmailSync(usuario.email);
        if (porEmail != null) {
            estadoFormulario.postValue("Error: El correo electrónico ya está registrado");
            return false;
        }

        // 3. INSERCIÓN: Si llegamos aquí, los datos son únicos
        usuarioRepo.insertarUsuario(usuario);
        esperar(500);

        String error = estadoFormulario.getValue();
        return (error == null || !error.startsWith("Error"));
    }

    private void notificarExito(String tipo) {
        estadoFormulario.postValue("Éxito: " + tipo + " registrado correctamente.");
    }

    private void esperar(int ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { e.printStackTrace(); }
    }
}