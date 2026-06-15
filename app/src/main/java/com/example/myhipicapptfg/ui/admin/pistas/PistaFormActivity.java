package com.example.myhipicapptfg.ui.admin.pistas;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

/**
 * Activity encargada de crear y editar una Pista.
 *
 * Funcionalidades principales:
 * - Introducción y validación de los datos de la pista
 *   (nombre, ancho y largo).
 * - Carga de datos en modo edición a partir del ID recibido.
 * - Persistencia mediante ViewModel (MVVM).
 */
public class PistaFormActivity extends AppCompatActivity {



    private GestionPistaViewModel viewModel;


    private TextInputLayout   layoutNombre;
    private TextInputLayout   layoutAncho;
    private TextInputLayout   layoutLargo;
    private TextInputEditText etNombre;
    private TextInputEditText etAncho;
    private TextInputEditText etLargo;
    private View              progressBar;


    /** Identificador de la pista en modo edición, o -1 en modo creación. */
    private int idPistaEditar = -1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pista_form);

        viewModel = new ViewModelProvider(this)
                .get(GestionPistaViewModel.class);

        initViews();
        setupToolbar();
        setupBotones();
        observarEstado();
        recogerExtras();
    }


    /**
     * Inicializa las referencias a las vistas del layout.
     */
    private void initViews() {
        layoutNombre = findViewById(R.id.layoutNombrePista);
        layoutAncho  = findViewById(R.id.layoutAnchoPista);
        layoutLargo  = findViewById(R.id.layoutLargoPista);
        etNombre     = findViewById(R.id.etNombrePista);
        etAncho      = findViewById(R.id.etAnchoPista);
        etLargo      = findViewById(R.id.etLargoPista);
        progressBar  = findViewById(R.id.progressBarPista);
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarPistaForm);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Asigna el listener al botón de guardar.
     */
    private void setupBotones() {
        findViewById(R.id.btnGuardarPista)
                .setOnClickListener(v -> guardar());
    }

    /**
     * Lee los extras del Intent y, si corresponde a modo edición,
     * carga los datos de la pista en el formulario.
     */
    private void recogerExtras() {

        if (getIntent().hasExtra("ID_PISTA")) {
            idPistaEditar = getIntent().getIntExtra("ID_PISTA", -1);
            cargarDatos(idPistaEditar);
        }
    }



    /**
     * Observa la pista por su ID y rellena los campos del formulario
     * con sus datos actuales.
     *
     * @param id Identificador de la pista a cargar.
     */
    private void cargarDatos(int id) {

        viewModel.buscarPorId(id).observe(this, pista -> {

            if (pista == null) return;

            etNombre.setText(pista.nombre);
            etAncho.setText(String.valueOf(pista.ancho));
            etLargo.setText(String.valueOf(pista.largo));
        });
    }


    /**
     * Observa el estado de la última operación de guardado.
     *
     * Oculta el indicador de progreso y muestra mensajes de
     * éxito o error según el resultado. En caso de éxito cierra
     * la Activity.
     */
    private void observarEstado() {

        viewModel.getEstadoOperacion().observe(this, estado -> {

            progressBar.setVisibility(View.GONE);

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_NOMBRE_DUPLICADO":
                    layoutNombre.setError(
                            "Ya existe una pista con ese nombre"
                    );
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error en base de datos",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }


    /**
     * Valida el formulario, construye el objeto Pista con los
     * datos introducidos y delega el guardado al ViewModel.
     *
     * Muestra el indicador de progreso durante la operación.
     * En modo edición actualiza la pista existente.
     * En modo creación inserta una nueva.
     */
    private void guardar() {

        if (!validar()) {
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        double ancho;
        double largo;

        try {
            ancho = Double.parseDouble(etAncho.getText().toString().trim());
            largo = Double.parseDouble(etLargo.getText().toString().trim());
        } catch (NumberFormatException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this,
                    "Introduce números válidos",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Pista pista = new Pista(
                etNombre.getText().toString().trim(),
                ancho,
                largo
        );

        if (idPistaEditar > 0) {
            pista.idPista = idPistaEditar;
            viewModel.actualizar(pista);
        } else {
            viewModel.insertar(pista);
        }
    }


    /**
     * Valida que todos los campos obligatorios del formulario
     * estén rellenos antes de intentar guardar.
     *
     * Comprobaciones realizadas:
     * - Nombre de la pista (obligatorio).
     * - Ancho de la pista (obligatorio).
     * - Largo de la pista (obligatorio).
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validar() {

        boolean ok = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            layoutNombre.setError("Obligatorio");
            ok = false;
        } else {
            layoutNombre.setError(null);
        }

        if (etAncho.getText().toString().trim().isEmpty()) {
            layoutAncho.setError("Obligatorio");
            ok = false;
        } else {
            layoutAncho.setError(null);
        }

        if (etLargo.getText().toString().trim().isEmpty()) {
            layoutLargo.setError("Obligatorio");
            ok = false;
        } else {
            layoutLargo.setError(null);
        }

        return ok;
    }
}