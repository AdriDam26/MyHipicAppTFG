package com.example.myhipicapptfg.ui.ar.ar;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.repositorios.EquinoRepository;

import java.util.concurrent.Executors;


/**
 * ViewModel encargado de proporcionar los datos necesarios
 * para la funcionalidad de Realidad Aumentada.
 *
 * Su responsabilidad es:
 *
 * 1. Buscar un caballo a partir de su número de microchip.
 * 2. Obtener el nombre de su propietario.
 * 3. Exponer los datos mediante LiveData para que la
 *    ARActivity pueda observar los cambios automáticamente.
 *
 *
 */
public class AREquinoViewModel extends AndroidViewModel {

    /**
     * Repositorio encargado de acceder a los datos.
     */
    private final EquinoRepository repository;

    /**
     * Contiene el caballo obtenido desde la base de datos.
     *
     * La actividad observará este LiveData para mostrar
     * la información en la tarjeta AR.
     */
    private final MutableLiveData<Equino> equinoSeleccionado = new MutableLiveData<>();

    /**
     * Contiene el nombre del propietario del caballo.
     */
    private final MutableLiveData<String> nombrePropietario = new MutableLiveData<>();


    /**
     * Devuelve el caballo actualmente seleccionado.
     */
    public LiveData<Equino> getEquinoSeleccionado() {
        return equinoSeleccionado;
    }

    /**
     * Devuelve el nombre del propietario.
     */
    public LiveData<String> getNombrePropietario() {
        return nombrePropietario;
    }


    /**
     * Constructor del ViewModel.
     *
     * Inicializa el repositorio que se utilizará para
     * realizar las consultas a la base de datos.
     */
    public AREquinoViewModel(@NonNull Application application) {
        super(application);
        repository = new EquinoRepository(application);
    }


    /**
     * Busca un caballo a partir de su número de microchip.
     *
     * El proceso se ejecuta en un hilo secundario para evitar
     * bloquear la interfaz de usuario.
     *
     * Una vez obtenido el caballo:
     * - Se publica en equinoSeleccionado.
     * - Se busca el nombre de su propietario.
     * - Se publica en nombrePropietario.
     *
     * @param microchip Número de microchip leído desde el código QR.
     */
    public void cargarPorMicrochip(String microchip) {
        // Ejecuta la consulta en segundo plano
        Executors.newSingleThreadExecutor().execute(() -> {
            // Busca el caballo mediante su microchip
            Equino equino = repository.buscarPorMicrochipSync(microchip);
            // Actualiza el LiveData del caballo
            equinoSeleccionado.postValue(equino);

            // Si el caballo tiene propietario asignado
            if (equino != null && equino.idUsuario != null) {
                // Obtiene el nombre completo del propietario
                String nombre = repository.obtenerNombrePropietarioSync(equino.idUsuario);

                // Publica el nombre obtenido
                nombrePropietario.postValue(nombre != null ? nombre.trim() : "Hípica");
            } else {
                // Valor por defecto cuando no existe propietario
                nombrePropietario.postValue("Hípica");
            }
        });
    }
}