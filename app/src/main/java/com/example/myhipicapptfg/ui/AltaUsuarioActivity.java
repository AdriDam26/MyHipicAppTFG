package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import com.example.myhipicapptfg.util.SeleccionDisciplina;
import com.example.myhipicapptfg.viewmodel.AltaUsuarioViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class AltaUsuarioActivity extends AppCompatActivity {

    private AltaUsuarioViewModel viewModel;

    // Componentes UI
    private TextInputEditText etNombre, etApellido1, etApellido2, etDni, etEmail;
    private TextInputLayout layNombre, layDni, layEmail, layTipo;
    private AutoCompleteTextView spinnerSexo, spinnerTipo;
    private CheckBox cbDoma, cbSalto, cbVaquera;
    private AutoCompleteTextView spinnerNivelDoma, spinnerNivelSalto, spinnerNivelVaquera;
    private TextInputLayout layoutNivelDoma, layoutNivelSalto, layoutNivelVaquera;
    private LinearLayout layoutSeccionDisciplinas;
    private TextView tvTituloDisciplinas;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        viewModel = new ViewModelProvider(this).get(AltaUsuarioViewModel.class);

        vincularVistas();
        configurarComponentes();
        observarViewModel();
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
        progressBar = findViewById(R.id.progressBar);

        // Referencias a los Layouts para gestionar errores visuales
        layNombre = (TextInputLayout) etNombre.getParent().getParent();
        layDni = (TextInputLayout) etDni.getParent().getParent();
        layEmail = (TextInputLayout) etEmail.getParent().getParent();
        layTipo = (TextInputLayout) spinnerTipo.getParent().getParent();

        cbDoma = findViewById(R.id.cbDomaClasica);
        cbSalto = findViewById(R.id.cbSalto);
        cbVaquera = findViewById(R.id.cbDomaVaquera);

        spinnerNivelDoma = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto = findViewById(R.id.spinnerNivelSalto);
        spinnerNivelVaquera = findViewById(R.id.spinnerNivelVaquera);

        layoutNivelDoma = (TextInputLayout) spinnerNivelDoma.getParent().getParent();
        layoutNivelSalto = (TextInputLayout) spinnerNivelSalto.getParent().getParent();
        layoutNivelVaquera = (TextInputLayout) spinnerNivelVaquera.getParent().getParent();

        findViewById(R.id.btnGuardar).setOnClickListener(v -> recolectarYEnviar());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
    }

    private void observarViewModel() {
        viewModel.getMensajeEstado().observe(this, mensaje -> {
            if (mensaje != null) {
                // Si el error viene de la DB (duplicados), lo asignamos al campo
                if (mensaje.contains("DNI ya está registrado")) {
                    layDni.setError(mensaje);
                } else if (mensaje.contains("email ya está registrado")) {
                    layEmail.setError(mensaje);
                } else {
                    // Para otros mensajes (éxito o errores de lógica) usamos Toast
                    Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                    if (mensaje.startsWith("Éxito")) {
                        finish();
                    }
                }
            }
        });

        viewModel.getCargando().observe(this, estaCargando -> {
            progressBar.setVisibility(estaCargando ? View.VISIBLE : View.GONE);
            findViewById(R.id.btnGuardar).setEnabled(!estaCargando);
        });
    }

    private void recolectarYEnviar() {
        // 1. Resetear errores visuales
        layNombre.setError(null);
        layDni.setError(null);
        layEmail.setError(null);
        layTipo.setError(null);

        // 2. Extraer strings
        String nombre = etNombre.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String sexo = spinnerSexo.getText().toString();
        String tipo = spinnerTipo.getText().toString();

        // 3. Validaciones de Formato y Campos Vacíos (UI)
        boolean hayError = false;

        if (nombre.isEmpty()) {
            layNombre.setError("El nombre es obligatorio");
            hayError = true;
        }

        // Validación 8 números y 1 letra
        if (!dni.matches("\\d{8}[A-Za-z]")) {
            layDni.setError("Formato incorrecto (8 números y 1 letra)");
            hayError = true;
        }

        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layEmail.setError("Introduce un email válido");
            hayError = true;
        }

        if (tipo.isEmpty()) {
            layTipo.setError("Selecciona un tipo de usuario");
            hayError = true;
        }

        if (hayError) return;

        // 4. Obtener disciplinas
        List<SeleccionDisciplina> selecciones = new ArrayList<>();
        if (cbDoma.isChecked())
            selecciones.add(new SeleccionDisciplina(1, spinnerNivelDoma.getText().toString()));
        if (cbSalto.isChecked())
            selecciones.add(new SeleccionDisciplina(2, spinnerNivelSalto.getText().toString()));
        if (cbVaquera.isChecked())
            selecciones.add(new SeleccionDisciplina(3, spinnerNivelVaquera.getText().toString()));

        // 5. Enviar al ViewModel (para validación de negocio y BD)
        viewModel.registrarNuevoUsuario(nombre, ap1, ap2, dni, email, sexo, tipo, selecciones);
    }

    private void configurarComponentes() {
        spinnerSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Masculino", "Femenino"}));
        spinnerTipo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, new String[]{"Alumno", "Profesor", "Propietario"}));

        String[] niveles = {AlumnoDisciplina.PRINCIPIANTE, AlumnoDisciplina.INTERMEDIO, AlumnoDisciplina.AVANZADO};
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles);
        spinnerNivelDoma.setAdapter(adapterNiveles);
        spinnerNivelSalto.setAdapter(adapterNiveles);
        spinnerNivelVaquera.setAdapter(adapterNiveles);

        spinnerTipo.setOnItemClickListener((parent, view, position, id) -> {
            layTipo.setError(null); // Limpiar error al seleccionar
            String seleccion = (String) parent.getItemAtPosition(position);

            if (seleccion.equals("Propietario")) {
                layoutSeccionDisciplinas.setVisibility(View.GONE);
            } else {
                layoutSeccionDisciplinas.setVisibility(View.VISIBLE);
                boolean esAlumno = seleccion.equals("Alumno");
                int visibilidadNivel = esAlumno ? View.VISIBLE : View.GONE;
                layoutNivelDoma.setVisibility(visibilidadNivel);
                layoutNivelSalto.setVisibility(visibilidadNivel);
                layoutNivelVaquera.setVisibility(visibilidadNivel);
                tvTituloDisciplinas.setText(esAlumno ? "Disciplinas y Niveles" : "Disciplinas que enseña");
            }
        });

        cbDoma.setOnCheckedChangeListener((v, checked) -> spinnerNivelDoma.setEnabled(checked));
        cbSalto.setOnCheckedChangeListener((v, checked) -> spinnerNivelSalto.setEnabled(checked));
        cbVaquera.setOnCheckedChangeListener((v, checked) -> spinnerNivelVaquera.setEnabled(checked));
    }
}