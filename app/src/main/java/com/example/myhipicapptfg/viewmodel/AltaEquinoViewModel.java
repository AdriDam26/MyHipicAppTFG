package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.repository.CuadraRepository;
import com.example.myhipicapptfg.repository.EquinoRepository;
import com.example.myhipicapptfg.repository.UsuarioRepository;

import java.util.List;

public class AltaEquinoViewModel extends AndroidViewModel {

    private final EquinoRepository equinoRepo;
    private final LiveData<List<Usuario>> propietarios;
    private final LiveData<List<Cuadra>> cuadras;
    private final MutableLiveData<Boolean> cargando = new MutableLiveData<>(false);

    public AltaEquinoViewModel(@NonNull Application application) {
        super(application);
        equinoRepo = new EquinoRepository(application);

        UsuarioRepository usuarioRepo = new UsuarioRepository(application);
        CuadraRepository cuadraRepo = new CuadraRepository(application);

        // Cargamos los datos para los desplegables
        propietarios = usuarioRepo.obtenerUsuariosPorTipo("Propietario");
        cuadras = cuadraRepo.getCuadrasLibres();
    }

    public LiveData<List<Usuario>> getPropietarios() { return propietarios; }
    public LiveData<List<Cuadra>> getCuadras() { return cuadras; }
    public LiveData<String> getMensajeEstado() { return equinoRepo.getMensajeStatus(); }
    public LiveData<Boolean> getCargando() { return cargando; }

    /**
     * Este método ahora recibe los datos sueltos de la Activity,
     * construye el objeto Equino y lo envía al repositorio.
     */
    public void registrarEquino(String nombre, String raza, String microchip, String fecha,
                                String sexo, String temp, String salud, String alturaStr,
                                String pesoStr, Integer idCuadra, Integer idPropietario) {

        cargando.setValue(true);

        // Creamos el objeto dentro del ViewModel (Lógica de negocio)
        Equino nuevoEquino = new Equino();
        nuevoEquino.nombre = nombre;
        nuevoEquino.raza = raza;
        nuevoEquino.numeroMicrochip = microchip;
        nuevoEquino.fechaNacimiento = fecha;

        // Mapeo de Sexo (M/H)
        nuevoEquino.sexo = sexo.equals("Macho") ? Equino.SEXO_MACHO : Equino.SEXO_HEMBRA;

        nuevoEquino.temperamento = temp;
        nuevoEquino.estadoSalud = salud;

        // Conversión segura de tipos numéricos
        try {
            nuevoEquino.altura = Double.parseDouble(alturaStr);
            nuevoEquino.peso = Double.parseDouble(pesoStr);
        } catch (NumberFormatException | NullPointerException e) {
            nuevoEquino.altura = 0.0;
            nuevoEquino.peso = 0.0;
        }

        nuevoEquino.idCuadra = idCuadra;
        nuevoEquino.idPropietario = idPropietario;

        // Enviamos al repositorio
        equinoRepo.insertar(nuevoEquino);
    }

    public void finalizarOperacion() {
        cargando.setValue(false);
    }
}