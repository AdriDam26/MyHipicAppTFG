package com.example.myhipicapptfg.ui.ar.ar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.google.android.material.chip.Chip;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;


/**
 * Activity encargada de mostrar la información de un caballo en
 * Realidad Aumentada.
 *
 * Recibe un microchip desde el escáner QR, obtiene los datos del
 * caballo mediante el ViewModel y genera una tarjeta 3D en la escena
 * AR utilizando Sceneform.
 *
 * La tarjeta se mantiene siempre frente a la cámara del usuario y se
 * actualiza dinámicamente.
 */
public class ARActivity extends AppCompatActivity {


    /**
     * Fragmento de Sceneform que gestiona la escena de realidad aumentada.
     */
    private ArFragment arFragment;
    /**
     * Fragmento de Sceneform que gestiona la escena de realidad aumentada.
     */
    private AREquinoViewModel viewModel;
    /**
     * Nodo 3D que representa la tarjeta flotante en la escena AR.
     */
    private Node nodoTarjeta;
    /**
     * Controla que la tarjeta solo se cree una vez.
     */
    private boolean tarjetaCreada = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar);

        // Botón para volver atrás
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());

        // Obtener el microchip enviado desde el escáner QR
        String microchip = getIntent().getStringExtra("MICROCHIP");

        // Validación del microchip
        if (microchip == null || microchip.isEmpty()) {
            Toast.makeText(this, "QR inválido", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AREquinoViewModel.class);

        // Cargar datos del caballo a partir del microchip
        viewModel.cargarPorMicrochip(microchip);

        // Inicializar fragmento de realidad aumentada
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        /**
         * Observa el caballo obtenido desde la base de datos.
         * Cuando llega el dato, se observa también el propietario
         * y se inicializa la escena AR.
         */
        viewModel.getEquinoSeleccionado().observe(this, equino -> {
            // Si no existe el caballo, se muestra error y se cierra
            if (equino == null) {
                Toast.makeText(this, "Caballo no encontrado", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            /**
             * Observa el nombre del propietario del caballo.
             */
            viewModel.getNombrePropietario().observe(this, nombreProp -> {
                /**
                 * Se añade un listener que se ejecuta en cada frame
                 * de la escena AR.
                 */
                arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
                    // Crear la tarjeta solo una vez
                    if (!tarjetaCreada) {
                        tarjetaCreada = true;
                        crearTarjetaAR(equino, nombreProp != null ? nombreProp : "Hípica");
                    } else {
                        // Mantener la tarjeta frente a la cámara
                        actualizarPosicion();
                    }
                });
            });
        });
    }

    /**
     * Calcula la edad del caballo a partir de su fecha de nacimiento.
     */
    private int calcularEdad(long fechaNacimiento) {
        if (fechaNacimiento <= 0) {
            return 0;
        }
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(fechaNacimiento);
            LocalDate nacimiento = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));
            return Period.between(nacimiento, LocalDate.now()).getYears();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Crea la tarjeta 3D que se mostrará en la escena AR.
     */
    private void crearTarjetaAR(Equino equino, String nombrePropietario) {
        ViewRenderable.builder()
                .setView(this, R.layout.layout_tarjeta_ar)
                .build()
                .thenAccept(renderable -> {

                    // Obtener la vista del layout en 3D
                    View v = renderable.getView();

                    // Referencias UI dentro de la tarjeta
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

                    // Asignación de datos del caballo
                    tvNombre.setText(equino.nombre.toUpperCase());
                    tvRaza.setText(equino.raza);
                    tvEdad.setText(calcularEdad(equino.fechaNacimiento) + " años");
                    tvPeso.setText((int)equino.peso + " kg");
                    tvAltura.setText(String.format("%.2f m", equino.altura));
                    tvSexo.setText(equino.sexo.equals(Equino.SEXO_MACHO) ? "Macho" : "Hembra");
                    tvTemperamento.setText(equino.temperamento);
                    tvMicrochip.setText("SN: " + equino.numeroMicrochip);
                    tvPropietario.setText("Propietario: " + nombrePropietario);

                    // Estado de salud con color dinámico
                    tvSalud.setText(equino.estadoSalud.toUpperCase());
                    android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
                    gd.setCornerRadius(20f);
                    int colorS = equino.estadoSalud.equalsIgnoreCase(Equino.BUENO) ? 0xFF4CAF50 : (equino.estadoSalud.equalsIgnoreCase(Equino.REGULAR) ? 0xFFFF9800 : 0xFFF44336);
                    gd.setColor(colorS);
                    tvSalud.setBackground(gd);

                    // Mostrar chips según habilidades
                    chipSalto.setVisibility(equino.sabeSalto ? View.VISIBLE : View.GONE);
                    chipDoma.setVisibility(equino.sabeDoma ? View.VISIBLE : View.GONE);

                    // Cargar imagen del caballo
                    ImageView ivFoto = v.findViewById(R.id.ivFotoEquino);

                    if (equino.fotoPerfil != null && !equino.fotoPerfil.isEmpty()) {
                        Glide.with(this)
                                .load(equino.fotoPerfil)
                                .centerCrop()
                                .placeholder(R.drawable.ic_horse_placeholder)
                                .error(R.drawable.ic_horse_placeholder)
                                .into(ivFoto);
                    }

                    // Crear nodo 3D
                    nodoTarjeta = new Node();
                    nodoTarjeta.setRenderable(renderable);
                    nodoTarjeta.setParent(arFragment.getArSceneView().getScene());

                    // Escala del objeto en el mundo AR
                    nodoTarjeta.setLocalScale(new Vector3(0.65f, 0.65f, 0.65f));

                    Log.d("ARActivity", "Tarjeta AR creada");
                })
                .exceptionally(error -> {
                    Log.e("ARActivity", "Error al crear tarjeta AR", error);
                    return null;
                });
    }

    /**
     * Mantiene la tarjeta siempre delante de la cámara del usuario.
     */
    private void actualizarPosicion() {
        if (nodoTarjeta == null) {
            return;
        }
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