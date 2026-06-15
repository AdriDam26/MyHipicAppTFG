package com.example.myhipicapptfg.ui.competiciones.juez.participantes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myhipicapptfg.model.ParticipacionDetalle;
import com.example.myhipicapptfg.datos.repositorios.JuezRepository;
import com.example.myhipicapptfg.datos.repositorios.PruebaRepository;

import java.util.List;

/**
 * ViewModel encargado de gestionar los datos de los participantes
 * de una prueba y el estado de publicación de sus resultados.
 *
 * Actúa como intermediario entre la interfaz de usuario y los repositorios,
 * manteniendo los datos durante cambios de configuración y proporcionando
 * información observable mediante LiveData.
 */
public class ParticipantesViewModel extends AndroidViewModel {

    // Repositorio encargado de las operaciones relacionadas con jueces y participaciones
    private final JuezRepository   juezRepo;

    // Repositorio encargado de las operaciones relacionadas con las pruebas
    private final PruebaRepository pruebaRepo;

    // Lista observable de participantes de una prueba
    private LiveData<List<ParticipacionDetalle>> participantes;

    // Estado observable que indica si la prueba está publicada
    private LiveData<Boolean>                    publicado;
    // Identificador de la prueba actualmente cargada
    private int idPrueba;

    /**
     * Constructor del ViewModel.
     *
     * Inicializa los repositorios necesarios para acceder a los datos.
     *
     * @param application Contexto de la aplicación.
     */
    public ParticipantesViewModel(@NonNull Application application) {
        super(application);
        juezRepo   = new JuezRepository(application);
        pruebaRepo = new PruebaRepository(application);
    }

    /**
     * Obtiene la lista de participantes de una prueba determinada.
     *
     * Los datos se cargan únicamente la primera vez que se solicita la información,
     * reutilizando posteriormente el mismo objeto LiveData para evitar consultas
     * innecesarias a la base de datos.
     *
     * Además, se obtiene el estado de publicación asociado a la prueba.
     *
     * @param idPrueba Identificador de la prueba.
     * @return LiveData con la lista de participantes.
     */
    public LiveData<List<ParticipacionDetalle>> getParticipantes(int idPrueba) {
        if (participantes == null) {
            this.idPrueba  = idPrueba;
            participantes  = juezRepo.getParticipantesByPrueba(idPrueba);
            publicado      = pruebaRepo.isPublicado(idPrueba);
        }
        return participantes;
    }

    /**
     * Devuelve el estado de publicación de la prueba.
     *
     * @return LiveData que contiene:
     *         true si la prueba está publicada.
     *         false si la prueba no está publicada.
     */
    public LiveData<Boolean> isPublicado() {
        return publicado;
    }

    /**
     * Actualiza el estado de publicación de la prueba actual.
     *
     * Este método permite publicar o despublicar los resultados
     * de una prueba.
     *
     * @param nuevoEstado Nuevo estado de publicación.
     *                     true = publicada.
     *                     false = no publicada.
     */
    public void actualizarPublicado(boolean nuevoEstado) {
        pruebaRepo.actualizarPublicado(idPrueba, nuevoEstado);
    }
}