package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.DisciplinaEquino;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.EquinoRepository;
import com.example.myhipicapptfg.repository.UsuarioRepository;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltaEquinoViewModel extends AndroidViewModel {

    private final EquinoRepository equinoRepo;
    private final DisciplinaEquinoRepository discEquinoRepo;
    private final LiveData<List<Usuario>> propietarios;
    private final LiveData<List<Cuadra>> cuadras;

    private final MutableLiveData<String> mensajeEstado = new MutableLiveData<>();
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);
    private final ExecutorService executorService;

    public AltaEquinoViewModel(@NonNull Application application) {
        super(application);
        equinoRepo = new EquinoRepository(application);
        discEquinoRepo = new DisciplinaEquinoRepository(application);

        UsuarioRepository usuarioRepo = new UsuarioRepository(application);
        CuadraRepository cuadraRepo = new CuadraRepository(application);

        // Cargamos los datos para los desplegables (Solo cuadras libres)
        propietarios = usuarioRepo.obtenerUsuariosPorTipo("Propietario");
        cuadras = cuadraRepo.getCuadrasLibres();

        executorService = Executors.newSingleThreadExecutor();
    }

    // Getters para la UI
    public LiveData<List<Usuario>> getPropietarios() { return propietarios; }
    public LiveData<List<Cuadra>> getCuadras() { return cuadras; }
    public LiveData<String> getMensajeEstado() { return mensajeEstado; }
    public LiveData<Boolean> getCargando() { return cargando; }

    /**
     * Registra un equino y sus disciplinas asociadas de forma atómica.
     */
    public void registrarEquino(String nombre, String raza, String microchip, String fecha,
                                String sexo, String temp, String salud, String alturaStr,
                                String pesoStr, Integer idCuadra, Integer idPropietario,
                                List<Integer> idsDisciplinas) {

        cargando.setValue(true);

        executorService.execute(() -> {
            // 1. Crear el objeto Equino
            Equino nuevoEquino = new Equino();
            nuevoEquino.nombre = nombre;
            nuevoEquino.raza = raza;
            nuevoEquino.numeroMicrochip = microchip;
            nuevoEquino.fechaNacimiento = fecha;

            // Mapeo de Sexo
            nuevoEquino.sexo = sexo.equalsIgnoreCase("Macho") ? "M" : "F";

            nuevoEquino.temperamento = temp;
            nuevoEquino.estadoSalud = salud;

            // Conversión segura de números
            try {
                nuevoEquino.altura = Double.parseDouble(alturaStr.replace(",", "."));
                nuevoEquino.peso = Double.parseDouble(pesoStr.replace(",", "."));
            } catch (Exception e) {
                nuevoEquino.altura = 0.0;
                nuevoEquino.peso = 0.0;
            }

            nuevoEquino.idCuadra = idCuadra;
            nuevoEquino.idPropietario = idPropietario;

            // 2. Insertar Equino y obtener ID generado (Usa un método Sync en el repo)
            long idGenerado = equinoRepo.insertarSync(nuevoEquino);

            if (idGenerado > 0) {
                // 3. Si tiene especialidades, las insertamos
                if (idsDisciplinas != null && !idsDisciplinas.isEmpty()) {
                    for (Integer idDisc : idsDisciplinas) {
                        DisciplinaEquino de = new DisciplinaEquino();
                        de.idEquino = (int) idGenerado;
                        de.idDisciplina = idDisc;
                        discEquinoRepo.insertarSync(de); // Método Sync en el repo
                    }
                }
                // Notificar éxito (null suele significar sin errores en tu lógica)
                mensajeEstado.postValue(null);
            } else {
                // Error (probablemente microchip duplicado)
                mensajeEstado.postValue("Error: El microchip ya existe en el sistema.");
            }

            cargando.postValue(false);
        });
    }

    public void finalizarOperacion() {
        cargando.setValue(false);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}