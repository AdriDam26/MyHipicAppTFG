package com.example.myhipicapptfg.ui.ar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.viewmodel.AREquinoViewModel;
import com.google.android.material.chip.Chip;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

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

        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        String microchip = getIntent().getStringExtra("MICROCHIP");
        if (microchip == null || microchip.isEmpty()) {
            Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        viewModel = new ViewModelProvider(this).get(AREquinoViewModel.class);
        viewModel.cargarPorMicrochip(microchip);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        viewModel.getEquinoSeleccionado().observe(this, equino -> {
            if (equino == null) {
                Toast.makeText(this, "Caballo no encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Aquí el UpdateListener actualiza la posición en cada frame
            arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                if (!tarjetaCreada) {
                    tarjetaCreada = true;
                    crearTarjetaAR(equino);
                } else {
                    actualizarPosicion();
                }
            });
        });
    }

    private int calcularEdad(long fechaNacimiento) {
        if (fechaNacimiento <= 0) return 0;
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fechaNacimiento);
            LocalDate nacimiento = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            return Period.between(nacimiento, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    private void crearTarjetaAR(Equino equino) {
        ViewRenderable.builder()
                .setView(this, R.layout.layout_tarjeta_ar)
                .build()
                .thenAccept(renderable -> {
                    View v = renderable.getView();

                    // Referencias y Datos
                    TextView tvNombre       = v.findViewById(R.id.tvNombre);
                    TextView tvSalud        = v.findViewById(R.id.tvSalud);
                    TextView tvRaza         = v.findViewById(R.id.tvRaza);
                    TextView tvPropietario  = v.findViewById(R.id.tvPropietario);
                    TextView tvEdad         = v.findViewById(R.id.tvEdad);
                    TextView tvPeso         = v.findViewById(R.id.tvPeso);
                    TextView tvSexo         = v.findViewById(R.id.tvSexo);
                    TextView tvAltura       = v.findViewById(R.id.tvAltura);
                    TextView tvTemperamento = v.findViewById(R.id.tvTemperamento);
                    TextView tvMicrochip    = v.findViewById(R.id.tvMicrochip);
                    Chip chipSalto          = v.findViewById(R.id.chipSalto);
                    Chip chipDoma           = v.findViewById(R.id.chipDoma);

                    tvNombre.setText(equino.nombre.toUpperCase());
                    tvRaza.setText(equino.raza);
                    tvEdad.setText(calcularEdad(equino.fechaNacimiento) + " años");
                    tvPeso.setText((int)equino.peso + " kg");
                    tvAltura.setText(String.format("%.2f m", equino.altura));
                    tvSexo.setText(equino.sexo.equals(Equino.SEXO_MACHO) ? "Macho" : "Hembra");
                    tvTemperamento.setText(equino.temperamento);
                    tvMicrochip.setText("SN: " + equino.numeroMicrochip);
                    tvPropietario.setText(equino.idUsuario != null ? "Propietario: ID #" + equino.idUsuario : "Propietario: Hipica");

                    // Color de salud
                    tvSalud.setText(equino.estadoSalud.toUpperCase());
                    android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                    gd.setCornerRadius(20f);
                    int colorS = equino.estadoSalud.equalsIgnoreCase(Equino.BUENO) ? 0xFF4CAF50 : (equino.estadoSalud.equalsIgnoreCase(Equino.REGULAR) ? 0xFFFF9800 : 0xFFF44336);
                    gd.setColor(colorS);
                    tvSalud.setBackground(gd);

                    chipSalto.setVisibility(equino.sabeSalto ? View.VISIBLE : View.GONE);
                    chipDoma.setVisibility(equino.sabeDoma ? View.VISIBLE : View.GONE);

                    // Nodo
                    nodoTarjeta = new Node();
                    nodoTarjeta.setRenderable(renderable);
                    nodoTarjeta.setParent(arFragment.getArSceneView().getScene());

                    // ESCALA EQUILIBRADA
                    nodoTarjeta.setLocalScale(new Vector3(0.65f, 0.65f, 0.65f));

                    Log.d(TAG, "Tarjeta AR creada (Modo Seguimiento)");
                })
                .exceptionally(error -> {
                    Log.e(TAG, "Error al crear tarjeta AR", error);
                    return null;
                });
    }

    private void actualizarPosicion() {
        if (nodoTarjeta == null) return;
        com.google.ar.sceneform.Camera cam = arFragment.getArSceneView().getScene().getCamera();

        // Mantiene la tarjeta a 1.2m siempre frente a la cámara
        Vector3 pos = Vector3.add(cam.getWorldPosition(), cam.getForward().scaled(1.2f));
        // La baja un poco (0.35m) para que no tape el centro de la pantalla
        pos = Vector3.add(pos, cam.getDown().scaled(0.35f));

        nodoTarjeta.setWorldPosition(pos);

        // Hace que la tarjeta mire siempre al usuario
        Vector3 cameraPos = cam.getWorldPosition();
        Vector3 direction = Vector3.subtract(cameraPos, pos);
        nodoTarjeta.setLookDirection(direction);
    }
}