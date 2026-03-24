package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.AdminViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AltaUsuarioActivity extends AppCompatActivity {

    private AdminViewModel viewModel;

    // Referencias a los componentes de la UI
    private TextInputEditText etNombre, etApellido1, etApellido2, etDni, etEmail;
    private AutoCompleteTextView spinnerSexo, spinnerTipo; // Cambiado a AutoCompleteTextView para Material 3
    private MaterialButton btnGuardar, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_alta_usuario);

        // 1. Inicializar el ViewModel
        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        // 2. Vincular vistas
        inicializarVistas();

        // 3. Configurar los desplegables modernos
        configurarSpinners();

        // 4. Observar el estado de la operación
        observarViewModel();

        // 5. Eventos de botones
        btnGuardar.setOnClickListener(v -> realizarRegistro());
        btnVolver.setOnClickListener(v -> finish());
    }

    private void inicializarVistas() {
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etEmail);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);
    }

    private void configurarSpinners() {
        // Adaptador para Sexo
        String[] opcionesSexo = {"Masculino", "Femenino"};
        ArrayAdapter<String> adapterSexo = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, opcionesSexo);
        spinnerSexo.setAdapter(adapterSexo);

        // Adaptador para Tipo
        String[] opcionesTipo = {"Alumno", "Profesor", "Propietario"};
        ArrayAdapter<String> adapterTipo = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, opcionesTipo);
        spinnerTipo.setAdapter(adapterTipo);
    }

    private void observarViewModel() {
        viewModel.getEstadoFormulario().observe(this, mensaje -> {
            if (mensaje != null) {
                // Caso A: Todo ha ido perfecto
                if (mensaje.startsWith("Éxito")) {
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                    limpiarFormulario(); // <--- Ahora sí se ejecutará
                }
                // Caso B: Algo ha fallado (DNI, Email, etc.)
                else if (mensaje.startsWith("Error")) {
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();

                    // No limpiamos, solo marcamos el error donde toque
                    if (mensaje.contains("DNI")) etDni.setError("Ya existe");
                    if (mensaje.contains("email")) etEmail.setError("Ya existe");
                }
            }
        });
    }

    private void realizarRegistro() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim().toUpperCase();
        String email = etEmail.getText().toString().trim();
        String sSexo = spinnerSexo.getText().toString();
        String sTipo = spinnerTipo.getText().toString();

        // 1. Validaciones básicas
        if (nombre.isEmpty() || dni.isEmpty() || email.isEmpty() || sSexo.isEmpty() || sTipo.isEmpty()) {
            Toast.makeText(this, "Por favor, rellene todos los campos obligatorios", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validarDNI(dni)) {
            etDni.setError("Formato de DNI/NIE no válido");
            return;
        }

        // 2. Crear objeto Usuario y mapear valores
        Usuario u = new Usuario();
        u.nombre = nombre;
        u.apellido1 = etApellido1.getText().toString().trim();
        u.apellido2 = etApellido2.getText().toString().trim();
        u.dni = dni;
        u.email = email;

        // Mapeo de Sexo a constantes de la Entidad ("M"/"F")
        u.sexo = sSexo.equals("Masculino") ? Usuario.SEXO_MASCULINO : Usuario.SEXO_FEMENINO;

        // Mapeo de Tipo a constantes de la Entidad
        switch (sTipo) {
            case "Alumno": u.tipo = Usuario.TIPO_ALUMNO; break;
            case "Profesor": u.tipo = Usuario.TIPO_PROFESOR; break;
            case "Propietario": u.tipo = Usuario.TIPO_PROPIETARIO; break;
        }

        // 3. Enviar al ViewModel
        viewModel.registrarUsuarioCompleto(u);
    }

    private void limpiarFormulario() {
        etNombre.setText("");
        etApellido1.setText("");
        etApellido2.setText("");
        etDni.setText("");
        etEmail.setText("");
        spinnerSexo.setText("", false);
        spinnerTipo.setText("", false);
        etNombre.requestFocus();
    }

    private boolean validarDNI(String dni) {
        String regexDNI = "^([0-9]{8}[A-Z])|[XYZ][0-9]{7}[A-Z]$";
        return dni.matches(regexDNI);
    }
}