package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.viewmodel.AREquinoViewModel;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class ARActivity extends AppCompatActivity {

    private static final String TAG = "ARActivity";

    private ArFragment arFragment;
    private AREquinoViewModel viewModel;
    private Node nodoTarjeta;
    private boolean tarjetaCreada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        // Botón volver
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Recibir microchip del QRScannerActivity
        String microchip = getIntent().getStringExtra("MICROCHIP");
        if (microchip == null || microchip.isEmpty()) {
            Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Iniciar ViewModel y pedir el caballo
        viewModel = new ViewModelProvider(this).get(AREquinoViewModel.class);
        viewModel.cargarPorMicrochip(microchip);

        // Fragmento AR
        arFragment = (ArFragment) getSupportFragmentManager()
                .findFragmentById(R.id.arFragment);



        // Cuando Room devuelva el caballo, arrancar la tarjeta AR
        viewModel.getEquinoSeleccionado().observe(this, equino -> {
            if (equino == null) {
                Toast.makeText(this,
                        "Caballo no encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            arFragment.getArSceneView().getScene()
                    .addOnUpdateListener(frameTime -> {
                        if (!tarjetaCreada) {
                            tarjetaCreada = true;
                            crearTarjetaAR(equino);
                        } else {
                            actualizarPosicion();
                        }
                    });
        });
    }

    // -----------------------------------------------------------
    // Calcula la edad en años a partir de "YYYY-MM-DD"
    // -----------------------------------------------------------
    private int calcularEdad(String fechaNacimiento) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate nacimiento  = LocalDate.parse(fechaNacimiento, fmt);
            return Period.between(nacimiento, LocalDate.now()).getYears();
        } catch (Exception e) {
            Log.e(TAG, "Error al calcular edad", e);
            return 0;
        }
    }

    // -----------------------------------------------------------
    // Construye la tarjeta AR con los datos del equino
    // -----------------------------------------------------------
    private void crearTarjetaAR(Equino equino) {
        ViewRenderable.builder()
                .setView(this, R.layout.layout_tarjeta_ar)
                .build()
                .thenAccept(renderable -> {

                    android.view.View v = renderable.getView();

                    // Referencias a los TextViews
                    TextView tvNombre       = v.findViewById(R.id.tvNombre);
                    TextView tvRaza         = v.findViewById(R.id.tvRaza);
                    TextView tvEdad         = v.findViewById(R.id.tvEdad);
                    TextView tvSexo         = v.findViewById(R.id.tvSexo);
                    TextView tvAltura       = v.findViewById(R.id.tvAltura);
                    TextView tvPeso         = v.findViewById(R.id.tvPeso);
                    TextView tvTemperamento = v.findViewById(R.id.tvTemperamento);
                    TextView tvMicrochip    = v.findViewById(R.id.tvMicrochip);

                    // Rellenar datos
                    tvNombre.setText(equino.nombre);
                    tvRaza.setText(equino.raza);

                    // Edad calculada desde fechaNacimiento
                    int edad = calcularEdad(equino.fechaNacimiento);
                    tvEdad.setText(edad + " años");

                    // Sexo legible
                    tvSexo.setText(equino.sexo.equals(Equino.SEXO_MACHO)
                            ? "Macho" : "Hembra");

                    tvAltura.setText(equino.altura + " m");
                    tvPeso.setText(equino.peso + " kg");
                    tvTemperamento.setText(equino.temperamento);
                    tvMicrochip.setText("🔖 " + equino.numeroMicrochip);

                    // Crear nodo en la escena AR
                    nodoTarjeta = new Node();
                    nodoTarjeta.setRenderable(renderable);
                    nodoTarjeta.setParent(
                            arFragment.getArSceneView().getScene());

                    Log.d(TAG, "Tarjeta AR creada para: " + equino.nombre);
                })
                .exceptionally(error -> {
                    Log.e(TAG, "Error al crear tarjeta AR", error);
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "Error al mostrar AR", Toast.LENGTH_SHORT).show());
                    return null;
                });
    }

    // -----------------------------------------------------------
    // Mantiene la tarjeta delante de la cámara al moverse
    // -----------------------------------------------------------
    private void actualizarPosicion() {
        if (nodoTarjeta == null) return;

        com.google.ar.sceneform.Camera cam =
                arFragment.getArSceneView().getScene().getCamera();

        // ✅ 1.5m adelante — más lejos del móvil
        Vector3 pos = Vector3.add(
                cam.getWorldPosition(),
                cam.getForward().scaled(1.5f)
        );

        // ✅ 0.5m abajo — más centrada en pantalla
        pos = Vector3.add(pos, cam.getDown().scaled(0.5f));

        nodoTarjeta.setWorldPosition(pos);
        nodoTarjeta.setLookDirection(
                Vector3.subtract(cam.getWorldPosition(), pos));
    }
}