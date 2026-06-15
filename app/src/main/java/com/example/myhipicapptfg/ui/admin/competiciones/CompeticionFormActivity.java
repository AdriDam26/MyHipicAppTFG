package com.example.myhipicapptfg.ui.admin.competiciones;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;


/**
 * Activity para crear o editar una Competición.
 *
 * Funcionalidad:
 * - Crear nueva competición
 * - Editar competición existente
 * - Seleccionar fecha mediante DatePicker
 * - Validar campos
 * - Mostrar estado de operación desde ViewModel
 */
public class CompeticionFormActivity extends AppCompatActivity {

    // ViewModel de competiciones
    private GestionCompeticionesViewModel viewModel;

    // Campos de UI
    private TextInputEditText etNombre, etFecha;
    private TextInputLayout layNombre, layFecha;
    private ProgressBar progressBar;

    // Estado de la competición
    private long fechaSeleccionada = -1;
    private int competicionId = -1;
    private boolean modoEdicion = false;
    private Competicion competicionActual; // objeto cargado en edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_competicion_form);

        // Inicializa ViewModel
        viewModel = new ViewModelProvider(this).get(GestionCompeticionesViewModel.class);

        initViews(); // Vincula XML con código
        setupFechaPicker(); // Configura selector de fecha

        // Verificar si es edición
        if (getIntent().hasExtra("ID_COMPETICION")) {
            competicionId = getIntent().getIntExtra("ID_COMPETICION", -1);
            modoEdicion = true;
            cargarDatos();
        }

        // Botón guardar
        findViewById(R.id.btnGuardarCompeticion).setOnClickListener(v -> guardar());

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbarCompeticionForm);

        // Configura la acción para ir hacia atrás al presionar la flecha
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Observa resultados del ViewModel
        observarEstado();
    }

    /**
     * Vincula vistas del layout
     */
    private void initViews() {
        etNombre = findViewById(R.id.etNombreCompeticion);
        etFecha = findViewById(R.id.etFechaCompeticion);
        layNombre = findViewById(R.id.layNombreCompeticion);
        layFecha = findViewById(R.id.layFechaCompeticion);
        progressBar = findViewById(R.id.progressBarCompeticion);
    }

    /**
     * Carga datos de la competición si estamos en modo edición
     */
    private void cargarDatos() {
        // Buscamos la competición en el ViewModel
        viewModel.getCompeticiones().observe(this, lista -> {
            for (Competicion c : lista) {
                if (c.idCompeticion == competicionId) {
                    competicionActual = c;
                    etNombre.setText(c.nombre);
                    fechaSeleccionada = c.fecha;

                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(c.fecha);
                    etFecha.setText(cal.get(Calendar.DAY_OF_MONTH) + "/" +
                            (cal.get(Calendar.MONTH) + 1) + "/" +
                            cal.get(Calendar.YEAR));
                    break;
                }
            }
        });
    }

    /**
     * Configura el selector de fecha (DatePickerDialog)
     */
    private void setupFechaPicker() {
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (fechaSeleccionada > 0) c.setTimeInMillis(fechaSeleccionada);

            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar f = Calendar.getInstance();
                f.set(year, month, day);
                fechaSeleccionada = f.getTimeInMillis();
                etFecha.setText(day + "/" + (month + 1) + "/" + year);
                layFecha.setError(null);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    /**
     * Valida y guarda la competición (crear o actualizar)
     */
    private void guardar() {
        String nombre = etNombre.getText().toString().trim();

        if (nombre.isEmpty()) {
            layNombre.setError("Obligatorio");
            return;
        }
        if (fechaSeleccionada <= 0) {
            layFecha.setError("Selecciona una fecha");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (modoEdicion) {
            competicionActual.nombre = nombre;
            competicionActual.fecha = fechaSeleccionada;
            viewModel.actualizar(competicionActual);
        } else {
            viewModel.insertar(new Competicion(nombre, fechaSeleccionada));
        }
    }

    /**
     * Observa el estado del ViewModel (resultado de operaciones)
     */
    private void observarEstado() {
        viewModel.getEstado().observe(this, estado -> {
            if (estado == null) return;
            progressBar.setVisibility(View.GONE);

            switch (estado) {
                case "EXITO":
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case "ERROR_NOMBRE_DUPLICADO":
                    layNombre.setError("Este nombre ya existe");
                    break;
                case "ERROR_BD":
                    Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }
}