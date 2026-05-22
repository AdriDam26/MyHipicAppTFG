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

public class SeguirRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 100;

    private static final int COLOR_RUTA_PENDIENTE = 0xAA6200EE;
    private static final int COLOR_RUTA_RECORRIDA = 0xFF4CAF50;

    private SeguirRutaViewModel viewModel;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    // ✅ NUEVO: flag para saber si el GPS está actualmente activo
    private boolean gpsActivo = false;

    private TextView tvDistanciaTotal, tvAlPunto, tvProgreso, tvNombreRuta;
    private LinearProgressIndicator progressBar;
    private View bannerCompletado;
    private MaterialButton btnSalir;

    private Polyline polylinePendiente;
    private Polyline polylineRecorrida;
    private Marker marcadorUsuario;

    private List<CoordenadaRuta> puntosRuta = new ArrayList<>();
    private boolean mapaListo      = false;
    private boolean puntosCargados = false;
    private int     ultimoIndice   = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seguir_ruta);

        int idRuta = getIntent().getIntExtra("RUTA_ID", -1);
        if (idRuta == -1) { finish(); return; }

        inicializarVistas();
        inicializarViewModel(idRuta);
        inicializarMapa();

        // ✅ CORRECCIÓN: inicializamos fusedClient y callback ANTES de cualquier
        //    llamada a arrancarGPS(), así onResume() nunca los encuentra nulos.
        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        configurarLocationCallback();

        inicializarGPS();
    }

    // ── Vistas ───────────────────────────────────────────────────────────────

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
                // ✅ NUEVO: detenemos el GPS al completar, ya no hace falta
                detenerGPS();
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
        if (!puntosRuta.isEmpty()) dibujarRutaInicial();
    }

    private void dibujarRutaInicial() {
        if (puntosRuta.size() < 2 || mMap == null) return;

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

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 120));

        // ✅ NUEVO: si ya teníamos progreso previo (vuelta de ajustes), lo restauramos
        if (ultimoIndice > 0) {
            actualizarColorPolylines(ultimoIndice);
        }
    }

    private void actualizarColorPolylines(int indiceCercano) {
        if (polylineRecorrida == null || polylinePendiente == null) return;
        if (puntosRuta.isEmpty()) return;

        List<LatLng> recorrida = new ArrayList<>();
        List<LatLng> pendiente  = new ArrayList<>();

        for (int i = 0; i < puntosRuta.size(); i++) {
            LatLng ll = new LatLng(puntosRuta.get(i).latitud, puntosRuta.get(i).longitud);
            if (i <= indiceCercano) recorrida.add(ll);
            else pendiente.add(ll);
        }

        if (!recorrida.isEmpty() && !pendiente.isEmpty()) {
            pendiente.add(0, recorrida.get(recorrida.size() - 1));
        }

        polylineRecorrida.setPoints(recorrida);
        polylinePendiente.setPoints(pendiente);
    }

    // ── GPS ──────────────────────────────────────────────────────────────────

    private void inicializarGPS() {
        iniciarSeguimiento();
    }

    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                // ✅ CORRECCIÓN: igual que en GrabarRuta, procesamos TODOS los puntos del lote
                for (Location loc : result.getLocations()) {
                    if (loc.getAccuracy() <= 30) {
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
                    // ✅ NUEVO: cuando el GPS vuelve, restauramos el color del texto
                    tvAlPunto.setTextColor(Color.BLACK);
                }
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
                    .icon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_AZURE)));
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
        if (gpsActivo) return; // ✅ CORRECCIÓN: evita registrar el callback dos veces

        LocationRequest req = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 2000)
                .setMinUpdateIntervalMillis(1500)
                .setMaxUpdateDelayMillis(3000)    // ✅ NUEVO: igual que GrabarRuta
                .build();

        fusedClient.removeLocationUpdates(locationCallback); // limpieza defensiva
        fusedClient.requestLocationUpdates(req, locationCallback, Looper.getMainLooper());
        gpsActivo = true;
    }

    // ✅ NUEVO: método centralizado para detener el GPS de forma segura
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

    // ── Utilidades ───────────────────────────────────────────────────────────

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

    // ── Ciclo de vida ────────────────────────────────────────────────────────

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPermission()) return;

        if (!isLocationEnabled()) {
            pedirActivarUbicacion();
            return;
        }

        // ✅ CORRECCIÓN PRINCIPAL: siempre detenemos antes de volver a arrancar.
        //    Así evitamos callbacks duplicados al volver de Ajustes del sistema.
        detenerGPS();
        arrancarGPS();

        // Si el mapa ya está listo y tenemos puntos, restauramos el progreso visual
        if (mapaListo && puntosCargados && ultimoIndice > 0) {
            actualizarColorPolylines(ultimoIndice);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // ✅ NUEVO: pausamos el GPS cuando la app pasa a segundo plano.
        //    Se reanuda en onResume() al volver.
        detenerGPS();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detenerGPS();
    }

    // ── Sistema GPS ──────────────────────────────────────────────────────────

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