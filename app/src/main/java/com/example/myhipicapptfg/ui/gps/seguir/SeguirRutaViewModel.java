package com.example.myhipicapptfg.ui.gps.seguir;

import android.app.Application;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.example.myhipicapptfg.datos.repository.CoordenadaRutaRepository;

import java.util.ArrayList;
import java.util.List;

public class SeguirRutaViewModel extends AndroidViewModel {

    private final CoordenadaRutaRepository coordRepo;

    private final MutableLiveData<Integer> progreso = new MutableLiveData<>(0);
    private final MutableLiveData<Double> distanciaAlTrazado = new MutableLiveData<>(0.0);
    private final MutableLiveData<Boolean> rutaCompletada = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> fueraDeRuta = new MutableLiveData<>(false);
    private final MutableLiveData<Integer> indiceCercano = new MutableLiveData<>(0);

    private List<CoordenadaRuta> puntos = new ArrayList<>();

    private int indiceActual = 0;
    private int direccion = 1; // 1 = normal, -1 = inversa
    private boolean direccionDefinida = false;

    private long ultimoCambio = 0;

    private static final float RADIO_LLEGADA = 20f; // metros
    private static final float RADIO_FUERA_RUTA = 40f;
    private static final long TIEMPO_MINIMO_ENTRE_PUNTOS = 2000; // 2 segundos

    public SeguirRutaViewModel(@NonNull Application app) {
        super(app);
        coordRepo = new CoordenadaRutaRepository(app);
    }

    // ───────────── GETTERS ─────────────

    public LiveData<Integer> getProgreso() { return progreso; }
    public LiveData<Double> getDistanciaAlPunto() { return distanciaAlTrazado; }
    public LiveData<Boolean> getRutaCompletada() { return rutaCompletada; }
    public LiveData<Boolean> getFueraDeRuta() { return fueraDeRuta; }
    public LiveData<Integer> getIndiceCercano() { return indiceCercano; }

    public LiveData<List<CoordenadaRuta>> cargarPuntos(int idRuta) {
        return coordRepo.obtenerCoordenadasPorRuta(idRuta);
    }

    // ───────────── INICIALIZACIÓN ─────────────

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

    // ───────────── LÓGICA PRINCIPAL ─────────────

    public void actualizarUbicacion(double lat, double lng) {

        if (puntos == null || puntos.size() < 2) return;
        if (Boolean.TRUE.equals(rutaCompletada.getValue())) return;

        float[] resultado = new float[1];

        // 🔥 1️⃣ Detectar dirección SOLO la primera vez
        if (!direccionDefinida) {

            float[] rInicio = new float[1];
            float[] rFinal = new float[1];

            CoordenadaRuta inicio = puntos.get(0);
            CoordenadaRuta fin = puntos.get(puntos.size() - 1);

            Location.distanceBetween(lat, lng, inicio.latitud, inicio.longitud, rInicio);
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

        // 🔥 2️⃣ Punto objetivo actual
        CoordenadaRuta objetivo = puntos.get(indiceActual);

        Location.distanceBetween(
                lat, lng,
                objetivo.latitud,
                objetivo.longitud,
                resultado
        );

        float distancia = resultado[0];

        distanciaAlTrazado.postValue((double) distancia);
        fueraDeRuta.postValue(distancia > RADIO_FUERA_RUTA);

        // 🔥 3️⃣ Control antirrebote
        long ahora = System.currentTimeMillis();

        if (distancia < RADIO_LLEGADA &&
                ahora - ultimoCambio > TIEMPO_MINIMO_ENTRE_PUNTOS) {

            ultimoCambio = ahora;
            indiceActual += direccion;

            boolean terminado =
                    (direccion == 1 && indiceActual >= puntos.size()) ||
                            (direccion == -1 && indiceActual < 0);

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

            int pct = (int) ((puntosRecorridos / (float) puntos.size()) * 100);
            progreso.postValue(pct);
        }
    }
}