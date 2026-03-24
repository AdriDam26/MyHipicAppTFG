package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import com.example.myhipicapptfg.entities.*;
import com.example.myhipicapptfg.repository.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AdminViewModel extends AndroidViewModel {

    private final UsuarioRepository usuarioRepo;
    private final AlumnoRepository alumnoRepo;
    private final ProfesorRepository profesorRepo;
    private final PropietarioRepository propietarioRepo;
    private final ExecutorService executorService;
    private final MediatorLiveData<String> estadoFormulario = new MediatorLiveData<>();

    public AdminViewModel(@NonNull Application application) {
        super(application);
        usuarioRepo = new UsuarioRepository(application);
        alumnoRepo = new AlumnoRepository(application);
        profesorRepo = new ProfesorRepository(application);
        propietarioRepo = new PropietarioRepository(application);
        executorService = Executors.newSingleThreadExecutor();

        // IMPORTANTE: Escuchar al repositorio y pasar el valor al estado del formulario
        estadoFormulario.addSource(usuarioRepo.getErrorProgreso(), error -> {
            // Pasamos lo que diga el repositorio (sea error o sea null/éxito)
            estadoFormulario.setValue(error);
        });
    }

    public LiveData<String> getEstadoFormulario() {
        return estadoFormulario;
    }

    public void registrarUsuarioCompleto(Usuario usuario) {
        // 1. LIMPIAMOS el estado antes de empezar para que no arrastre errores antiguos
        estadoFormulario.postValue(null);

        executorService.execute(() -> {
            // 2. Intentamos insertar el usuario base
            usuarioRepo.insertarUsuario(usuario);

            // 3. Esperamos a que el repositorio termine sus validaciones (DNI/Email)
            try { Thread.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }

            // 4. Obtenemos el estado actual DESPUÉS de la inserción
            String estadoActual = estadoFormulario.getValue();

            // 5. Solo procedemos si el repositorio NO ha posteado un error
            // (Si el repositorio fue bien, el estado debería ser null o EXITOSO)
            if (estadoActual == null || !estadoActual.startsWith("Error")) {

                Usuario uCreado = usuarioRepo.buscarPorDNISync(usuario.dni);

                if (uCreado != null) {
                    int id = uCreado.idUsuario;
                    // Usamos las constantes de la clase Usuario para evitar errores de escritura
                    switch (uCreado.tipo) {
                        case Usuario.TIPO_ALUMNO:
                            Alumno a = new Alumno();
                            a.idAlumno = id;
                            alumnoRepo.insertarAlumno(a);
                            break;
                        case Usuario.TIPO_PROFESOR:
                            Profesor p = new Profesor();
                            p.idProfesor = id;
                            profesorRepo.insertarProfesor(p);
                            break;
                        case Usuario.TIPO_PROPIETARIO:
                            Propietario prop = new Propietario();
                            prop.idPropietario = id;
                            propietarioRepo.insertarPropietario(prop);
                            break;
                    }
                    // 6. Notificamos ÉXITO FINAL a la Activity
                    estadoFormulario.postValue("Éxito: " + uCreado.tipo + " registrado correctamente.");
                } else {
                    estadoFormulario.postValue("Error: No se pudo recuperar el ID del usuario.");
                }
            }
            // Si había un error (DNI/Email), no hacemos nada más,
            // la Activity ya habrá recibido el "Error: ..." a través del Mediator.
        });
    }
}