package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.viewmodel.AltaPistaViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AltaPistaActivity extends AppCompatActivity {

    private AltaPistaViewModel viewModel;
    private TextInputEditText etNombre, etAncho, etLargo;
    private TextInputLayout layNombre, layAncho, layLargo;
    private AutoCompleteTextView spinnerEstado;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_pista);

        viewModel = new ViewModelProvider(this).get(AltaPistaViewModel.class);

        vincularVistas();
        configurarSpinner();
        observarViewModel();
    }

    private void vincularVistas() {
        etNombre = findViewById(R.id.etNombrePista);
        etAncho = findViewById(R.id.etAnchoPista);
        etLargo = findViewById(R.id.etLargoPista);
        spinnerEstado = findViewById(R.id.spinnerEstadoPista);
        progressBar = findViewById(R.id.progressBarPista);

        // Referencias a los layouts para errores en rojo
        layNombre = (TextInputLayout) etNombre.getParent().getParent();
        layAncho = (TextInputLayout) etAncho.getParent().getParent();
        layLargo = (TextInputLayout) etLargo.getParent().getParent();

        findViewById(R.id.btnVolverAltaPista).setOnClickListener(v -> finish());
        findViewById(R.id.btnGuardarPista).setOnClickListener(v -> validarYEnviar());
    }

    private void configurarSpinner() {
        String[] estados = {Pista.DISPONIBLE, Pista.MANTENIMIENTO, Pista.CERRADA};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados);
        spinnerEstado.setAdapter(adapter);
    }

    private void observarViewModel() {
        viewModel.getMensajeEstado().observe(this, mensaje -> {
            viewModel.finalizarOperacion();
            if (mensaje != null) {
                if (mensaje.contains("existe")) {
                    layNombre.setError(mensaje); // Error de nombre duplicado en rojo
                } else {
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Pista guardada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        viewModel.getCargando().observe(this, estaCargando -> {
            if (progressBar != null) progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            findViewById(R.id.btnGuardarPista).setEnabled(!estaCargando);
        });
    }

    private void validarYEnviar() {
        // Limpiar errores
        layNombre.setError(null);
        layAncho.setError(null);
        layLargo.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String ancho = etAncho.getText().toString().trim();
        String largo = etLargo.getText().toString().trim();
        String estado = spinnerEstado.getText().toString();

        boolean error = false;

        if (nombre.isEmpty()) { layNombre.setError("Campo obligatorio"); error = true; }
        if (ancho.isEmpty()) { layAncho.setError("Campo obligatorio"); error = true; }
        if (largo.isEmpty()) { layLargo.setError("Campo obligatorio"); error = true; }

        if (error) return;

        viewModel.registrarPista(nombre, ancho, largo, estado);
    }
}