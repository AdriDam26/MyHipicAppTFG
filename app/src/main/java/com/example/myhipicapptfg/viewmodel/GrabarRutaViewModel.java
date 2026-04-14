package com.example.myhipicapptfg.viewmodel;

import android.app.Application;
import android.location.Location;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.example.myhipicapptfg.entities.RutaPersonal;
import com.example.myhipicapptfg.repository.RutaPersonalRepository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class GrabarRutaViewModel extends AndroidViewModel {

    private final RutaPersonalRepository rutaRepo;
    // Ya no es necesario coordRepo aquí porque el guardado es atómico vía rutaRepo

    private final MutableLiveData<Boolean> grabando = new MutableLiveData<>(false);
    private final MutableLiveData<Double> distanciaKm = new MutableLiveData<>(0.0);
    private final MutableLiveData<String> tiempoTexto = new MutableLiveData<>("00:00");

    private final List<CoordenadaRuta> puntosTemporales = new ArrayList<>();
    private long tiempoInicio = 0;
    private int ordenPunto = 1;
    private CoordenadaRuta ultimoPunto = null;

    private final Handler timerHandler = new Handler(Looper.getMainLooper());
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

                String formato = horas > 0 ?
                        String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos) :
                        String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);

                tiempoTexto.setValue(formato);
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    public GrabarRutaViewModel(@NonNull Application app) {
        super(app);
        rutaRepo = new RutaPersonalRepository(app);
    }

    public LiveData<Boolean> getGrabando() { return grabando; }
    public LiveData<Double> getDistanciaKm() { return distanciaKm; }
    public LiveData<String> getTiempoTexto() { return tiempoTexto; }
    public LiveData<Long> getIdInsertado() { return rutaRepo.getIdInsertado(); }
    public LiveData<String> getMensajeStatus() { return rutaRepo.getMensajeStatus(); }
    public List<CoordenadaRuta> getPuntosTemporales() { return puntosTemporales; }

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

    public void detener() {
        grabando.setValue(false);
        timerHandler.removeCallbacks(timerRunnable);
    }

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
                    lat, lng, resultados);

            float distanciaEnMetros = resultados[0];

            if (distanciaEnMetros > 1.5) {
                double distanciaEnKm = distanciaEnMetros / 1000.0;
                double totalActual = distanciaKm.getValue() != null ? distanciaKm.getValue() : 0.0;
                distanciaKm.setValue(totalActual + distanciaEnKm);
            }
        }

        ultimoPunto = punto;
        puntosTemporales.add(punto);
    }

    /**
     * MÉTODO CORREGIDO:
     * Ahora envía la ruta y la lista de puntos al repositorio de forma conjunta.
     */
    public void guardar(String nombre, int idPropietario) {
        if (puntosTemporales.isEmpty()) {
            // Opcional: Notificar que no hay recorrido
            return;
        }

        RutaPersonal ruta = new RutaPersonal();
        ruta.nombre = nombre;
        ruta.idPropietario = idPropietario;
        ruta.distanciaRecorrida = distanciaKm.getValue() != null ? distanciaKm.getValue() : 0.0;
        ruta.fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        ruta.hora = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
        ruta.duracion = tiempoTexto.getValue();

        // Llamamos al nuevo método unificado del repositorio
        // Usamos una copia de la lista (new ArrayList) para evitar problemas de concurrencia
        rutaRepo.guardarRutaCompleta(ruta, new ArrayList<>(puntosTemporales));
    }

    // El método guardarCoordenadas(long idRuta) se ELIMINA
    // porque ya está integrado en el proceso transaccional del repositorio.
}