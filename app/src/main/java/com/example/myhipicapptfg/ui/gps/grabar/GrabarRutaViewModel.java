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

public class GrabarRutaViewModel extends AndroidViewModel {

    private final RutaPersonalRepository rutaRepo;

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

                String formato = horas > 0
                        ? String.format(Locale.getDefault(), "%02d:%02d:%02d", horas, minutos, segundos)
                        : String.format(Locale.getDefault(), "%02d:%02d", minutos, segundos);

                tiempoTexto.setValue(formato);
                timerHandler.postDelayed(this, 1000);
            }
        }
    };

    public GrabarRutaViewModel(@NonNull Application app) {
        super(app);
        rutaRepo = new RutaPersonalRepository(app);
    }

    // =========================
    // 🔹 GETTERS
    // =========================

    public LiveData<Boolean> getGrabando() { return grabando; }
    public LiveData<Double> getDistanciaKm() { return distanciaKm; }
    public LiveData<String> getTiempoTexto() { return tiempoTexto; }

    public LiveData<Long> getIdInsertado() { return rutaRepo.getIdInsertado(); }
    public LiveData<String> getEstadoOperacion() { return rutaRepo.getEstadoOperacion(); }

    public List<CoordenadaRuta> getPuntosTemporales() { return puntosTemporales; }

    // =========================
    // 🔹 CONTROL GRABACIÓN
    // =========================

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

    // =========================
    // 🔹 GPS
    // =========================

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

    // =========================
    // 🔹 GUARDAR RUTA
    // =========================

    public void guardar(String nombre, int idPropietario) {

        if (puntosTemporales.isEmpty()) return;

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