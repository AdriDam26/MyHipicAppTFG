package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.repository.PistaRepository;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AltaPistaActivity extends AppCompatActivity {

    private PistaRepository repository;
    private TextInputEditText etNombre, etAncho, etLargo;
    private AutoCompleteTextView spinnerEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_pista);

        repository = new PistaRepository(getApplication());

        // Referencias
        etNombre = findViewById(R.id.etNombrePista);
        etAncho = findViewById(R.id.etAnchoPista);
        etLargo = findViewById(R.id.etLargoPista);
        spinnerEstado = findViewById(R.id.spinnerEstadoPista);
        MaterialButton btnGuardar = findViewById(R.id.btnGuardarPista);
        MaterialButton btnVolver = findViewById(R.id.btnVolverAltaPista);

        // Configurar Desplegable de Estados (Usando las constantes de tu Entidad)
        String[] estados = {Pista.DISPONIBLE, Pista.MANTENIMIENTO, Pista.CERRADA};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estados);
        spinnerEstado.setAdapter(adapter);
        spinnerEstado.setText(Pista.DISPONIBLE, false); // Valor por defecto

        btnVolver.setOnClickListener(v -> finish());
        btnGuardar.setOnClickListener(v -> guardarPista());

        // Observar errores del Repositorio (Nombre duplicado, etc.)
        repository.getErrorLiveData().observe(this, error -> {
            if (error != null) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Pista guardada con éxito", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void guardarPista() {
        String nombre = etNombre.getText().toString().trim();
        String anchoStr = etAncho.getText().toString().trim();
        String largoStr = etLargo.getText().toString().trim();
        String estado = spinnerEstado.getText().toString();

        if (nombre.isEmpty() || anchoStr.isEmpty() || largoStr.isEmpty()) {
            Toast.makeText(this, "Por favor, rellena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Pista nuevaPista = new Pista();
        nuevaPista.nombre = nombre;
        nuevaPista.ancho = Double.parseDouble(anchoStr);
        nuevaPista.largo = Double.parseDouble(largoStr);
        nuevaPista.estado = estado;

        repository.insertarPista(nuevaPista);
    }
}