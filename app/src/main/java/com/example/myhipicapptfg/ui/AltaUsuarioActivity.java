package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView; // Añadido
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.util.SeleccionDisciplina;
import com.example.myhipicapptfg.viewmodel.AdminViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AltaUsuarioActivity extends AppCompatActivity {

    private AdminViewModel viewModel;

    // Componentes UI
    private TextInputEditText etNombre, etApellido1, etApellido2, etDni, etEmail;
    private AutoCompleteTextView spinnerSexo, spinnerTipo;
    private AutoCompleteTextView spinnerNivelDoma, spinnerNivelSalto, spinnerNivelVaquera;
    private TextInputLayout layoutNivelDoma, layoutNivelSalto, layoutNivelVaquera;
    private CheckBox cbDoma, cbSalto, cbVaquera;
    private LinearLayout layoutSeccionDisciplinas;
    private TextView tvTituloDisciplinas; // Añadido para el título dinámico

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
        tvTituloDisciplinas = findViewById(R.id.tvTituloDisciplinas);

        cbDoma = findViewById(R.id.cbDomaClasica);
        cbSalto = findViewById(R.id.cbSalto);
        cbVaquera = findViewById(R.id.cbDomaVaquera);

        spinnerNivelDoma = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto = findViewById(R.id.spinnerNivelSalto);
        spinnerNivelVaquera = findViewById(R.id.spinnerNivelVaquera);

        // Accedemos al contenedor (TextInputLayout) para ocultar todo el bloque del nivel
        layoutNivelDoma = (TextInputLayout) spinnerNivelDoma.getParent().getParent();
        layoutNivelSalto = (TextInputLayout) spinnerNivelSalto.getParent().getParent();
        layoutNivelVaquera = (TextInputLayout) spinnerNivelVaquera.getParent().getParent();

        findViewById(R.id.btnGuardar).setOnClickListener(v -> decidirTipoDeRegistro());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void configurarSpinners() {
        spinnerSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Masculino", "Femenino"}));
        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Alumno", "Profesor", "Propietario"}));

        String[] niveles = {AlumnoDisciplina.PRINCIPIANTE, AlumnoDisciplina.INTERMEDIO, AlumnoDisciplina.AVANZADO};
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles);
        spinnerNivelDoma.setAdapter(adapterNiveles);
        spinnerNivelSalto.setAdapter(adapterNiveles);
        spinnerNivelVaquera.setAdapter(adapterNiveles);
    }

    private void configurarListeners() {
        spinnerTipo.setOnItemClickListener((parent, view, position, id) -> {
            String seleccion = (String) parent.getItemAtPosition(position);

            if (seleccion.equals("Alumno")) {
                layoutSeccionDisciplinas.setVisibility(View.VISIBLE);
                tvTituloDisciplinas.setText("Selecciona Disciplinas y Niveles"); // Título para Alumno
                setVisibilidadNiveles(View.VISIBLE);
            } else if (seleccion.equals("Profesor")) {
                layoutSeccionDisciplinas.setVisibility(View.VISIBLE);
                tvTituloDisciplinas.setText("Selecciona Disciplinas que sabe enseñar"); // Título para Profesor
                setVisibilidadNiveles(View.GONE); // Ocultamos los selectores de nivel
            } else {
                layoutSeccionDisciplinas.setVisibility(View.GONE);
            }
        });

        // Habilitar spinners de nivel solo si el checkbox está marcado
        cbDoma.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelDoma.setEnabled(isChecked));
        cbSalto.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelSalto.setEnabled(isChecked));
        cbVaquera.setOnCheckedChangeListener((v, isChecked) -> spinnerNivelVaquera.setEnabled(isChecked));
    }

    private void setVisibilidadNiveles(int visibilidad) {
        layoutNivelDoma.setVisibility(visibilidad);
        layoutNivelSalto.setVisibility(visibilidad);
        layoutNivelVaquera.setVisibility(visibilidad);
    }

    private void decidirTipoDeRegistro() {
        if (!validarCampos()) return;

        Usuario u = new Usuario();
        u.nombre = etNombre.getText().toString().trim();
        u.apellido1 = etApellido1.getText().toString().trim();
        u.apellido2 = etApellido2.getText().toString().trim();
        u.dni = etDni.getText().toString().trim().toUpperCase();
        u.email = etEmail.getText().toString().trim();
        u.sexo = spinnerSexo.getText().toString().equals("Masculino") ? "M" : "F";
        u.tipo = spinnerTipo.getText().toString();

        List<SeleccionDisciplina> disciplinas = obtenerDisciplinasSeleccionadas();

        switch (u.tipo) {
            case "Alumno":
                if (disciplinas.isEmpty()) {
                    Toast.makeText(this, "Selecciona al menos una disciplina y nivel", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.registrarAlumno(u, disciplinas);
                break;

            case "Profesor":
                if (disciplinas.isEmpty()) {
                    Toast.makeText(this, "Selecciona las disciplinas que enseña", Toast.LENGTH_SHORT).show();
                    return;
                }
                viewModel.registrarProfesor(u, disciplinas);
                break;

            case "Propietario":
                viewModel.registrarPropietario(u);
                break;
        }
    }

    private List<SeleccionDisciplina> obtenerDisciplinasSeleccionadas() {
        List<SeleccionDisciplina> lista = new ArrayList<>();
        // El ID 1, 2, 3 debe coincidir con los insertados en TestDatabase
        // Si es profesor, el nivel obtenido del spinner se enviará pero el Repo lo ignorará.
        if (cbDoma.isChecked()) lista.add(new SeleccionDisciplina(1, spinnerNivelDoma.getText().toString()));
        if (cbSalto.isChecked()) lista.add(new SeleccionDisciplina(2, spinnerNivelSalto.getText().toString()));
        if (cbVaquera.isChecked()) lista.add(new SeleccionDisciplina(3, spinnerNivelVaquera.getText().toString()));
        return lista;
    }

    private boolean validarCampos() {
        if (etNombre.getText().toString().isEmpty() || etDni.getText().toString().isEmpty() || spinnerTipo.getText().toString().isEmpty()) {
            Toast.makeText(this, "Nombre, DNI y Tipo son obligatorios", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void observarEstadoRegistro() {
        viewModel.getEstadoFormulario().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                if (mensaje.startsWith("Éxito")) finish();
            }
        });
    }
}