package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.repository.CuadraRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AltaCuadraActivity extends AppCompatActivity {

    private CuadraRepository repository;
    private TextInputEditText etNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_cuadra);

        repository = new CuadraRepository(getApplication());
        etNumero = findViewById(R.id.etNumeroCuadra);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardarCuadra);
        MaterialButton btnVolver = findViewById(R.id.btnVolverAltaCuadra);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> guardarCuadra());

        // Observamos el mensaje de error/éxito que creaste en el Repositorio
        repository.getMensajeStatus().observe(this, mensaje -> {
            if (mensaje != null) {
                // Si hay mensaje, es un error (ej: duplicado)
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            } else {
                // Si es null, es que se insertó correctamente
                Toast.makeText(this, "Cuadra guardada con éxito", Toast.LENGTH_SHORT).show();
                finish(); // Volvemos atrás
            }
        });
    }

    private void guardarCuadra() {
        String textoNumero = etNumero.getText().toString().trim();

        if (textoNumero.isEmpty()) {
            etNumero.setError("Introduce un número");
            return;
        }

        Cuadra nuevaCuadra = new Cuadra();
        nuevaCuadra.numeroCuadra = Integer.parseInt(textoNumero);

        repository.insertar(nuevaCuadra);
    }
}