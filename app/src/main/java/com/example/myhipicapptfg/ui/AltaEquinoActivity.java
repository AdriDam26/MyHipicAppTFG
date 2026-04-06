package com.example.myhipicapptfg.ui;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.AltaEquinoViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AltaEquinoActivity extends AppCompatActivity {

    private AltaEquinoViewModel viewModel;

    private TextInputEditText etNombre, etMicrochip, etFechaNacimiento, etAltura, etPeso;
    private AutoCompleteTextView etRaza;
    private TextInputLayout layNombre, layMicrochip, layPropietario;
    private AutoCompleteTextView spinnerSexo, spinnerTemperamento, spinnerSalud, spinnerCuadra, spinnerPropietario;
    private CheckBox cbTienePropietario;
    private ProgressBar progressBar;

    private CheckBox cbTieneEspecialidades;
    private LinearLayout layoutEspecialidades;
    private CheckBox cbDomaClasica, cbSalto, cbDomaVaquera;

    private List<Usuario> listaPropietariosObj = new ArrayList<>();
    private List<Cuadra> listaCuadrasObj = new ArrayList<>();
    private Integer idPropietarioSeleccionado = null;
    private Integer idCuadraSeleccionada = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_equino);

        viewModel = new ViewModelProvider(this).get(AltaEquinoViewModel.class);

        vincularVistas();
        configurarUI();
        observarViewModel();
    }

    private void vincularVistas() {
        etNombre = findViewById(R.id.etNombreEquino);
        etRaza = findViewById(R.id.etRaza);
        etMicrochip = findViewById(R.id.etMicrochip);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etAltura = findViewById(R.id.etAltura);
        etPeso = findViewById(R.id.etPeso);

        layNombre = findViewById(R.id.layNombreEquino);
        layMicrochip = findViewById(R.id.layMicrochip);
        layPropietario = findViewById(R.id.layPropietario);

        spinnerSexo = findViewById(R.id.spinnerSexoEquino);
        spinnerTemperamento = findViewById(R.id.spinnerTemperamento);
        spinnerSalud = findViewById(R.id.spinnerSalud);
        spinnerCuadra = findViewById(R.id.spinnerCuadra);
        spinnerPropietario = findViewById(R.id.spinnerPropietario);

        cbTienePropietario = findViewById(R.id.cbTienePropietario);
        progressBar = findViewById(R.id.progressBarEquino);

        cbTieneEspecialidades = findViewById(R.id.cbTieneEspecialidadesEquino);
        layoutEspecialidades = findViewById(R.id.layoutEspecialidadesEquino);
        cbDomaClasica = findViewById(R.id.cbDomaClasicaEquino);
        cbSalto = findViewById(R.id.cbSaltoEquino);
        cbDomaVaquera = findViewById(R.id.cbDomaVaqueraEquino);

        findViewById(R.id.btnVolverAltaEquino).setOnClickListener(v -> finish());
        findViewById(R.id.btnGuardarEquino).setOnClickListener(v -> validarYGuardar());
    }

    private void configurarUI() {
        etFechaNacimiento.setOnClickListener(v -> mostrarDatePicker());

        String[] sexos = {"Macho", "Hembra"};
        spinnerSexo.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, sexos));

        String[] temperamentos = {Equino.FACIL, Equino.MANEJABLE, Equino.DIFICIL};
        spinnerTemperamento.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, temperamentos));

        String[] estadosSalud = {Equino.BUENO, Equino.REGULAR, Equino.MALO};
        spinnerSalud.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, estadosSalud));

        cbTienePropietario.setOnCheckedChangeListener((buttonView, isChecked) -> {
            layPropietario.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                idPropietarioSeleccionado = null;
                spinnerPropietario.setText("");
            }
        });

        // 1. Obtener el array
        String[] razas = getResources().getStringArray(R.array.razas_equinos);

        // 2. Crear el adaptador
        ArrayAdapter<String> adapterRazas = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                razas
        );

        // 3. Usar la variable de la clase directamente (etRaza)
        etRaza.setAdapter(adapterRazas);
        etRaza.setThreshold(1);

        cbTieneEspecialidades.setOnCheckedChangeListener((buttonView, isChecked) -> {
            // Mostrar/Ocultar el panel
            layoutEspecialidades.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            // Si se desmarca, limpiamos los checks internos para evitar errores
            if (!isChecked) {
                cbDomaClasica.setChecked(false);
                cbSalto.setChecked(false);
                cbDomaVaquera.setChecked(false);
            }
        });
    }

    private void observarViewModel() {
        viewModel.getCuadras().observe(this, cuadras -> {
            listaCuadrasObj = cuadras;
            List<String> labels = new ArrayList<>();
            for (Cuadra c : cuadras) labels.add("Cuadra nº " + c.numeroCuadra);
            spinnerCuadra.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, labels));
            spinnerCuadra.setOnItemClickListener((parent, view, position, id) ->
                    idCuadraSeleccionada = listaCuadrasObj.get(position).idCuadra);
        });

        viewModel.getPropietarios().observe(this, propietarios -> {
            listaPropietariosObj = propietarios;
            List<String> nombres = new ArrayList<>();
            for (Usuario u : propietarios) nombres.add(u.nombre + " " + u.apellido1 + " " + u.apellido2);
            spinnerPropietario.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres));
            spinnerPropietario.setOnItemClickListener((parent, view, position, id) ->
                    idPropietarioSeleccionado = listaPropietariosObj.get(position).idUsuario);
        });

        viewModel.getMensajeEstado().observe(this, mensaje -> {
            viewModel.finalizarOperacion();
            if (mensaje == null) {
                Toast.makeText(this, "Equino registrado correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (mensaje.toLowerCase().contains("microchip")) layMicrochip.setError(mensaje);
                else Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show();
            }
        });

        viewModel.getCargando().observe(this, c -> progressBar.setVisibility(c ? View.VISIBLE : View.GONE));
    }

    private void mostrarDatePicker() {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, monthOfYear, dayOfMonth) -> {
            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year1, monthOfYear + 1, dayOfMonth);
            etFechaNacimiento.setText(fecha);
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void validarYGuardar() {
        layNombre.setError(null);
        layMicrochip.setError(null);

        // Extraemos todos los Strings de la UI
        String nombre = etNombre.getText().toString().trim();
        String raza = etRaza.getText().toString().trim();
        String microchip = etMicrochip.getText().toString().trim();
        String fecha = etFechaNacimiento.getText().toString().trim();
        String sexo = spinnerSexo.getText().toString();
        String temp = spinnerTemperamento.getText().toString();
        String salud = spinnerSalud.getText().toString();
        String altura = etAltura.getText().toString().trim();
        String peso = etPeso.getText().toString().trim();

        // Validaciones mínimas de UI
        if (nombre.isEmpty()) {
            layNombre.setError("Obligatorio");
            return;
        }
        if (microchip.isEmpty()) {
            layMicrochip.setError("El microchip es obligatorio");
            return;
        } else if (!microchip.matches("\\d{15}")) {
            layMicrochip.setError("El microchip debe tener exactamente 15 números (sin letras ni espacios)");
            etMicrochip.requestFocus();
            return;
        }
        if (idCuadraSeleccionada == null) {
            Toast.makeText(this, "Selecciona una cuadra", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Integer> disciplinasSeleccionadas = new ArrayList<>();

        if (cbTieneEspecialidades.isChecked()) {
            // IDs fijos de tu tabla Disciplina
            if (cbDomaClasica.isChecked()) disciplinasSeleccionadas.add(1);
            if (cbSalto.isChecked()) disciplinasSeleccionadas.add(2);
            if (cbDomaVaquera.isChecked()) disciplinasSeleccionadas.add(3);

            // Validación: Si dice que tiene, debe marcar al menos una
            if (disciplinasSeleccionadas.isEmpty()) {
                Toast.makeText(this, "Selecciona al menos una disciplina", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // Enviamos la lista al ViewModel
        viewModel.registrarEquino(
                nombre, raza, microchip, fecha, sexo, temp, salud,
                altura, peso, idCuadraSeleccionada, idPropietarioSeleccionado,
                disciplinasSeleccionadas
        );
    }
}