package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.util.SeleccionDisciplina;
import com.example.myhipicapptfg.viewmodel.AdminViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class AltaUsuarioActivity extends AppCompatActivity {

    private AdminViewModel viewModel;

    // UI Components
    private TextInputEditText etNombre, etApellido1, etApellido2, etDni, etEmail;
    private AutoCompleteTextView spinnerSexo, spinnerTipo;
    private AutoCompleteTextView spinnerNivelDoma, spinnerNivelSalto, spinnerNivelVaquera;
    private CheckBox cbDoma, cbSalto, cbVaquera;
    private LinearLayout layoutSeccionDisciplinas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_activity_alta_usuario);

        viewModel = new ViewModelProvider(this).get(AdminViewModel.class);

        vincularVistas();
        configurarSpinners();
        configurarListeners();
        observarEstadoRegistro();
    }

    private void vincularVistas() {
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etDni = findViewById(R.id.etDni);
        etEmail = findViewById(R.id.etEmail);
        spinnerSexo = findViewById(R.id.spinnerSexo);
        spinnerTipo = findViewById(R.id.spinnerTipo);
        layoutSeccionDisciplinas = findViewById(R.id.layoutSeccionDisciplinas);

        cbDoma = findViewById(R.id.cbDomaClasica);
        cbSalto = findViewById(R.id.cbSalto);
        cbVaquera = findViewById(R.id.cbDomaVaquera);

        spinnerNivelDoma = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto = findViewById(R.id.spinnerNivelSalto);
        spinnerNivelVaquera = findViewById(R.id.spinnerNivelVaquera);

        findViewById(R.id.btnGuardar).setOnClickListener(v -> decidirTipoDeRegistro());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        // Opciones de Sexo
        spinnerSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Masculino", "Femenino"}));

        // Opciones de Tipo de Usuario
        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Alumno", "Profesor", "Propietario"}));

        // Opciones de Niveles
        String[] niveles = {AlumnoDisciplina.PRINCIPIANTE, AlumnoDisciplina.INTERMEDIO, AlumnoDisciplina.AVANZADO};
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles);
        spinnerNivelDoma.setAdapter(adapterNiveles);
        spinnerNivelSalto.setAdapter(adapterNiveles);
        spinnerNivelVaquera.setAdapter(adapterNiveles);
    }

    private void configurarListeners() {
        // Mostrar disciplinas solo si es Alumno
        spinnerTipo.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = (String) parent.getItemAtPosition(position);
            layoutSeccionDisciplinas.setVisibility(seleccion.equals("Alumno") ? View.VISIBLE : View.GONE);
        });

        // Habilitar niveles solo si la disciplina está marcada
        cbDoma.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelDoma.setEnabled(isChecked));
        cbSalto.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelSalto.setEnabled(isChecked));
        cbVaquera.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelVaquera.setEnabled(isChecked));
    }

    /**
     * AQUÍ ESTÁ TU LÓGICA: Decide qué método del ViewModel llamar
     */
    private void decidirTipoDeRegistro() {
        if (!validarCampos()) return;

        // Crear objeto usuario con los datos comunes
        Usuario u = new Usuario();
        u.nombre = etNombre.getText().toString().trim();
        u.apellido1 = etApellido1.getText().toString().trim();
        u.apellido2 = etApellido2.getText().toString().trim();
        u.dni = etDni.getText().toString().trim().toUpperCase();
        u.email = etEmail.getText().toString().trim();
        u.sexo = spinnerSexo.getText().toString().equals("Masculino") ? "M" : "F";
        u.tipo = spinnerTipo.getText().toString();

        // Ejecutar el método correspondiente según lo que el usuario pulsó/eligió
        switch (u.tipo) {
            case "Alumno":
                List<SeleccionDisciplina> disciplinas = obtenerDisciplinasSeleccionadas();
                if (disciplinas.isEmpty()) {
                    Toast.makeText(this, "Selecciona al menos una disciplina para el alumno", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.registrarAlumno(u, disciplinas);
                break;

            case "Profesor":
                viewModel.registrarProfesor(u);
                break;

            case "Propietario":
                viewModel.registrarPropietario(u);
                break;
        }
    }

    private List<SeleccionDisciplina> obtenerDisciplinasSeleccionadas() {
        List<SeleccionDisciplina> lista = new ArrayList<>();
        // IDs: 1: Doma, 2: Salto, 3: Vaquera (ajustar según tu BD)
        if (cbDoma.isChecked()) lista.add(new SeleccionDisciplina(1, spinnerNivelDoma.getText().toString()));
        if (cbSalto.isChecked()) lista.add(new SeleccionDisciplina(2, spinnerNivelSalto.getText().toString()));
        if (cbVaquera.isChecked()) lista.add(new SeleccionDisciplina(3, spinnerNivelVaquera.getText().toString()));
        return lista;
    }

    private boolean validarCampos() {
        String nombre = etNombre.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String tipo = spinnerTipo.getText().toString();

        // 1. Verificar campos vacíos obligatorios
        if (nombre.isEmpty() || dni.isEmpty() || tipo.isEmpty()) {
            Toast.makeText(this, "Nombre, DNI y Tipo son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }

        // 2. Validar formato DNI (8 números y 1 letra)
        // El RegEx ^[0-9]{8}[A-Z]$ significa: 8 dígitos y una letra al final
        if (!dni.matches("^[0-9]{8}[A-Z]$")) {
            etDni.setError("Formato de DNI incorrecto (Ej: 12345678Z)");
            return false;
        }

        // 3. Validar formato Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Correo electrónico no válido");
            return false;
        }

        return true;
    }

    private void observarEstadoRegistro() {
        viewModel.getEstadoFormulario().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                if (mensaje.startsWith("Éxito")) {
                    finish(); // Cerramos la actividad al terminar con éxito
                }
            }
        });
    }
}