package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.viewmodel.AltaCuadraViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class AltaCuadraActivity extends AppCompatActivity {

    private AltaCuadraViewModel viewModel;
    private TextInputEditText etNumero;
    private TextInputLayout layNumero;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_cuadra);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(AltaCuadraViewModel.class);

        vincularVistas();
        observarViewModel();
    }

    private void vincularVistas() {
        etNumero = findViewById(R.id.etNumeroCuadra);
        layNumero = (TextInputLayout) etNumero.getParent().getParent(); // Para mostrar error en rojo
        progressBar = findViewById(R.id.progressBarCuadra); // Asegúrate de tenerlo en el XML

        MaterialButton btnGuardar = findViewById(R.id.btnGuardarCuadra);
        MaterialButton btnVolver = findViewById(R.id.btnVolverAltaCuadra);

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> validarYGuardar());
    }

    private void observarViewModel() {
        // Observar errores de base de datos (como duplicados)
        viewModel.getMensajeEstado().observe(this, mensaje -> {
            viewModel.finalizarOperacion(); // Detenemos el progreso

            if (mensaje != null) {
                // Si el mensaje contiene la palabra "existe", lo ponemos en el campo
                if (mensaje.contains("existe")) {
                    layNumero.setError(mensaje);
                } else {
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                }
            } else {
                // Éxito: mensaje es null
                Toast.makeText(this, "Cuadra guardada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // Observar estado de carga para mostrar/ocultar progress bar
        viewModel.getCargando().observe(this, estaCargando -> {
            if (progressBar != null) {
                progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            }
            findViewById(R.id.btnGuardarCuadra).setEnabled(!estaCargando);
        });
    }

    private void validarYGuardar() {
        String textoNumero = etNumero.getText().toString().trim();

        // Validación de formato (Local)
        if (textoNumero.isEmpty()) {
            layNumero.setError("Introduce un número de cuadra");
            return;
        }

        layNumero.setError(null); // Limpiamos error anterior
        viewModel.registrarCuadra(textoNumero);
    }
}