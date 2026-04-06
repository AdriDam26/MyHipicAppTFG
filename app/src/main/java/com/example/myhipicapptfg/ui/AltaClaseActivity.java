package com.example.myhipicapptfg.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Disciplina;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.AltaClaseViewModel;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AltaClaseActivity extends AppCompatActivity {

    private AltaClaseViewModel viewModel;
    private TextInputEditText etFecha, etHoraInicio, etHoraFin;
    private AutoCompleteTextView spinnerNivel, spinnerProfesor, spinnerPista, spinnerDisciplina;

    private List<Usuario> listaProfesoresObj = new ArrayList<>();
    private List<Pista> listaPistasObj = new ArrayList<>();
    private List<Disciplina> listaDisciplinasObj = new ArrayList<>();

    private Integer idProfesorSel = null;
    private Integer idPistaSel = null;
    private Integer idDisciplinaSel = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alta_clase);

        viewModel = new ViewModelProvider(this).get(AltaClaseViewModel.class);

        vincularVistas();
        configurarUI();
        observarViewModel();
    }

    private void vincularVistas() {
        etFecha = findViewById(R.id.etFechaClase);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        spinnerNivel = findViewById(R.id.spinnerNivel);
        spinnerProfesor = findViewById(R.id.spinnerProfesor);
        spinnerPista = findViewById(R.id.spinnerPista);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
    }

    private void configurarUI() {
        etFecha.setOnClickListener(v -> mostrarDatePicker());
        etHoraInicio.setOnClickListener(v -> mostrarTimePicker(etHoraInicio));
        etHoraFin.setOnClickListener(v -> mostrarTimePicker(etHoraFin));

        // Niveles estáticos
        String[] niveles = {Clase.PRINCIPIANTE, Clase.INTERMEDIO, Clase.AVANZADO};
        ArrayAdapter<String> adapterNivel = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, niveles);
        spinnerNivel.setAdapter(adapterNivel);


        View.OnTouchListener bloqueoListener = (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                if (horarioIncompleto()) {
                    Toast.makeText(this, "Debe rellenar Fecha, Hora Inicio y Hora Fin primero", Toast.LENGTH_SHORT).show();
                    return true; // Bloquea el evento: no se abre el desplegable
                }

                // Si la hora de fin es menor a la de inicio, también bloqueamos
                String hIni = etHoraInicio.getText().toString();
                String hFin = etHoraFin.getText().toString();
                if (!hIni.isEmpty() && !hFin.isEmpty() && hIni.compareTo(hFin) >= 0) {
                    Toast.makeText(this, "Corrija el error en las horas antes de continuar", Toast.LENGTH_SHORT).show();
                    return true;
                }
            }
            return false; // Permite que el evento continúe y se abra el spinner
        };

        spinnerPista.setOnTouchListener(bloqueoListener);
        spinnerProfesor.setOnTouchListener(bloqueoListener);



        // Forzar mostrar desplegable al hacer click (evita que el filtro lo oculte)
        spinnerProfesor.setOnClickListener(v -> spinnerProfesor.showDropDown());
        spinnerPista.setOnClickListener(v -> spinnerPista.showDropDown());
        spinnerDisciplina.setOnClickListener(v -> spinnerDisciplina.showDropDown());
        spinnerNivel.setOnClickListener(v -> spinnerNivel.showDropDown());
    }

    private void observarViewModel() {
        // Observar Pistas LIBRES
        viewModel.getPistasLibres().observe(this, pistas -> {
            listaPistasObj = pistas;
            List<String> nombres = new ArrayList<>();
            // Asegúrate que en Pista.java el campo sea "nombrePista" o "nombre"
            for (Pista p : pistas) nombres.add(p.nombre);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
            spinnerPista.setAdapter(adapter);
        });

        spinnerPista.setOnItemClickListener((parent, view, position, id) -> {
            idPistaSel = listaPistasObj.get(position).idPista;
        });

        // Observar Profesores LIBRES (Ahora vienen como objetos Usuario)
        viewModel.getProfesoresLibres().observe(this, profes -> {
            listaProfesoresObj = profes;
            List<String> nombres = new ArrayList<>();
            for (Usuario u : profes) {
                String nombreFull = u.nombre + " " + u.apellido1 + (u.apellido2 != null ? " " + u.apellido2 : "");
                nombres.add(nombreFull);
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
            spinnerProfesor.setAdapter(adapter);
        });

        spinnerProfesor.setOnItemClickListener((parent, view, position, id) -> {
            // El idUsuario es el mismo que ID_Profesor por la relación 1:1
            idProfesorSel = listaProfesoresObj.get(position).idUsuario;
        });

        // Observar Disciplinas
        viewModel.getDisciplinas().observe(this, disciplinas -> {
            listaDisciplinasObj = disciplinas;
            List<String> nombres = new ArrayList<>();
            for (Disciplina d : disciplinas) nombres.add(d.nombre);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nombres);
            spinnerDisciplina.setAdapter(adapter);
        });

        spinnerDisciplina.setOnItemClickListener((parent, view, position, id) -> {
            idDisciplinaSel = listaDisciplinasObj.get(position).idDisciplina;
        });

        // Estado de guardado y errores
        viewModel.getErrorLiveData().observe(this, error -> {
            viewModel.finalizarCarga();
            if (error == null) {
                Toast.makeText(this, "Clase creada correctamente", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });

        findViewById(R.id.btnGuardarClase).setOnClickListener(v -> validarYGuardar());
    }

    private void refrescarDisponibilidad() {
        String fecha = etFecha.getText().toString();
        String hIni = etHoraInicio.getText().toString();
        String hFin = etHoraFin.getText().toString();

        // Referencia al contenedor para mostrar el error visualmente (el TextInputLayout)
        com.google.android.material.textfield.TextInputLayout layoutHoraFin = findViewById(R.id.etHoraFin).getParent().getParent() instanceof com.google.android.material.textfield.TextInputLayout ?
                (com.google.android.material.textfield.TextInputLayout) findViewById(R.id.etHoraFin).getParent().getParent() : null;

        if (!fecha.isEmpty() && !hIni.isEmpty() && !hFin.isEmpty()) {
            if (hIni.compareTo(hFin) < 0) {
                // HORARIO CORRECTO
                if (layoutHoraFin != null) layoutHoraFin.setError(null);
                etHoraFin.setError(null);

                // Limpiamos selecciones previas para forzar nueva búsqueda
                spinnerPista.setText("", false);
                spinnerProfesor.setText("", false);
                idPistaSel = null;
                idProfesorSel = null;

                viewModel.actualizarDisponibilidad(fecha, hIni, hFin);
            } else {
                // HORARIO INCORRECTO: Mostramos advertencia en rojo
                etHoraFin.setError("¡Hora fin debe ser mayor!");
                if (layoutHoraFin != null) {
                    layoutHoraFin.setError("La hora de fin no puede ser anterior o igual a la de inicio");
                }

                // Limpiamos los spinners para evitar que elijan algo inválido
                spinnerPista.setAdapter(null);
                spinnerProfesor.setAdapter(null);
                spinnerPista.setText("", false);
                spinnerProfesor.setText("", false);
            }
        }
    }

    private void mostrarDatePicker() {
        Calendar c = Calendar.getInstance();
        new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String fecha = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            etFecha.setText(fecha);
            refrescarDisponibilidad();
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void mostrarTimePicker(TextInputEditText campo) {
        Calendar c = Calendar.getInstance();
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String hora = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute);
            campo.setText(hora);
            refrescarDisponibilidad();
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
    }

    private void validarYGuardar() {
        String nivel = spinnerNivel.getText().toString();

        if (idProfesorSel == null || idPistaSel == null || idDisciplinaSel == null ||
                etFecha.getText().toString().isEmpty() || nivel.isEmpty()) {
            Toast.makeText(this, "Rellene todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        viewModel.registrarClase(
                etFecha.getText().toString(),
                etHoraInicio.getText().toString(),
                etHoraFin.getText().toString(),
                nivel,
                idPistaSel, idDisciplinaSel, idProfesorSel
        );
    }

    private boolean horarioIncompleto() {
        return etFecha.getText().toString().isEmpty() ||
                etHoraInicio.getText().toString().isEmpty() ||
                etHoraFin.getText().toString().isEmpty();
    }
}