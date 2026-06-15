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


/**
 * Actividad encargada de grabar rutas GPS.
 *
 * Funcionalidades principales:
 *
 * - Mostrar el mapa mediante Google Maps.
 * - Obtener la posición GPS del dispositivo.
 * - Dibujar el recorrido en tiempo real.
 * - Calcular distancia y duración de la ruta.
 * - Guardar la ruta junto con todas sus coordenadas.
 */
public class GrabarRutaActivity extends AppCompatActivity implements OnMapReadyCallback {

    /**
     * Código utilizado para solicitar permisos de ubicación.
     */
    private static final int REQUEST_LOCATION = 100;

    /**
     * ViewModel encargado de gestionar la lógica
     * de grabación de la ruta.
     */
    private GrabarRutaViewModel viewModel;

    /**
     * Instancia principal de Google Maps.
     *
     * Permite controlar la cámara, dibujar elementos
     * gráficos y mostrar la ubicación actual.
     */
    private GoogleMap mMap;

    /**
     * Cliente de localización fusionada de Google.
     *
     * Combina GPS, redes móviles y Wi-Fi para obtener
     * ubicaciones precisas con un consumo energético optimizado.
     */
    private FusedLocationProviderClient fusedClient;

    /**
     * Callback encargado de recibir actualizaciones
     * periódicas de ubicación.
     */
    private LocationCallback locationCallback;

    /**
     * Línea dibujada sobre el mapa que representa
     * visualmente el recorrido realizado.
     */
    private Polyline polylineActiva;

    /**
     * Lista de coordenadas utilizadas para construir
     * la representación gráfica de la ruta.
     */
    private final List<LatLng> puntosMapa = new ArrayList<>();

    /**
     * Botones principales de la pantalla.
     */
    private MaterialButton btnIniciarDetener, btnGuardar;

    /**
     * Textos que muestran información en tiempo real.
     */
    private TextView tvDistancia, tvTiempo, tvEstado;

    /**
     * Evita recentrar continuamente el mapa.
     *
     * Solo se centra automáticamente
     * la primera vez que se obtiene ubicación.
     */
    private boolean mapaCentradoInicialmente = false;

