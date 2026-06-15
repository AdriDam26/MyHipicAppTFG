package com.example.myhipicapptfg.ui.gps.seguir;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.datos.local.entidades.CoordenadaRuta;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import com.example.myhipicapptfg.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import android.widget.Toast;

/**
 * Actividad encargada de guiar al usuario a lo largo de una ruta GPS
 * previamente grabada.
 *
 * Funcionalidades principales:
 *
 * - Cargar las coordenadas de una ruta almacenada.
 * - Mostrar el recorrido sobre Google Maps.
 * - Obtener la ubicación actual del usuario mediante GPS.
 * - Calcular el progreso realizado sobre la ruta.
 * - Detectar desviaciones respecto al trazado original.
 * - Mostrar visualmente la parte recorrida y pendiente.
 * - Indicar cuándo la ruta ha sido completada.
 */
public class SeguirRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Código utilizado para solicitar permisos
     * de acceso a la ubicación.
     */
    private static final int REQUEST_LOCATION = 100;

    /**
     * Color utilizado para representar
     * la parte pendiente de la ruta.
     */
    private static final int COLOR_RUTA_PENDIENTE = 0xAA6200EE;
    /**
     * Color utilizado para representar
     * la parte ya recorrida de la ruta.
     */
    private static final int COLOR_RUTA_RECORRIDA = 0xFF4CAF50;

    /**
     * ViewModel encargado de gestionar
     * la lógica de seguimiento de rutas.
     */
    private SeguirRutaViewModel viewModel;

    /**
     * Instancia principal de Google Maps.
     */
    private GoogleMap mMap;
    /**
     * Cliente de localización fusionada.
     *
     * Combina GPS, Wi-Fi y redes móviles
     * para obtener ubicaciones precisas.
     */
    private FusedLocationProviderClient fusedClient;
    /**
     * Callback que recibe las actualizaciones
     * periódicas de ubicación.
     */
    private LocationCallback locationCallback;

    /**
     * Indica si el sistema de seguimiento GPS
     * se encuentra actualmente activo.
     */
    private boolean gpsActivo = false;

    /**
     * Textos informativos mostrados al usuario.
     */
    private TextView tvDistanciaTotal, tvAlPunto, tvProgreso, tvNombreRuta;

    /**
     * Textos informativos mostrados al usuario.
     */
    private LinearProgressIndicator progressBar;

    /**
     * Panel mostrado cuando la ruta
     * ha sido completada.
     */
    private View bannerCompletado;

    /**
     * Botón para abandonar la pantalla
     * de seguimiento.
     */
    private MaterialButton btnSalir;

    /**
     * Polilínea que representa la parte
     * pendiente de la ruta.
     */
    private Polyline polylinePendiente;

    /**
     * Polilínea que representa la parte
     * ya recorrida por el usuario.
     */
    private Polyline polylineRecorrida;

    /**
     * Marcador que indica la posición
     * actual del usuario sobre el mapa.
     */
    private Marker marcadorUsuario;

    /**
     * Lista de coordenadas que forman
     * el recorrido almacenado.
     */
    private List<CoordenadaRuta> puntosRuta = new ArrayList<>();

    /**
     * Indica si Google Maps ya se encuentra listo.
     */
    private boolean mapaListo      = false;

    /**
     * Evita cargar varias veces la misma ruta.
     */
    private boolean puntosCargados = false;

    /**
     * Último punto alcanzado por el usuario.
     *
     * Se utiliza para restaurar el progreso
     * visual tras cambios de configuración
     * o regreso desde ajustes del sistema.
     */
    private int     ultimoIndice   = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguir_ruta);

        int idRuta = getIntent().getIntExtra("RUTA_ID", -1);
        if (idRuta == -1) {
            finish(); return;
        }

        inicializarVistas();
        inicializarViewModel(idRuta);
        inicializarMapa();
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        configurarLocationCallback();
        inicializarGPS();
    }

    /**
     * Inicializa todos los componentes visuales de la interfaz
     * y configura los eventos básicos de interacción.
     */
    private void inicializarVistas() {
        tvNombreRuta     = findViewById(R.id.tvNombreRuta);
        tvDistanciaTotal = findViewById(R.id.tvDistanciaTotal);
        tvAlPunto        = findViewById(R.id.tvAlPunto);
        tvProgreso       = findViewById(R.id.tvProgreso);
        progressBar      = findViewById(R.id.progressBar);
        bannerCompletado = findViewById(R.id.bannerCompletado);
        btnSalir         = findViewById(R.id.btnSalir);

        bannerCompletado.setVisibility(View.GONE);
        btnSalir.setOnClickListener(v -> finish());
    }

    /**
     * Inicializa el ViewModel encargado del seguimiento de rutas.
     *
     * Además:
     * - Configura los observadores de LiveData.
     * - Carga las coordenadas asociadas a la ruta seleccionada.
     * - Calcula la distancia total de la ruta.
     * - Dibuja el recorrido cuando el mapa está listo.
     */
    private void inicializarViewModel(int idRuta) {
        viewModel = new ViewModelProvider(this).get(SeguirRutaViewModel.class);
        configurarObservadores();

        viewModel.cargarPuntos(idRuta).observe(this, puntos -> {
            if (puntos == null || puntos.isEmpty() || puntosCargados) {
                return;
            }
            puntosCargados = true;
            puntosRuta = puntos;
            viewModel.setPuntos(puntos);
            tvDistanciaTotal.setText(String.format(Locale.getDefault(),
                    "%.2f km", calcularDistanciaTotal(puntos)));
            if (mapaListo) {
                dibujarRutaInicial();
            }
        });
    }

    /**
     * Registra los observadores que reaccionan a los cambios
     * producidos en el ViewModel.
     *
     * Actualiza automáticamente:
     * - El porcentaje de progreso.
     * - La distancia al siguiente punto.
     * - Los avisos de salida de ruta.
     * - El coloreado del recorrido.
     * - El estado de finalización.
     */
    private void configurarObservadores() {

        viewModel.getProgreso().observe(this, pct -> {
            progressBar.setProgress(pct);
            tvProgreso.setText(pct + "%");
        });

        viewModel.getDistanciaAlPunto().observe(this, metros -> {
            if (Boolean.TRUE.equals(viewModel.getFueraDeRuta().getValue())) {
                tvAlPunto.setText(String.format(Locale.getDefault(),
                        "⚠ %.0f m del trazado", metros));
            } else {
                tvAlPunto.setText(String.format(Locale.getDefault(),
                        "%.0f m al siguiente punto", metros));
            }
        });

        viewModel.getFueraDeRuta().observe(this, fuera -> {
            tvAlPunto.setTextColor(Boolean.TRUE.equals(fuera) ? Color.RED : Color.BLACK);
        });

        viewModel.getIndiceCercano().observe(this, indice -> {
            if (indice == null || puntosRuta.isEmpty()) return;
            ultimoIndice = indice;
            actualizarColorPolylines(indice);
        });

        viewModel.getRutaCompletada().observe(this, completada -> {
            if (Boolean.TRUE.equals(completada)) {
                tvAlPunto.setText("🎉 ¡Ruta completada!");
                bannerCompletado.setVisibility(View.VISIBLE);
                progressBar.setProgress(100);
                tvProgreso.setText("100%");
                detenerGPS();
            }
        });
    }

    /**
     * Obtiene el fragmento de Google Maps y solicita
     * la carga asíncrona del mapa.
     */
    private void inicializarMapa() {
        SupportMapFragment f = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapSeguir);
        if (f != null) f.getMapAsync(this);
    }

    /**
     * Método invocado automáticamente cuando Google Maps
     * termina de inicializarse correctamente.
     *
     * Configura elementos visuales del mapa y dibuja
     * la ruta si ya se encuentra cargada.
     */
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapaListo = true;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        if (!puntosRuta.isEmpty()) {
            dibujarRutaInicial();
        }
    }

    /**
     * Dibuja la ruta completa sobre el mapa.
     *
     * Se crean:
     * - Una polilínea para el tramo pendiente.
     * - Una polilínea para el tramo recorrido.
     * - Un marcador de inicio.
     * - Un marcador de fin.
     *
     * Además se ajusta la cámara para mostrar
     * el recorrido completo.
     */
    private void dibujarRutaInicial() {
        if (puntosRuta.size() < 2 || mMap == null) {
            return;
        }

        PolylineOptions pendiente = new PolylineOptions()
                .color(COLOR_RUTA_PENDIENTE)
                .width(14f)
                .geodesic(true);

        LatLngBounds.Builder bounds = new LatLngBounds.Builder();
        for (CoordenadaRuta p : puntosRuta) {
            LatLng ll = new LatLng(p.latitud, p.longitud);
            pendiente.add(ll);
            bounds.include(ll);
        }
        polylinePendiente = mMap.addPolyline(pendiente);

        polylineRecorrida = mMap.addPolyline(new PolylineOptions()
                .color(COLOR_RUTA_RECORRIDA)
                .width(14f)
                .geodesic(true));

        CoordenadaRuta ini = puntosRuta.get(0);
        CoordenadaRuta fin = puntosRuta.get(puntosRuta.size() - 1);

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(ini.latitud, ini.longitud))
                .title("Inicio")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));

        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(fin.latitud, fin.longitud))
                .title("Fin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


        // Ajusta la cámara para ver toda la ruta
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120));


        if (ultimoIndice > 0) {
            actualizarColorPolylines(ultimoIndice);
        }
    }

    /**
     * Dibuja la ruta completa sobre el mapa.
     *
     * Se crean:
     * - Una polilínea para el tramo pendiente.
     * - Una polilínea para el tramo recorrido.
     * - Un marcador de inicio.
     * - Un marcador de fin.
     *
     * Además se ajusta la cámara para mostrar
     * el recorrido completo.
     */
    private void actualizarColorPolylines(int indiceCercano) {
        if (polylineRecorrida == null || polylinePendiente == null) {
            return;
        }
        if (puntosRuta.isEmpty()) {
            return;
        }

        List<LatLng> recorrida = new ArrayList<>();
        List<LatLng> pendiente  = new ArrayList<>();

        for (int i = 0; i < puntosRuta.size(); i++) {
            LatLng ll = new LatLng(puntosRuta.get(i).latitud, puntosRuta.get(i).longitud);
            if (i <= indiceCercano) {
                recorrida.add(ll); // Verde
            }
            else {
                pendiente.add(ll); // Morado
            }
        }

        // Une ambas polilíneas sin hueco visual
        if (!recorrida.isEmpty() && !pendiente.isEmpty()) {
            pendiente.add(0, recorrida.get(recorrida.size() - 1));
        }

        polylineRecorrida.setPoints(recorrida);
        polylinePendiente.setPoints(pendiente);
    }


    private void inicializarGPS() {
        iniciarSeguimiento();
    }

    /**
     * Configura el callback encargado de recibir
     * las actualizaciones periódicas de ubicación.
     *
     * Cada nueva posición:
     * - Actualiza la lógica del seguimiento.
     * - Mueve el marcador del usuario.
     *
     * También informa cuando el GPS pierde señal.
     */
    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {


                for (Location loc : result.getLocations()) {
                    if (loc.getAccuracy() <= 30) { // Filtra posiciones poco precisas
                        viewModel.actualizarUbicacion(loc.getLatitude(), loc.getLongitude());
                        actualizarMarcadorUsuario(loc);
                    }
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability availability) {
                super.onLocationAvailability(availability);
                if (!availability.isLocationAvailable()) {
                    tvAlPunto.setText("⚠️ Buscando señal GPS...");
                    tvAlPunto.setTextColor(Color.RED);
                } else {
                    tvAlPunto.setTextColor(Color.BLACK);
                }
            }
        };
    }

    /**
     * Actualiza la posición del usuario sobre el mapa.
     *
     * Si todavía no existe un marcador, lo crea en la ubicación
     * actual y centra la cámara sobre dicha posición.
     *
     * Si el marcador ya existe, simplemente actualiza su
     * posición y orientación para reflejar el movimiento
     * del usuario en tiempo real.
     *
     * @param loc Ubicación GPS recibida.
     */
    private void actualizarMarcadorUsuario(Location loc) {
        if (mMap == null) {
            return;
        }
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

        if (marcadorUsuario == null) {
            marcadorUsuario = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .rotation(loc.getBearing())
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f));
        } else {
            marcadorUsuario.setPosition(pos);
            marcadorUsuario.setRotation(loc.getBearing());
        }
    }

    /**
     * Inicia el proceso de seguimiento GPS.
     *
     * Comprueba:
     * - Permisos de ubicación.
     * - Estado del GPS.
     *
     * Si todo es correcto comienza a recibir
     * actualizaciones de localización.
     */
    private void iniciarSeguimiento() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else if (!isLocationEnabled()) {
            pedirActivarUbicacion();
        } else {
            arrancarGPS();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (isLocationEnabled()) {
                arrancarGPS();
            } else {
                pedirActivarUbicacion();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void arrancarGPS() {
        if (gpsActivo){
            return;
        }

        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1500)
                .setMaxUpdateDelayMillis(3000)
                .build();

        fusedClient.removeLocationUpdates(locationCallback);
        fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
        gpsActivo = true;
    }


    private void detenerGPS() {
        if (fusedClient != null && locationCallback != null && gpsActivo) {
            fusedClient.removeLocationUpdates(locationCallback);
            gpsActivo = false;
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Calcula la distancia total acumulada de una ruta.
     *
     * Comprueba:
     * - Si la lista contiene al menos dos puntos para poder operar.
     *
     * Si todo es correcto devuelve la distancia total sumada entre
     * todos los puntos consecutivos, convertida a kilómetros.
     */
    private double calcularDistanciaTotal(List<CoordenadaRuta> puntos) {
        if (puntos.size() < 2) return 0;
        double total = 0;
        float[] r = new float[1];
        for (int i = 1; i < puntos.size(); i++) {
            CoordenadaRuta a = puntos.get(i - 1), b = puntos.get(i);
            Location.distanceBetween(a.latitud, a.longitud, b.latitud, b.longitud, r);
            total += r[0];
        }
        return total / 1000.0;
    }


    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPermission()) {
            return;
        }

        if (!isLocationEnabled()) {
            pedirActivarUbicacion();
            return;
        }


        detenerGPS();
        arrancarGPS();


        if (mapaListo && puntosCargados && ultimoIndice > 0) {
            actualizarColorPolylines(ultimoIndice);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        detenerGPS();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerGPS();
    }


    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm != null && (
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        );
    }

    private void pedirActivarUbicacion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("GPS Desactivado")
                .setMessage("Para seguir la ruta correctamente es necesario activar la ubicación.")
                .setPositiveButton("Configuración", (d, w) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancelar", (d, w) ->
                        Toast.makeText(this,
                                "El seguimiento no funcionará sin GPS",
                                Toast.LENGTH_SHORT).show())
                .setCancelable(false)
                .show();
    }
}