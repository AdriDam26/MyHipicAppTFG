package com.example.myhipicapptfg.ui.gps.seguir;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.repositorios.CoordenadaRutaRepository;

import java.util.ArrayList;
import java.util.List;


/**
 * ViewModel encargado de gestionar la lógica de seguimiento
 * de una ruta previamente grabada.
 *
 * Sus principales responsabilidades son:
 *
 * - Cargar las coordenadas asociadas a una ruta.
 * - Determinar el punto objetivo que debe alcanzar el usuario.
 * - Calcular la distancia respecto al recorrido.
 * - Detectar desviaciones de la ruta.
 * - Calcular el porcentaje completado.
 * - Detectar la finalización del recorrido.
 */
public class SeguirRutaViewModel extends AndroidViewModel {

    /**
     * Repositorio encargado de obtener las coordenadas
     * almacenadas para una ruta.
     */
    private final CoordenadaRutaRepository coordRepo;

    /**
     * Porcentaje de progreso completado sobre la ruta.
     */
    private final MutableLiveData<Integer> progreso = new MutableLiveData<>(0);

    /**
     * Distancia actual entre la posición del usuario
     * y el punto objetivo de la ruta.
     */
    private final MutableLiveData<Double> distanciaAlTrazado = new MutableLiveData<>(0.0);

    /**
     * Indica si la ruta ha sido completada.
     */
    private final MutableLiveData<Boolean> rutaCompletada = new MutableLiveData<>(false);

    /**
     * Indica si el usuario se ha alejado excesivamente
     * del recorrido previsto.
     */
    private final MutableLiveData<Boolean> fueraDeRuta = new MutableLiveData<>(false);

    /**
     * Índice del punto objetivo actual dentro de la ruta.
     */
    private final MutableLiveData<Integer> indiceCercano = new MutableLiveData<>(0);

    /**
     * Lista de coordenadas que forman el recorrido.
     */
    private List<CoordenadaRuta> puntos = new ArrayList<>();

    /**
     * Índice del punto que debe alcanzarse actualmente.
     */
    private int indiceActual = 0;

    /**
     * Sentido de navegación de la ruta.
     *
     * 1  -> recorrido desde el inicio.
     * -1 -> recorrido desde el final.
     */
    private int direccion = 1; // 1 = normal, -1 = inversa

    /**
     * Indica si la dirección de recorrido ya ha sido determinada.
     */
    private boolean direccionDefinida = false;

    /**
     * Instante temporal del último avance entre puntos.
     *
     * Se utiliza para evitar cambios múltiples provocados
     * por fluctuaciones del GPS.
     */
    private long ultimoCambio = 0;

    /**
     * Distancia máxima para considerar que un punto
     * de la ruta ha sido alcanzado.
     */
    private static final float RADIO_LLEGADA = 20f; // metros

    /**
     * Distancia máxima permitida antes de considerar
     * que el usuario se ha salido del recorrido.
     */
    private static final float RADIO_FUERA_RUTA = 40f; // metros

    /**
     * Tiempo mínimo que debe transcurrir entre dos
     * avances consecutivos de puntos.
     */
    private static final long TIEMPO_MINIMO_ENTRE_PUNTOS = 2000; // 2 segundos

    /**
     * Constructor del ViewModel.
     *
     * Inicializa el repositorio utilizado para acceder
     * a las coordenadas almacenadas.
     */
    public SeguirRutaViewModel(@NonNull Application app) {
        super(app);
        coordRepo = new CoordenadaRutaRepository(app);
    }


    /**
     * Devuelve el porcentaje completado de la ruta.
     */
    public LiveData<Integer> getProgreso() { return progreso; }
    /**
     * Devuelve la distancia actual al punto objetivo.
     */
    public LiveData<Double> getDistanciaAlPunto() { return distanciaAlTrazado; }
    /**
     * Devuelve el estado de finalización de la ruta.
     */
    public LiveData<Boolean> getRutaCompletada() { return rutaCompletada; }
    /**
     * Devuelve si el usuario se encuentra fuera del recorrido.
     */
    public LiveData<Boolean> getFueraDeRuta() { return fueraDeRuta; }
    /**
     * Devuelve el índice del punto objetivo actual.
     */
    public LiveData<Integer> getIndiceCercano() { return indiceCercano; }

