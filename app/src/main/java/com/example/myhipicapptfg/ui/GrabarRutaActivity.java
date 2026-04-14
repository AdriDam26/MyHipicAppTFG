package com.example.myhipicapptfg.ui;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Propietario;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.GrabarRutaViewModel;
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
    private int idPropietarioActual = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_ruta);

        crearUsuarioPruebaSiNoExiste();

        btnIniciarDetener = findViewById(R.id.btnIniciarDetener);
        btnGuardar = findViewById(R.id.btnGuardar);
        tvDistancia = findViewById(R.id.tvDistancia);
        tvTiempo = findViewById(R.id.tvTiempo);
        tvEstado = findViewById(R.id.tvEstado);

        viewModel = new ViewModelProvider(this).get(GrabarRutaViewModel.class);
        configurarObservadores();

        fusedClient = LocationServices.getFusedLocationProviderClient(this);
        configurarLocationCallback();

        if (!checkPermission()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            iniciarMapa();
            iniciarActualizacionesGPS();
        }

        btnIniciarDetener.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getGrabando().getValue())) detener();
            else iniciar();
        });

        btnGuardar.setOnClickListener(v -> mostrarDialogoGuardar());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            iniciarMapa();
            iniciarActualizacionesGPS();
        } else {
            Toast.makeText(this,
                    "Se necesita permiso de ubicación para grabar rutas.",
                    Toast.LENGTH_LONG).show();
        }
    }

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
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.setMyLocationEnabled(true);

        fusedClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 17f));
            }
        });
    }

    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                Location loc = result.getLastLocation();
                if (loc == null || mMap == null) return;

                if (!Boolean.TRUE.equals(viewModel.getGrabando().getValue())) return;

                LatLng latLng = new LatLng(loc.getLatitude(), loc.getLongitude());

                // Añadir punto al ViewModel
                viewModel.agregarPunto(
                        loc.getLatitude(),
                        loc.getLongitude(),
                        loc.getAltitude()
                );

                // Añadir punto SOLO a la lista
                puntosMapa.add(latLng);

                // Actualizar polyline SIN borrarla
                if (polylineActiva != null) {
                    polylineActiva.setPoints(puntosMapa);
                }

                // Mover cámara suavemente (sin animaciones pesadas)
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            }
        };
    }

    @SuppressLint("MissingPermission")
    private void iniciarActualizacionesGPS() {

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                3000 // cada 3 segundos
        )
                .setMinUpdateIntervalMillis(2000)
                .build();

        fusedClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );
    }

    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }

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
                        android.content.res.ColorStateList.valueOf(
                                Color.parseColor("#018786")));
                tvEstado.setText("Listo para grabar");
                btnGuardar.setEnabled(!viewModel.getPuntosTemporales().isEmpty());
            }
        });

        viewModel.getIdInsertado().observe(this, id -> {
            if (id != null) {
                Toast.makeText(this,
                        "Ruta guardada correctamente",
                        Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getMensajeStatus().observe(this, msg -> {
            if (msg != null)
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
    }

    private void iniciar() {

        puntosMapa.clear();

        if (polylineActiva != null) {
            polylineActiva.remove();
        }

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

    private void mostrarDialogoGuardar() {

        View v = LayoutInflater.from(this)
                .inflate(R.layout.dialog_nombre_ruta, null);

        TextInputEditText et = v.findViewById(R.id.etNombreRuta);

        String fechaDefault = new SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
        ).format(new Date());

        et.setText("Ruta " + fechaDefault);

        new MaterialAlertDialogBuilder(this)
                .setTitle("Guardar Ruta")
                .setView(v)
                .setPositiveButton("Guardar", (d, w) -> {

                    String nombre = et.getText() != null
                            ? et.getText().toString()
                            : "Ruta " + fechaDefault;

                    viewModel.guardar(nombre, idPropietarioActual);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    private void crearUsuarioPruebaSiNoExiste() {

        TestDatabase db = TestDatabase.getInstance(this);

        new Thread(() -> {
            try {

                if (!db.rutaPersonalDao().esPropietarioValido(1)) {

                    Usuario usuario = new Usuario();
                    usuario.nombre = "Carlos";
                    usuario.apellido1 = "García";
                    usuario.apellido2 = "López";

                    long timestamp = System.currentTimeMillis();
                    usuario.dni = "DNI" + (timestamp % 10000);
                    usuario.email = "carlos@test.com";
                    usuario.telefono = "600123456";
                    usuario.fechaNacimiento = "1990-01-01";
                    usuario.sexo = Usuario.SEXO_MASCULINO;
                    usuario.tipo = Usuario.TIPO_PROPIETARIO;

                    long idGenerado = db.usuarioDao().insertarUsuario(usuario);

                    Propietario propietario = new Propietario();
                    propietario.idPropietario = (int) idGenerado;
                    db.propietarioDao().insertarPropietario(propietario);

                    idPropietarioActual = (int) idGenerado;

                } else {
                    idPropietarioActual = 1;
                }

            } catch (Exception e) {
                Log.e(TAG, "Error inicialización prueba: " + e.getMessage());
            }
        }).start();
    }
}