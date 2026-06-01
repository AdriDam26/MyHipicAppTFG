package com.example.myhipicapptfg.ui.gps.grabar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.google.android.gms.location.*;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.*;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.content.Context;


public class GrabarRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_LOCATION = 100;

    private GrabarRutaViewModel viewModel;
    private GoogleMap mMap;
    private FusedLocationProviderClient fusedClient;
    private LocationCallback locationCallback;

    private Polyline polylineActiva;
    private final List<LatLng> puntosMapa = new ArrayList<>();

    private MaterialButton btnIniciarDetener, btnGuardar;
    private TextView tvDistancia, tvTiempo, tvEstado;

    private boolean mapaCentradoInicialmente = false;

    private int idPropietarioActual = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_ruta);

        btnIniciarDetener = findViewById(R.id.btnIniciarDetener);
        btnGuardar        = findViewById(R.id.btnGuardar);
        tvDistancia       = findViewById(R.id.tvDistancia);
        tvTiempo          = findViewById(R.id.tvTiempo);
        tvEstado          = findViewById(R.id.tvEstado);

        viewModel = new ViewModelProvider(this).get(GrabarRutaViewModel.class);
        configurarObservadores();

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        configurarLocationCallback();

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION
            );
        } else if (!isLocationEnabled()) {
            pedirActivarUbicacion();
        } else {
            iniciarMapa();
            iniciarActualizacionesGPS();
        }

        btnIniciarDetener.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getGrabando().getValue())) {
                detener();
            } else {
                iniciar();
            }
        });

        btnGuardar.setOnClickListener(v -> mostrarDialogoGuardar());
    }

    // =========================
    // 🔹 PERMISOS
    // =========================

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (!isLocationEnabled()) {
                pedirActivarUbicacion();
            } else {
                iniciarMapa();
                iniciarActualizacionesGPS();
            }
        } else {
            Toast.makeText(this, "Se necesita permiso de ubicación", Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

    // =========================
    // 🔹 MAPA
    // =========================

    private void iniciarMapa() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) mapFragment.getMapAsync(this);
    }

    @Override
    @SuppressLint("MissingPermission")
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.setMyLocationEnabled(true);

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f));
            }
        });
    }

    // =========================
    // 🔹 GPS CALLBACK
    // =========================

    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                if (mMap == null) return;

                // ✅ CORRECCIÓN: procesar TODAS las localizaciones del lote,
                //    no solo la última. Así no se pierden puntos intermedios.
                for (Location loc : result.getLocations()) {
                    procesarLocalizacion(loc);
                }
            }

            @Override
            public void onLocationAvailability(@NonNull LocationAvailability availability) {
                super.onLocationAvailability(availability);
                if (!availability.isLocationAvailable()) {
                    tvEstado.setText("🛰️ Buscando satélites...");
                } else {
                    if (Boolean.TRUE.equals(viewModel.getGrabando().getValue())) {
                        tvEstado.setText("● Grabando");
                    }
                }
            }
        };
    }

    // ✅ NUEVO: lógica de cada punto extraída a su propio método
    private void procesarLocalizacion(Location loc) {

        // ✅ CORRECCIÓN: umbral subido de 20 → 35 metros.
        //    En ciudad/exterior con árboles rara vez se baja de 20m.
        if (loc.getAccuracy() > 35) return;

        LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

        // Centrar cámara la primera vez
        if (!mapaCentradoInicialmente) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f));
            mapaCentradoInicialmente = true;
        }

        if (!Boolean.TRUE.equals(viewModel.getGrabando().getValue())) {
            tvEstado.setText("Listo para iniciar");
            return;
        }

        // ✅ CORRECCIÓN: umbral de micro-movimientos bajado de 2.0 → 0.5 metros.
        //    Con 2m + 6s de intervalo se perdían giros y pasos lentos.
        if (!puntosMapa.isEmpty()) {
            LatLng last = puntosMapa.get(puntosMapa.size() - 1);
            float[] results = new float[1];
            Location.distanceBetween(
                    last.latitude, last.longitude,
                    latLng.latitude, latLng.longitude,
                    results
            );
            if (results[0] < 0.5f) return;
        }

        viewModel.agregarPunto(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
        puntosMapa.add(latLng);

        if (polylineActiva != null) {
            polylineActiva.setPoints(puntosMapa);
        }
    }

    @SuppressLint("MissingPermission")
    private void iniciarActualizacionesGPS() {

        // ✅ CORRECCIÓN: intervalo bajado de 6000/4000 → 2000/1500 ms.
        //    A caballo se pueden recorrer 10-15m en 6 segundos.
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000
        )
                .setMinUpdateIntervalMillis(1500)
                .setMaxUpdateDelayMillis(3000)   // ✅ NUEVO: máximo retardo de entrega del lote
                .build();

        fusedClient.removeLocationUpdates(locationCallback);
        fusedClient.requestLocationUpdates(request, locationCallback, Looper.getMainLooper());

        // Verificación de ajustes del sistema
        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder().addLocationRequest(request);

        LocationServices.getSettingsClient(this)
                .checkLocationSettings(builder.build())
                .addOnFailureListener(this, e -> {
                    tvEstado.setText("⚠️ Señal débil");
                    tvEstado.setTextColor(Color.RED);
                });
    }

    // =========================
    // 🔹 UI / OBSERVADORES
    // =========================

    private void configurarObservadores() {

        viewModel.getDistanciaKm().observe(this,
                km -> tvDistancia.setText(
                        String.format(Locale.getDefault(), "%.2f km", km)));

        viewModel.getTiempoTexto().observe(this,
                t -> tvTiempo.setText(t));

        viewModel.getGrabando().observe(this, grabando -> {
            if (grabando) {
                btnIniciarDetener.setText("Detener");
                btnIniciarDetener.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.RED));
                tvEstado.setText("● Grabando");
                btnGuardar.setEnabled(false);
            } else {
                btnIniciarDetener.setText("Iniciar");
                btnIniciarDetener.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(Color.parseColor("#018786")));
                tvEstado.setText("Listo");
                btnGuardar.setEnabled(!viewModel.getPuntosTemporales().isEmpty());
            }
        });

        viewModel.getIdInsertado().observe(this, id -> {
            if (id != null) {
                Toast.makeText(this, "Ruta guardada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;
            switch (estado) {
                case "ERROR_BD":
                    Toast.makeText(this, "Error al guardar ruta", Toast.LENGTH_LONG).show();
                    break;
                case "ERROR_PROPIETARIO_NO_VALIDO":
                    Toast.makeText(this, "Propietario no válido", Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }

    // =========================
    // 🔹 CONTROL
    // =========================

    private void iniciar() {
        puntosMapa.clear();

        if (polylineActiva != null) polylineActiva.remove();

        polylineActiva = mMap.addPolyline(
                new PolylineOptions()
                        .color(Color.BLUE)
                        .width(12f)
        );

        viewModel.iniciar();
    }

    private void detener() {
        viewModel.detener();
    }

    // =========================
    // 🔹 GUARDAR
    // =========================

    private void mostrarDialogoGuardar() {

        View v = LayoutInflater.from(this).inflate(R.layout.dialog_nombre_ruta, null);
        TextInputEditText et = v.findViewById(R.id.etNombreRuta);

        String fecha = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                .format(new Date());
        et.setText("Ruta " + fecha);

        new MaterialAlertDialogBuilder(this, R.style.Theme_MyHipicApp_Dialog_Rutas)
                .setTitle("Guardar Ruta")
                .setView(v)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = et.getText() != null
                            && !et.getText().toString().trim().isEmpty()
                            ? et.getText().toString()
                            : "Ruta " + fecha;
                    viewModel.guardar(nombre, idPropietarioActual);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // =========================
    // 🔹 CICLO DE VIDA
    // =========================

    @Override
    protected void onResume() {
        super.onResume();

        if (checkPermission() && isLocationEnabled()) {
            mapaCentradoInicialmente = false;
            if (mMap == null) iniciarMapa();
            iniciarActualizacionesGPS();
            tvEstado.setText("GPS Conectado");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    // =========================
    // 🔹 UBICACIÓN DEL SISTEMA
    // =========================

    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm != null && (
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        );
    }

    private void pedirActivarUbicacion() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Ubicación desactivada")
                .setMessage("Para grabar una ruta necesitas tener la ubicación activada. ¿Quieres ir a Ajustes?")
                .setPositiveButton("Ir a Ajustes", (d, w) -> {
                    startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                })
                .setNegativeButton("Cancelar", null)
                .setCancelable(false)
                .show();
    }
}