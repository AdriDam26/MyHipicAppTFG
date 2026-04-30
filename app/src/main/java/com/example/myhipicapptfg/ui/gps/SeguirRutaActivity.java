package com.example.myhipicapptfg.ui.gps;

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

import com.example.myhipicapptfg.entities.CoordenadaRuta;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.viewmodel.SeguirRutaViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SeguirRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 100;

    // Colores de la ruta
    private static final int COLOR_RUTA_PENDIENTE = 0xAA6200EE; // morado semitransparente
    private static final int COLOR_RUTA_RECORRIDA = 0xFF4CAF50; // verde sólido

    private SeguirRutaViewModel viewModel;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    private TextView tvDistanciaTotal, tvAlPunto, tvProgreso, tvNombreRuta;
    private LinearProgressIndicator progressBar;
    private View bannerCompletado;        // banner que aparece al terminar (NO cierra la app)
    private MaterialButton btnSalir;

    // Polylines: una para lo pendiente, otra para lo ya recorrido
    private Polyline polylinePendiente;
    private Polyline polylineRecorrida;

    private Marker marcadorUsuario;

    private List<CoordenadaRuta> puntosRuta = new ArrayList<>();
    private boolean mapaListo      = false;
    private boolean puntosCargados = false;
    private int     ultimoIndice   = 0;   // último índice cercano conocido

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguir_ruta);

        int idRuta = getIntent().getIntExtra("RUTA_ID", -1);
        if (idRuta == -1) { finish(); return; }

        inicializarVistas();
        inicializarViewModel(idRuta);
        inicializarMapa();
        inicializarGPS();
    }

    // ── Vistas ───────────────────────────────────────────────────────────────

    private void inicializarVistas() {
        tvNombreRuta     = findViewById(R.id.tvNombreRuta);
        tvDistanciaTotal = findViewById(R.id.tvDistanciaTotal);
        tvAlPunto        = findViewById(R.id.tvAlPunto);
        tvProgreso       = findViewById(R.id.tvProgreso);
        progressBar      = findViewById(R.id.progressBar);
        bannerCompletado = findViewById(R.id.bannerCompletado); // añadido al layout
        btnSalir         = findViewById(R.id.btnSalir);         // añadido al layout

        bannerCompletado.setVisibility(View.GONE);
        btnSalir.setOnClickListener(v -> finish());
    }

    // ── ViewModel ────────────────────────────────────────────────────────────

    private void inicializarViewModel(int idRuta) {
        viewModel = new ViewModelProvider(this).get(SeguirRutaViewModel.class);
        configurarObservadores();

        viewModel.cargarPuntos(idRuta).observe(this, puntos -> {
            if (puntos == null || puntos.isEmpty() || puntosCargados) return;
            puntosCargados = true;
            puntosRuta = puntos;
            viewModel.setPuntos(puntos);
            tvDistanciaTotal.setText(String.format(Locale.getDefault(),
                    "%.2f km", calcularDistanciaTotal(puntos)));
            if (mapaListo) dibujarRutaInicial();
        });
    }

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
                        "%.0f m del trazado", metros));
            }
        });

        viewModel.getFueraDeRuta().observe(this, fuera -> {
            tvAlPunto.setTextColor(Boolean.TRUE.equals(fuera)
                    ? Color.RED : Color.BLACK);
        });

        // Índice cercano → actualizamos el coloreado de la polyline
        viewModel.getIndiceCercano().observe(this, indice -> {
            if (indice == null || puntosRuta.isEmpty()) return;
            ultimoIndice = indice;
            actualizarColorPolylines(indice);
        });

        // Completado: mostramos banner, NO cerramos la app
        viewModel.getRutaCompletada().observe(this, completada -> {
            if (Boolean.TRUE.equals(completada)) {
                tvAlPunto.setText("🎉 ¡Ruta completada!");
                bannerCompletado.setVisibility(View.VISIBLE);
                progressBar.setProgress(100);
                tvProgreso.setText("100%");
            }
        });
    }

    // ── Mapa ─────────────────────────────────────────────────────────────────

    private void inicializarMapa() {
        SupportMapFragment f = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.mapSeguir);
        if (f != null) f.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mapaListo = true;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Sin punto azul del sistema: usamos nuestro propio marcador de dirección
        if (!puntosRuta.isEmpty()) dibujarRutaInicial();
    }

    /**
     * Dibuja la ruta completa en morado al arrancar.
     * Luego actualizarColorPolylines() irá pintando de verde lo recorrido.
     */
    private void dibujarRutaInicial() {
        if (puntosRuta.size() < 2 || mMap == null) return;

        // Polyline pendiente (morado) = toda la ruta al inicio
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

        // Polyline recorrida (verde) = vacía al inicio, crece con el movimiento
        polylineRecorrida = mMap.addPolyline(new PolylineOptions()
                .color(COLOR_RUTA_RECORRIDA)
                .width(14f)
                .geodesic(true));

        // Marcadores inicio y fin
        CoordenadaRuta ini = puntosRuta.get(0);
        CoordenadaRuta fin = puntosRuta.get(puntosRuta.size() - 1);
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(ini.latitud, ini.longitud)).title("Inicio")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(fin.latitud, fin.longitud)).title("Fin")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120));
    }

    /**
     * Divide la ruta en dos partes según el índice más cercano:
     * - De 0 a indice → verde (recorrido)
     * - De indice a final → morado (pendiente)
     * Funciona igual en ambas direcciones porque siempre tomamos el punto más cercano.
     */
    private void actualizarColorPolylines(int indiceCercano) {
        if (polylineRecorrida == null || polylinePendiente == null) return;
        if (puntosRuta.isEmpty()) return;

        List<LatLng> recorrida = new ArrayList<>();
        List<LatLng> pendiente = new ArrayList<>();

        for (int i = 0; i < puntosRuta.size(); i++) {
            LatLng ll = new LatLng(puntosRuta.get(i).latitud, puntosRuta.get(i).longitud);
            if (i <= indiceCercano) recorrida.add(ll);
            else pendiente.add(ll);
        }

        // El punto de unión aparece en ambas para que no haya hueco
        if (!recorrida.isEmpty() && !pendiente.isEmpty()) {
            pendiente.add(0, recorrida.get(recorrida.size() - 1));
        }


        polylineRecorrida.setPoints(recorrida);
        polylinePendiente.setPoints(pendiente);
    }

    // ── GPS ──────────────────────────────────────────────────────────────────

    private void inicializarGPS() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        configurarLocationCallback();
        iniciarSeguimiento();
    }

    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {
                Location loc = result.getLastLocation();
                if (loc == null) return;
                if (loc.getAccuracy() > 30) return; // descartamos lecturas imprecisas

                viewModel.actualizarUbicacion(loc.getLatitude(), loc.getLongitude());
                actualizarMarcadorUsuario(loc);
            }
        };
    }

    private void actualizarMarcadorUsuario(Location loc) {
        if (mMap == null) return;
        LatLng pos = new LatLng(loc.getLatitude(), loc.getLongitude());

        if (marcadorUsuario == null) {
            marcadorUsuario = mMap.addMarker(new MarkerOptions()
                    .position(pos)
                    .flat(true)
                    .anchor(0.5f, 0.5f)
                    .rotation(loc.getBearing())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
            // Solo centramos la primera vez
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f));
        } else {
            marcadorUsuario.setPosition(pos);
            marcadorUsuario.setRotation(loc.getBearing());
        }
    }

    private void iniciarSeguimiento() {
        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            return;
        }
        arrancarGPS();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            arrancarGPS();
        }
    }

    @SuppressLint("MissingPermission")
    private void arrancarGPS() {
        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setMinUpdateIntervalMillis(500)
                .build();
        fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    // ── Utilidades ───────────────────────────────────────────────────────────

    private double calcularDistanciaTotal(List<CoordenadaRuta> puntos) {
        if (puntos.size() < 2) return 0;
        double total = 0;
        float[] r = new float[1];
        for (int i = 1; i < puntos.size(); i++) {
            CoordenadaRuta a = puntos.get(i-1), b = puntos.get(i);
            Location.distanceBetween(a.latitud, a.longitud, b.latitud, b.longitud, r);
            total += r[0];
        }
        return total / 1000.0;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }
}