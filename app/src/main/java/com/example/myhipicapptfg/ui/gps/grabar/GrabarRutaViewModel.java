package com.example.myhipicapptfg.ui.gps.grabar;

import android.app.Application;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.example.myhipicapptfg.datos.repositorios.RutaPersonalRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ViewModel encargado de gestionar la grabación de rutas GPS.
 *
 * Su responsabilidad es:
 *
 * - Controlar el estado de la grabación.
 * - Calcular la distancia recorrida.
 * - Gestionar el cronómetro de la ruta.
 * - Almacenar temporalmente las coordenadas GPS.
 * - Guardar la ruta completa en la base de datos.
 *
 */


public class GrabarRutaViewModel extends AndroidViewModel {

    /**
     * Repositorio encargado del almacenamiento de rutas GPS.
     */
    private final RutaPersonalRepository rutaRepo;

    /**
     * Indica si actualmente se está grabando una ruta.
     */
    private final MutableLiveData<Boolean> grabando = new MutableLiveData<>(false);
    /**
     * Distancia recorrida acumulada en kilómetros.
     */
    private final MutableLiveData<Double> distanciaKm = new MutableLiveData<>(0.0);

    /**
     * Tiempo transcurrido durante la grabación.
     */
    private final MutableLiveData<String> tiempoTexto = new MutableLiveData<>("00:00");

    /**
     * Lista temporal donde se almacenan todas las coordenadas
     * capturadas durante la grabación.
     *
     * Estas coordenadas se guardarán posteriormente
     * en la base de datos.
     */
    private final List<CoordenadaRuta> puntosTemporales = new ArrayList<>();

    /**
     * Momento en el que comenzó la grabación.
     */
    private long tiempoInicio = 0;

    /**
     * Número de orden asignado a cada coordenada.
     *
     * Permite reconstruir posteriormente el recorrido
     * respetando el orden original.
     */
    private int ordenPunto = 1;

    /**
     * Último punto GPS registrado.
     *
     * Se utiliza para calcular la distancia respecto
     * al nuevo punto recibido.
     */
    private CoordenadaRuta ultimoPunto = null;

    /**
     * Handler utilizado para actualizar el cronómetro
     * cada segundo.
     */
    private final Handler timerHandler = new Handler(Looper.getMainLooper());

    /**
     * Tarea que actualiza periódicamente el tiempo transcurrido.
     *
     * Se ejecuta cada segundo mientras la grabación esté activa.
     */
    private final Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (Boolean.TRUE.equals(grabando.getValue())) {
                long millis = System.currentTimeMillis() - tiempoInicio;

                int segundos = (int) (millis / 1000);
                int minutos = segundos / 60;
                int horas = minutos / 60;

                segundos = segundos % 60;
                minutos = minutos % 60;

                String formato = horas > 0
                        ? String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos)
                        : String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);

                tiempoTexto.setValue(formato);
                // Vuelve a ejecutarse dentro de 1 segundo
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    /**
     * Constructor del ViewModel.
     *
     * Inicializa el repositorio de rutas.
     */
    public GrabarRutaViewModel(@NonNull Application app) {
        super(app);
        rutaRepo = new RutaPersonalRepository(app);
    }


    /**
     * Devuelve si actualmente se está grabando.
     */
    public LiveData<Boolean> getGrabando() { return grabando; }

    /**
     * Devuelve la distancia recorrida.
     */
    public LiveData<Double> getDistanciaKm() { return distanciaKm; }

    /**
     * Devuelve el tiempo transcurrido.
     */
    public LiveData<String> getTiempoTexto() { return tiempoTexto; }

    /**
     * Devuelve el ID de la última ruta insertada.
     */
    public LiveData<Long> getIdInsertado() { return rutaRepo.getIdInsertado(); }

    /**
     * Devuelve el estado de las operaciones de guardado.
     */
    public LiveData<String> getEstadoOperacion() { return rutaRepo.getEstadoOperacion(); }

    /**
     * Devuelve la lista temporal de coordenadas.
     */
    public List<CoordenadaRuta> getPuntosTemporales() { return puntosTemporales; }


    /**
     * Inicia una nueva grabación GPS.
     *
     * Reinicia:
     * - Coordenadas.
     * - Distancia acumulada.
     * - Cronómetro.
     * - Orden de puntos.
     */
    public void iniciar() {
        puntosTemporales.clear();
        distanciaKm.setValue(0.0);
        tiempoTexto.setValue("00:00");

        ultimoPunto = null;
        ordenPunto = 1;

        tiempoInicio = System.currentTimeMillis();

        grabando.setValue(true);
        timerHandler.post(timerRunnable);
    }

    /**
     * Detiene la grabación actual.
     */
    public void detener() {
        grabando.setValue(false);
        timerHandler.removeCallbacks(timerRunnable);
    }


    /**
     * Añade una nueva coordenada GPS a la ruta.
     *
     * Además calcula la distancia respecto
     * al punto anterior.
     *
     * @param lat Latitud.
     * @param lng Longitud.
     * @param altitud Altitud.
     */

    public void agregarPunto(double lat, double lng, double altitud) {

        CoordenadaRuta punto = new CoordenadaRuta();
        punto.latitud = lat;
        punto.longitud = lng;
        punto.altitud = altitud;
        punto.marcaTiempo = System.currentTimeMillis();
        punto.orden = ordenPunto++;

        if (ultimoPunto != null) {

            float[] resultados = new float[1];

            Location.distanceBetween(
                    ultimoPunto.latitud, ultimoPunto.longitud,
                    lat, lng,
                    resultados
            );

            float distanciaMetros = resultados[0];

            if (distanciaMetros > 1.5) {
                double km = distanciaMetros / 1000.0;

                double actual = distanciaKm.getValue() != null
                        ? distanciaKm.getValue()
                        : 0.0;

                distanciaKm.setValue(actual + km);
            }
        }

        ultimoPunto = punto;
        puntosTemporales.add(punto);
    }

    /**
     * Guarda la ruta actual en la base de datos.
     *
     * Se almacena:
     *
     * - Nombre de la ruta.
     * - Distancia recorrida.
     * - Duración.
     * - Fecha.
     * - Hora.
     * - Coordenadas GPS.
     *
     * @param nombre Nombre de la ruta.
     * @param idPropietario Usuario propietario.
     */
    public void guardar(String nombre, int idPropietario) {

        if (puntosTemporales.isEmpty()) {
            return;
        }

        RutaPersonal ruta = new RutaPersonal();
        ruta.nombre = nombre;
        ruta.idPropietario = idPropietario;
        ruta.distanciaRecorrida = distanciaKm.getValue() != null ? distanciaKm.getValue() : 0.0;

        ruta.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ruta.hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        ruta.duracion = tiempoTexto.getValue();

        rutaRepo.guardarRutaCompleta(ruta, new ArrayList<>(puntosTemporales));
    }
}