    /**
     * Identificador del propietario al que pertenece
     * la ruta que se está grabando.
     */
    private int idPropietario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_ruta);

        btnIniciarDetener = findViewById(R.id.btnIniciarDetener);
        btnGuardar        = findViewById(R.id.btnGuardar);
        tvDistancia       = findViewById(R.id.tvDistancia);
        tvTiempo          = findViewById(R.id.tvTiempo);
        tvEstado          = findViewById(R.id.tvEstado);

        idPropietario =
                getIntent().getIntExtra("ID_PROPIETARIO", -1);

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

    /**
     * Recibe el resultado de la solicitud de permisos.
     *
     * Si el usuario concede acceso a la ubicación,
     * se inicia el mapa y las actualizaciones GPS.
     */
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

    /**
     * Comprueba si el permiso ACCESS_FINE_LOCATION
     * ha sido concedido.
     *
     * @return true si existe permiso de ubicación.
     */
    private boolean checkPermission() {
        return ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Inicializa el fragmento de Google Maps y solicita
     * una notificación cuando el mapa esté listo.
     */
    private void iniciarMapa() {
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null){
            mapFragment.getMapAsync(this);
        }
    }

    /**
     * Método invocado automáticamente cuando Google Maps
     * termina de cargarse.
     *
     * Configura controles visuales y centra la cámara
     * sobre la última ubicación conocida del usuario.
     */
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

    /**
     * Configura el callback encargado de recibir
     * las actualizaciones de ubicación.
     *
     * Cada vez que se reciben coordenadas nuevas,
     * estas son procesadas individualmente.
     */
    private void configurarLocationCallback() {
        locationCallback = new LocationCallback() {

            //Este método se ejecuta cuando el dispositivo recibe una o varias ubicaciones nuevas.
            @Override
            public void onLocationResult(@NonNull LocationResult result) {

                if (mMap == null) {
                    return;
                }
                for (Location loc : result.getLocations()) {
                    procesarLocalizacion(loc);
                }
            }

            // Este método se ejecuta cuando cambia la disponibilidad del servicio de localización
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

    /**
     * Procesa una localización recibida desde el GPS.
     *
     * Funciones realizadas:
     * - Filtrar posiciones con baja precisión.
     * - Centrar el mapa inicialmente.
     * - Ignorar movimientos insignificantes.
     * - Registrar el punto en la ruta.
     * - Actualizar la polilínea mostrada en pantalla.
     *
     * @param loc posición recibida.
     */
    private void procesarLocalizacion(Location loc) {

        // Este filtro evita registrar coordenadas poco fiables.
        if (loc.getAccuracy() > 35) {
            return;
        }

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


        if (!puntosMapa.isEmpty()) {
            LatLng last = puntosMapa.get(puntosMapa.size() - 1);
            float[] results = new float[1];
            Location.distanceBetween(
                    last.latitude, last.longitude,
                    latLng.latitude, latLng.longitude,
                    results
            );
            // Si la nueva posición está a menos de medio metro del punto anterior, se descarta.
            if (results[0] < 0.5f) {
                return;
            }
        }

        viewModel.agregarPunto(loc.getLatitude(), loc.getLongitude(), loc.getAltitude());
        puntosMapa.add(latLng);

        if (polylineActiva != null) {
            polylineActiva.setPoints(puntosMapa);
        }
    }

    /**
     * Inicia las actualizaciones periódicas de ubicación.
     *
     * Se solicita alta precisión y una frecuencia elevada
     * para registrar correctamente movimientos a caballo.
     */
    @SuppressLint("MissingPermission")
    private void iniciarActualizacionesGPS() {

        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                2000
        )
                .setMinUpdateIntervalMillis(1500)
                .setMaxUpdateDelayMillis(3000)
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

    /**
     * Vincula los datos observables del ViewModel
     * con los elementos de la interfaz.
     *
     * Gracias a LiveData la interfaz se actualiza
     * automáticamente cuando cambia el estado.
     */
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

    /**
     * Inicia una nueva grabación.
     *
     * Se eliminan los datos visuales anteriores,
     * se crea una nueva polilínea y se notifica
     * al ViewModel el comienzo del recorrido.
     */
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

    /**
     * Finaliza la grabación actual.
     *
     * El cálculo de estadísticas continúa gestionado
     * por el ViewModel.
     */
    private void detener() {
        viewModel.detener();
    }

    /**
     * Muestra un cuadro de diálogo que permite
     * introducir el nombre de la ruta.
     *
     * Si el usuario no introduce ningún texto,
     * se genera automáticamente un nombre basado
     * en la fecha y hora actuales.
     */
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
                    viewModel.guardar(nombre, idPropietario);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Se ejecuta cuando la actividad vuelve
     * a primer plano.
     *
     * Reactiva el GPS y las actualizaciones
     * de localización si están disponibles.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (checkPermission() && isLocationEnabled()) {
            mapaCentradoInicialmente = false;
            if (mMap == null) {
                iniciarMapa();
            }
            iniciarActualizacionesGPS();
            tvEstado.setText("GPS Conectado");
        }
    }

    /**
     * Se ejecuta al destruir la actividad.
     *
     * Libera los recursos asociados al GPS
     * para evitar fugas de memoria.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (fusedClient != null && locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Comprueba si existe algún proveedor
     * de ubicación activo en el dispositivo.
     *
     * @return true si GPS o red están habilitados.
     */
    private boolean isLocationEnabled() {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return lm != null && (
                lm.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        );
    }

    /**
     * Solicita al usuario que active los servicios
     * de ubicación mediante un diálogo informativo.
     *
     * En caso afirmativo se abre directamente
     * la pantalla de ajustes del sistema.
     */
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