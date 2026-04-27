package com.example.myhipicapptfg.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.GestionEquinoViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class EquinoFormActivity extends AppCompatActivity {

    private GestionEquinoViewModel viewModel;

    // Vistas principales
    private TextInputEditText etNombre, etMicrochip, etFechaNacimiento, etAltura, etPeso, etCuadra;
    private TextInputLayout layNombre, layMicrochip, layFechaNacimiento, layAltura, layPeso, layCuadra;

    private AutoCompleteTextView spinnerRaza, spinnerSexo, spinnerTemperamento, spinnerSalud, spinnerPropietario;
    private TextInputLayout layRaza, layPropietario;

    private View layoutEspecialidades;
    private CheckBox cbTieneEspecialidades, cbDoma, cbSalto;
    private CheckBox cbTienePropietario;

    // Estado y Edición
    private long fechaNacimientoSeleccionada = -1;
    private boolean modoEdicion = false;
    private int equinoId = -1;

    // Lista auxiliar para manejar los IDs de los propietarios
    private List<Usuario> listaPropietariosCargados = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equino_form);

        viewModel = new ViewModelProvider(this).get(GestionEquinoViewModel.class);

        initViews();
        setupSpinners();
        setupFechaNacimiento();
        setupCheckBoxListeners();
        observarEstadoRepository();

        // Verificar si es edición
        if (getIntent().hasExtra("ID_EQUINO")) {
            equinoId = getIntent().getIntExtra("ID_EQUINO", -1);
            modoEdicion = true;
            cargarEquino(equinoId);
        }

        findViewById(R.id.btnGuardarEquino).setOnClickListener(v -> guardarEquino());
        findViewById(R.id.btnVolverAltaEquino).setOnClickListener(v -> finish());
    }

    private void cargarEquino(int id) {
        viewModel.buscarPorId(id).observe(this, equino -> {
            if (equino == null) return;

            etNombre.setText(equino.nombre);
            etMicrochip.setText(equino.numeroMicrochip);
            etAltura.setText(String.valueOf(equino.altura));
            etPeso.setText(String.valueOf(equino.peso));
            etCuadra.setText(String.valueOf(equino.numeroCuadra));

            spinnerRaza.setText(equino.raza, false);
            spinnerSexo.setText(equino.sexo, false);
            spinnerTemperamento.setText(equino.temperamento, false);
            spinnerSalud.setText(equino.estadoSalud, false);

            fechaNacimientoSeleccionada = equino.fechaNacimiento;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(equino.fechaNacimiento);
            etFechaNacimiento.setText(c.get(Calendar.DAY_OF_MONTH) + "/" + (c.get(Calendar.MONTH) + 1) + "/" + c.get(Calendar.YEAR));

            cbDoma.setChecked(equino.sabeDoma);
            cbSalto.setChecked(equino.sabeSalto);
            if (equino.sabeDoma || equino.sabeSalto) cbTieneEspecialidades.setChecked(true);

            if (equino.idUsuario != null) {
                cbTienePropietario.setChecked(true);
                layPropietario.setVisibility(View.VISIBLE);
                // El observador de propietarios se encargará de marcar el seleccionado cuando cargue la lista
            }
        });
    }

    private void initViews() {
        etNombre = findViewById(R.id.etNombreEquino);
        etMicrochip = findViewById(R.id.etMicrochip);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etAltura = findViewById(R.id.etAltura);
        etPeso = findViewById(R.id.etPeso);
        etCuadra = findViewById(R.id.etCuadra);

        layNombre = findViewById(R.id.layNombreEquino);
        layMicrochip = findViewById(R.id.layMicrochip);
        layFechaNacimiento = findViewById(R.id.layFechaNacimiento);
        layCuadra = findViewById(R.id.layCuadra);
        layRaza = findViewById(R.id.layRaza);
        layPropietario = findViewById(R.id.layPropietario);

        spinnerRaza = findViewById(R.id.etRaza);
        spinnerSexo = findViewById(R.id.spinnerSexoEquino);
        spinnerTemperamento = findViewById(R.id.spinnerTemperamento);
        spinnerSalud = findViewById(R.id.spinnerSalud);
        spinnerPropietario = findViewById(R.id.spinnerPropietario);

        cbTienePropietario = findViewById(R.id.cbTienePropietario);
        cbTieneEspecialidades = findViewById(R.id.cbTieneEspecialidadesEquino);
        cbDoma = findViewById(R.id.cbDomaClasicaEquino);
        cbSalto = findViewById(R.id.cbSaltoEquino);
        layoutEspecialidades = findViewById(R.id.layoutEspecialidadesEquino);
    }

    private void setupSpinners() {
        // 1. Razas extendidas
        String[] razas = {"Pura Raza Española (PRE)", "Árabe", "Cuarto de Milla", "Pura Sangre Inglés",
                "Appaloosa", "Frisón", "CDE", "Lusitano", "Hispano-Árabe", "Anglo-Árabe", "Poni Galés", "Shetland"};
        spinnerRaza.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, razas));

        // 2. Sexo, Temperamento y Salud
        spinnerSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{Equino.SEXO_MACHO, Equino.SEXO_HEMBRA}));
        spinnerTemperamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{Equino.FACIL, Equino.MANEJABLE, Equino.DIFICIL}));
        spinnerSalud.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{Equino.BUENO, Equino.REGULAR, Equino.MALO}));

        // 3. Propietarios dinámicos desde el ViewModel
        viewModel.obtenerPropietarios().observe(this, usuarios -> {
            if (usuarios != null) {
                listaPropietariosCargados = usuarios;
                ArrayAdapter<Usuario> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, usuarios);
                spinnerPropietario.setAdapter(adapter);
            }
        });
    }

    private void setupFechaNacimiento() {
        etFechaNacimiento.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (fechaNacimientoSeleccionada > 0) c.setTimeInMillis(fechaNacimientoSeleccionada);

            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar fecha = Calendar.getInstance();
                fecha.set(year, month, day);
                fechaNacimientoSeleccionada = fecha.getTimeInMillis();
                etFechaNacimiento.setText(day + "/" + (month + 1) + "/" + year);
                layFechaNacimiento.setError(null);
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void setupCheckBoxListeners() {
        cbTienePropietario.setOnCheckedChangeListener((b, isChecked) -> {
            layPropietario.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) spinnerPropietario.setText("");
        });

        cbTieneEspecialidades.setOnCheckedChangeListener((b, isChecked) ->
                layoutEspecialidades.setVisibility(isChecked ? View.VISIBLE : View.GONE));
    }

    private boolean validar() {
        boolean ok = true;

        // 1. Validar Nombre
        if (etNombre.getText().toString().trim().isEmpty()) {
            layNombre.setError("El nombre es obligatorio");
            ok = false;
        } else {
            layNombre.setError(null);
        }

        // 2. Validar Raza
        if (spinnerRaza.getText().toString().trim().isEmpty()) {
            layRaza.setError("Selecciona una raza");
            ok = false;
        } else {
            layRaza.setError(null);
        }

        // 3. Validar Microchip (Exactamente 15 dígitos)
        String microchip = etMicrochip.getText().toString().trim();
        if (microchip.isEmpty()) {
            layMicrochip.setError("Obligatorio");
            ok = false;
        } else if (!microchip.matches("\\d{15}")) {
            // Mensaje con el ejemplo que pediste
            layMicrochip.setError("Debe tener 15 dígitos numéricos. Ej: 981001234567890");
            ok = false;
        } else {
            layMicrochip.setError(null);
        }

        // 4. Validar Fecha de Nacimiento
        if (fechaNacimientoSeleccionada <= 0) {
            layFechaNacimiento.setError("Selecciona fecha");
            ok = false;
        } else {
            layFechaNacimiento.setError(null);
        }

        // 5. Validar Cuadra
        if (etCuadra.getText().toString().trim().isEmpty()) {
            layCuadra.setError("Obligatorio");
            ok = false;
        } else {
            layCuadra.setError(null);
        }

        // 6. Validar Propietario (solo si el CheckBox está marcado)
        if (cbTienePropietario.isChecked()) {
            if (spinnerPropietario.getText().toString().trim().isEmpty()) {
                layPropietario.setError("Debes seleccionar un propietario");
                ok = false;
            } else {
                layPropietario.setError(null);
            }
        }

        // 7. Validar Disciplinas (solo si el CheckBox está marcado)
        if (cbTieneEspecialidades.isChecked()) {
            if (!cbDoma.isChecked() && !cbSalto.isChecked()) {
                Toast.makeText(this, "Selecciona al menos una disciplina", Toast.LENGTH_SHORT).show();
                ok = false;
            }
        }

        return ok;
    }

    private void guardarEquino() {
        if (!validar()) return;

        // Capturar ID del propietario si está seleccionado
        Integer idPropietario = null;
        if (cbTienePropietario.isChecked()) {
            String seleccion = spinnerPropietario.getText().toString();
            for (Usuario u : listaPropietariosCargados) {
                if (u.toString().equals(seleccion)) {
                    idPropietario = u.idUsuario;
                    break;
                }
            }
        }

        Equino e = new Equino(
                etNombre.getText().toString().trim(),
                spinnerRaza.getText().toString().trim(),
                fechaNacimientoSeleccionada,
                spinnerSexo.getText().toString().trim(),
                Double.parseDouble(etAltura.getText().toString().trim()),
                Double.parseDouble(etPeso.getText().toString().trim()),
                spinnerTemperamento.getText().toString().trim(),
                spinnerSalud.getText().toString().trim(),
                etMicrochip.getText().toString().trim(),
                cbSalto.isChecked(),
                cbDoma.isChecked(),
                idPropietario,
                Integer.parseInt(etCuadra.getText().toString().trim())
        );

        if (modoEdicion) {
            e.idEquino = equinoId;
            viewModel.actualizar(e);
        } else {
            viewModel.insertar(e);
        }
    }

    private void observarEstadoRepository() {
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;

            // Ocultar cualquier progreso si lo tuvieras
            // progressBar.setVisibility(View.GONE);

            switch (estado) {
                case "EXITO":
                    Toast.makeText(this, "Caballo guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_MICROCHIP_DUPLICADO":
                    layMicrochip.setError("Este microchip ya pertenece a otro equino");
                    etMicrochip.requestFocus();
                    break;

                case "ERROR_CUADRA_OCUPADA":
                    layCuadra.setError("Esta cuadra ya está asignada a otro caballo");
                    etCuadra.requestFocus();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this, "Error crítico en la base de datos", Toast.LENGTH_LONG).show();
                    break;

            }
        });
    }
}