    /**
     * Obtiene todas las coordenadas asociadas a una ruta.
     *
     * @param idRuta identificador de la ruta.
     * @return lista observable de coordenadas.
     */
    public LiveData<List<CoordenadaRuta>> cargarPuntos(int idRuta) {
        return coordRepo.obtenerCoordenadasPorRuta(idRuta);
    }


    /**
     * Inicializa una nueva sesión de seguimiento.
     *
     * Se cargan los puntos de la ruta y se reinician
     * todas las variables de estado.
     *
     * @param lista coordenadas de la ruta.
     */
    public void setPuntos(List<CoordenadaRuta> lista) {
        puntos = lista;

        progreso.setValue(0);
        rutaCompletada.setValue(false);
        fueraDeRuta.setValue(false);

        indiceActual = 0;
        direccion = 1;
        direccionDefinida = false;
        ultimoCambio = 0;
    }


    /**
     * Procesa una nueva ubicación GPS del usuario.
     *
     * Funciones realizadas:
     *
     * - Determinar el sentido de recorrido.
     * - Calcular la distancia al punto objetivo.
     * - Detectar desviaciones del recorrido.
     * - Avanzar al siguiente punto cuando se alcanza.
     * - Actualizar el porcentaje completado.
     * - Detectar la finalización de la ruta.
     *
     * @param lat latitud actual.
     * @param lng longitud actual.
     */
    public void actualizarUbicacion(double lat, double lng) {

        // Verificar que la ruta sea válida
        if (puntos == null || puntos.size() < 2) {
            return;
        }
        // No continuar si la ruta ya ha finalizado
        if (Boolean.TRUE.equals(rutaCompletada.getValue())) {
            return;
        }

        float[] resultado = new float[1];

        // Determinar automáticamente el sentido de recorrido
        // según la cercanía al inicio o al final de la ruta.
        if (!direccionDefinida) {

            float[] rInicio = new float[1];
            float[] rFinal = new float[1];

            CoordenadaRuta inicio = puntos.get(0);
            CoordenadaRuta fin = puntos.get(puntos.size() - 1);
            // Mide distancia al primer punto
            Location.distanceBetween(lat, lng, inicio.latitud, inicio.longitud, rInicio);
            // Mide distancia al último punto
            Location.distanceBetween(lat, lng, fin.latitud, fin.longitud, rFinal);

            if (rInicio[0] <= rFinal[0]) {
                direccion = 1;
                indiceActual = 0;
            } else {
                direccion = -1;
                indiceActual = puntos.size() - 1;
            }

            direccionDefinida = true;
            indiceCercano.postValue(indiceActual);
        }

        // Obtener el punto objetivo actual
        CoordenadaRuta objetivo = puntos.get(indiceActual);

        // Calcular distancia entre usuario y punto objetivo
        Location.distanceBetween(
                lat, lng,
                objetivo.latitud,
                objetivo.longitud,
                resultado
        );

        float distancia = resultado[0];
        // Actualizar distancia mostrada
        distanciaAlTrazado.postValue((double) distancia);
        // Detectar desviación de la ruta
        fueraDeRuta.postValue(distancia > RADIO_FUERA_RUTA);


        long ahora = System.currentTimeMillis();

        // Control antirrebote para evitar saltos erróneos
        if (distancia < RADIO_LLEGADA &&
                ahora - ultimoCambio > TIEMPO_MINIMO_ENTRE_PUNTOS) {

            ultimoCambio = ahora;
            // Avanzar al siguiente punto
            indiceActual += direccion;

            boolean terminado =
                    (direccion == 1 && indiceActual >= puntos.size()) ||
                            (direccion == -1 && indiceActual < 0);

            // Comprobar finalización de la ruta
            if (terminado) {
                rutaCompletada.postValue(true);
                progreso.postValue(100);
                return;
            }

            indiceCercano.postValue(indiceActual);

            int puntosRecorridos;

            if (direccion == 1) {
                puntosRecorridos = indiceActual;
            } else {
                puntosRecorridos = puntos.size() - 1 - indiceActual;
            }
            // Calcular porcentaje completado
            int pct = (int) ((puntosRecorridos / (float) puntos.size()) * 100);
            progreso.postValue(pct);
        }
    }
}