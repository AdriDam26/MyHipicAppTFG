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
    private TextView tvTituloDisciplinas, tvTituloPrincipal;
    private ProgressBar progressBar;
    private Button btnGuardar;

    // Control de estado
    private int usuarioIdEdicion = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_usuario);

        viewModel = new ViewModelProvider(this).get(AltaUsuarioViewModel.class);

        vincularVistas();
        configurarComponentes();
        observarViewModel();

        // 1. COMPROBAR SI ES UNA EDICIÓN (Viene de pulsar el lápiz)
        usuarioIdEdicion = getIntent().getIntExtra("USUARIO_ID", -1);
        if (usuarioIdEdicion != -1) {
            prepararModoEdicion(usuarioIdEdicion);
        }

        // 2. COMPROBAR SI EL TIPO VIENE FORZADO (Viene de una lista específica)
        String tipoForzado = getIntent().getStringExtra("TIPO_USUARIO");
        if (tipoForzado != null) {
            // Ocultamos el campo para que quede más limpio (UX)
            layTipo.setVisibility(View.GONE);

            // Asignamos el valor internamente para que la lógica funcione
            spinnerTipo.setText(tipoForzado, false);
            actualizarInterfazSegunTipo(tipoForzado);

            // Actualizamos el título de la pantalla
            if (tvTituloPrincipal != null) {
                tvTituloPrincipal.setText(usuarioIdEdicion != -1 ? "Editar " + tipoForzado : "Nuevo " + tipoForzado);
            }
        }
    }

    private void vincularVistas() {
        tvTituloPrincipal = findViewById(R.id.tvTituloPrincipal);
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
        btnGuardar = findViewById(R.id.btnGuardar);

        layNombre = findViewById(R.id.layNombre);
        layDni = findViewById(R.id.layDni);
        layEmail = findViewById(R.id.layEmail);
        layTipo = findViewById(R.id.layTipo);

        cbDoma = findViewById(R.id.cbDomaClasica);
        cbSalto = findViewById(R.id.cbSalto);
        cbVaquera = findViewById(R.id.cbDomaVaquera);

        spinnerNivelDoma = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto = findViewById(R.id.spinnerNivelSalto);
        spinnerNivelVaquera = findViewById(R.id.spinnerNivelVaquera);

        layoutNivelDoma = findViewById(R.id.layNivelDoma);
        layoutNivelSalto = findViewById(R.id.layNivelSalto);
        layoutNivelVaquera = findViewById(R.id.layNivelVaquera);

        btnGuardar.setOnClickListener(v -> recolectarYEnviar());
        findViewById(R.id.btnVolver).setOnClickListener(v -> finish());
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
            layTipo.setError(null);
            actualizarInterfazSegunTipo((String) parent.getItemAtPosition(position));
        });

        cbDoma.setOnCheckedChangeListener((v, checked) -> spinnerNivelDoma.setEnabled(checked));
        cbSalto.setOnCheckedChangeListener((v, checked) -> spinnerNivelSalto.setEnabled(checked));
        cbVaquera.setOnCheckedChangeListener((v, checked) -> spinnerNivelVaquera.setEnabled(checked));
    }

    private void actualizarInterfazSegunTipo(String seleccion) {
        if ("Propietario".equals(seleccion)) {
            layoutSeccionDisciplinas.setVisibility(View.GONE);
        } else {
            layoutSeccionDisciplinas.setVisibility(View.VISIBLE);
            boolean esAlumno = "Alumno".equals(seleccion);
            int visibilidadNivel = esAlumno ? View.VISIBLE : View.GONE;

            layoutNivelDoma.setVisibility(visibilidadNivel);
            layoutNivelSalto.setVisibility(visibilidadNivel);
            layoutNivelVaquera.setVisibility(visibilidadNivel);

            tvTituloDisciplinas.setText(esAlumno ? "Disciplinas y Niveles" : "Disciplinas que enseña");
        }
    }

    private void recolectarYEnviar() {
        // Reset errores
        layNombre.setError(null); layDni.setError(null); layEmail.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String ap1 = etApellido1.getText().toString().trim();
        String ap2 = etApellido2.getText().toString().trim();
        String dni = etDni.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String sexo = spinnerSexo.getText().toString();
        String tipo = spinnerTipo.getText().toString();

        if (validarCampos(nombre, dni, email, tipo)) {
            List<SeleccionDisciplina> selecciones = new ArrayList<>();
            if (cbDoma.isChecked()) selecciones.add(new SeleccionDisciplina(1, spinnerNivelDoma.getText().toString()));
            if (cbSalto.isChecked()) selecciones.add(new SeleccionDisciplina(2, spinnerNivelSalto.getText().toString()));
            if (cbVaquera.isChecked()) selecciones.add(new SeleccionDisciplina(3, spinnerNivelVaquera.getText().toString()));

            if (usuarioIdEdicion == -1) {
                // MODO ALTA
                viewModel.registrarNuevoUsuario(nombre, ap1, ap2, dni, email, sexo, tipo, selecciones);
            } else {
                // MODO ACTUALIZACIÓN (Debes tener este método en tu ViewModel)
                viewModel.actualizarUsuario(usuarioIdEdicion, nombre, ap1, ap2, dni, email, sexo, tipo, selecciones);
            }
        }
    }

    private boolean validarCampos(String n, String d, String e, String t) {
        boolean ok = true;
        if (n.isEmpty()) { layNombre.setError("Obligatorio"); ok = false; }
        if (!d.matches("\\d{8}[A-Za-z]")) { layDni.setError("DNI inválido"); ok = false; }
        if (e.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(e).matches()) { layEmail.setError("Email inválido"); ok = false; }
        if (t.isEmpty()) { layTipo.setError("Selecciona tipo"); ok = false; }
        return ok;
    }

    private void observarViewModel() {
        viewModel.getMensajeEstado().observe(this, mensaje -> {
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
                if (mensaje.startsWith("Éxito")) finish();
            }
        });

        viewModel.getCargando().observe(this, cargando -> {
            progressBar.setVisibility(cargando ? View.VISIBLE : View.GONE);
            btnGuardar.setEnabled(!cargando);
        });
    }

    private void prepararModoEdicion(int id) {
        if (tvTituloPrincipal != null) tvTituloPrincipal.setText("Editar Registro");
        btnGuardar.setText("ACTUALIZAR DATOS");

        // Aquí deberías llamar a un método del viewModel para cargar los datos actuales
        // viewModel.cargarDatosUsuario(id);
    }
}