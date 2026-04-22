package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Disciplina;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.ClaseRepository;
import com.example.myhipicapptfg.repository.PistaRepository;
import com.example.myhipicapptfg.repository.ProfesorRepository; // CAMBIADO

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltaClaseViewModel extends AndroidViewModel {

    private final ClaseRepository claseRepo;
    private final PistaRepository pistaRepo;
    private final ProfesorRepository profesorRepo; // CAMBIADO de UsuarioRepository a ProfesorRepository
    private final DisciplinaRepository disciplinaRepo;

    private final ExecutorService executorService;

    // LiveDatas para la UI
    private final LiveData<List<Disciplina>> disciplinas;
    private final MutableLiveData<List<Pista>> pistasLibres = new MutableLiveData<>();
    private final MutableLiveData<List<Usuario>> profesoresLibres = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public AltaClaseViewModel(@NonNull Application application) {
        super(application);
        executorService = Executors.newFixedThreadPool(2); // Un poco más de hilos para fluidez

        claseRepo = new ClaseRepository(application);
        pistaRepo = new PistaRepository(application);
        disciplinaRepo = new DisciplinaRepository(application);
        profesorRepo = new ProfesorRepository(application); // Inicializado

        // Las disciplinas son estáticas (no dependen de la hora), se cargan al inicio
        disciplinas = disciplinaRepo.obtenerTodasDisciplinas();
    }

    // Getters
    public LiveData<List<Pista>> getPistasLibres() { return pistasLibres; }
    public LiveData<List<Usuario>> getProfesoresLibres() { return profesoresLibres; }
    public LiveData<List<Disciplina>> getDisciplinas() { return disciplinas; }
    public LiveData<String> getErrorLiveData() { return claseRepo.getErrorLiveData(); }
    public LiveData<Boolean> getCargando() { return cargando; }

    /**
     * Lógica central: Busca disponibilidad en tiempo real según la fecha y horas elegidas.
     */
    public void actualizarDisponibilidad(String fecha, String hIni, String hFin) {
        executorService.execute(() -> {
            // Usamos los métodos Sync de los repositorios
            List<Pista> pLibres = pistaRepo.obtenerPistasLibresSync(fecha, hIni, hFin);

            // Ahora profesorRepo devuelve la lista de Usuarios (con nombres) que están libres
            List<Usuario> profLibres = profesorRepo.obtenerProfesoresLibresSync(fecha, hIni, hFin);

            pistasLibres.postValue(pLibres);
            profesoresLibres.postValue(profLibres);
        });
    }

    public void registrarClase(String fecha, String horaIni, String horaFin,
                               String nivel, int idPista, int idDisc, int idProf) {
        cargando.setValue(true);

        Clase nuevaClase = new Clase();
        nuevaClase.fecha = fecha;
        nuevaClase.horaInicio = horaIni;
        nuevaClase.horaFin = horaFin;
        nuevaClase.nivelRecomendado = nivel;
        nuevaClase.idPista = idPista;
        nuevaClase.idDisciplina = idDisc;
        nuevaClase.idProfesor = idProf; // Este es el idUsuario que viene del Spinner

        claseRepo.insertarClase(nuevaClase);
    }

    public void finalizarCarga() {
        cargando.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (!executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}