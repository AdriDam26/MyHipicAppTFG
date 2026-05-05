package com.example.myhipicapptfg.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.entities.Pista;
import com.example.myhipicapptfg.entities.Profesor;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.GestionClaseViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClaseFormActivity extends AppCompatActivity {

    private GestionClaseViewModel viewModel;

    private TextInputEditText etFecha, etHoraInicio, etHoraFin;
    private TextInputLayout layFecha, layHoraInicio, layHoraFin, layNivel, layProfesor, layPista, layDisciplina;
    private AutoCompleteTextView spinnerNivel, spinnerProfesor, spinnerPista, spinnerDisciplina;

    private List<Usuario> listaUsuariosProfesores = new ArrayList<>();
    private List<Profesor> detallesProfesores = new ArrayList<>();
    private List<Pista> listaPistasCargadas = new ArrayList<>();
    private List<Clase> todasLasClasesCargadas = new ArrayList<>();

    private long fechaSeleccionada = -1;
    private long horaInicioSeleccionada = -1;
    private long horaFinSeleccionada = -1;
    private int claseId = -1;
    private boolean modoEdicion = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clase_form);

        viewModel = new ViewModelProvider(this).get(GestionClaseViewModel.class);

        initViews();
        setupPickers();
        setupSpinnersEstaticos();
        observarDatos();

        if (getIntent().hasExtra("ID_CLASE")) {
            claseId = getIntent().getIntExtra("ID_CLASE", -1);
            modoEdicion = true;
            cargarClase(claseId);
        }

        findViewById(R.id.btnGuardarClase).setOnClickListener(v -> guardarClase());
        findViewById(R.id.btnVolverClase).setOnClickListener(v -> finish());
    }

    private void initViews() {
        etFecha = findViewById(R.id.etFechaClase);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);
        layFecha = findViewById(R.id.layFechaClase);
        layHoraInicio = findViewById(R.id.layHoraInicio);
        layHoraFin = findViewById(R.id.layHoraFin);
        layNivel = findViewById(R.id.layNivelClase);
        layProfesor = findViewById(R.id.layProfesorClase);
        layPista = findViewById(R.id.layPistaClase);
        layDisciplina = findViewById(R.id.layDisciplinaClase);
        spinnerNivel = findViewById(R.id.spinnerNivel);
        spinnerProfesor = findViewById(R.id.spinnerProfesor);
        spinnerPista = findViewById(R.id.spinnerPista);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
    }

    private void observarDatos() {
        viewModel.obtenerTodasLasClases().observe(this, clases -> {
            todasLasClasesCargadas = clases;
            actualizarOpcionesDisponibles();
        });

        viewModel.obtenerTodosLosUsuarios().observe(this, usuarios -> {
            listaUsuariosProfesores.clear();
            for (Usuario u : usuarios) {
                if (Usuario.TIPO_PROFESOR.equals(u.tipo)) listaUsuariosProfesores.add(u);
            }
            actualizarOpcionesDisponibles();
        });

        viewModel.obtenerTodosLosProfesores().observe(this, profesores -> {
            detallesProfesores = profesores;
            actualizarOpcionesDisponibles();
        });

        viewModel.obtenerTodasLasPistas().observe(this, pistas -> {
            listaPistasCargadas = pistas;
            actualizarOpcionesDisponibles();
        });
    }

    private void actualizarOpcionesDisponibles() {
        // Si no hay tiempos definidos, no podemos filtrar disponibilidad
        if (fechaSeleccionada <= 0 || horaInicioSeleccionada <= 0 || horaFinSeleccionada <= 0) return;

        String disciplina = spinnerDisciplina.getText().toString();
        String nivel = spinnerNivel.getText().toString();

        // --- FILTRAR PROFESORES ---
        List<Usuario> disponiblesProfs = new ArrayList<>();
        for (Usuario u : listaUsuariosProfesores) {
            Profesor det = buscarDetalle(u.idUsuario);
            if (det != null && cumpleRequisitos(det, disciplina, nivel) && profesorEstaLibre(u.idUsuario)) {
                disponiblesProfs.add(u);
            }
        }

        // CORRECCIÓN 2: Actualizar adaptador y resetear profesor si ya no es válido
        ArrayAdapter<Usuario> adapterProf = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, disponiblesProfs);
        spinnerProfesor.setAdapter(adapterProf);

        String profActual = spinnerProfesor.getText().toString();
        boolean sigueSiendoValido = false;
        for (Usuario u : disponiblesProfs) {
            if (u.toString().equals(profActual)) { sigueSiendoValido = true; break; }
        }
        if (!sigueSiendoValido) spinnerProfesor.setText("", false);

        // --- FILTRAR PISTAS ---
        List<Pista> disponiblesPistas = new ArrayList<>();
        for (Pista pi : listaPistasCargadas) {
            if (pistaEstaLibre(pi.idPista)) disponiblesPistas.add(pi);
        }
        spinnerPista.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, disponiblesPistas));
    }

    private void setupPickers() {
        etFecha.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            if (fechaSeleccionada > 0) c.setTimeInMillis(fechaSeleccionada);

            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar selected = Calendar.getInstance();
                selected.set(year, month, day, 0, 0, 0);
                selected.set(Calendar.MILLISECOND, 0);
                fechaSeleccionada = selected.getTimeInMillis();

                // CORRECCIÓN 3: Formateo correcto de fecha para el texto
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                etFecha.setText(sdf.format(selected.getTime()));

                actualizarOpcionesDisponibles();
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        etHoraInicio.setOnClickListener(v -> mostrarTimePicker(true));
        etHoraFin.setOnClickListener(v -> mostrarTimePicker(false));
    }

    private void mostrarTimePicker(boolean esInicio) {
        new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            Calendar t = Calendar.getInstance();
            // Importante usar la fecha seleccionada para comparar milisegundos correctamente
            if (fechaSeleccionada > 0) t.setTimeInMillis(fechaSeleccionada);
            t.set(Calendar.HOUR_OF_DAY, hourOfDay);
            t.set(Calendar.MINUTE, minute);
            t.set(Calendar.SECOND, 0);
            t.set(Calendar.MILLISECOND, 0);

            if (esInicio) {
                horaInicioSeleccionada = t.getTimeInMillis();
                etHoraInicio.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            } else {
                horaFinSeleccionada = t.getTimeInMillis();
                etHoraFin.setText(String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute));
            }
            actualizarOpcionesDisponibles();
        }, 12, 0, true).show();
    }

    private void setupSpinnersEstaticos() {
        spinnerNivel.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{Clase.PRINCIPIANTE, Clase.INTERMEDIO, Clase.AVANZADO}));
        spinnerDisciplina.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, new String[]{Clase.DOMA, Clase.SALTO}));

        spinnerNivel.setOnItemClickListener((p, v, i, l) -> actualizarOpcionesDisponibles());
        spinnerDisciplina.setOnItemClickListener((p, v, i, l) -> actualizarOpcionesDisponibles());
    }

    private boolean validar() {
        boolean ok = true;

        if (fechaSeleccionada <= 0) { layFecha.setError("Obligatorio"); ok = false; } else layFecha.setError(null);

        if (horaInicioSeleccionada <= 0) { layHoraInicio.setError("Obligatorio"); ok = false; } else layHoraInicio.setError(null);

        if (horaFinSeleccionada <= 0) { layHoraFin.setError("Obligatorio"); ok = false; } else layHoraFin.setError(null);

        // CORRECCIÓN 1: Validar que Fin sea después de Inicio
        if (horaInicioSeleccionada > 0 && horaFinSeleccionada > 0) {
            if (horaFinSeleccionada <= horaInicioSeleccionada) {
                layHoraFin.setError("La hora de fin debe ser posterior a la de inicio");
                ok = false;
            } else layHoraFin.setError(null);
        }

        if (spinnerProfesor.getText().toString().isEmpty()) { layProfesor.setError("Selecciona profesor"); ok = false; } else layProfesor.setError(null);

        if (spinnerPista.getText().toString().isEmpty()) { layPista.setError("Selecciona pista"); ok = false; } else layPista.setError(null);

        return ok;
    }

    private void guardarClase() {
        if (!validar()) return;

        int idProfesor = -1;
        String selProf = spinnerProfesor.getText().toString();
        for (Usuario u : listaUsuariosProfesores) {
            if (u.toString().equals(selProf)) { idProfesor = u.idUsuario; break; }
        }

        int idPista = -1;
        String selPista = spinnerPista.getText().toString();
        for (Pista pi : listaPistasCargadas) {
            if (pi.toString().equals(selPista)) { idPista = pi.idPista; break; }
        }

        Clase c = new Clase(horaInicioSeleccionada, horaFinSeleccionada, fechaSeleccionada,
                spinnerNivel.getText().toString(), spinnerDisciplina.getText().toString(),
                idPista, idProfesor);

        if (modoEdicion) c.idClase = claseId;

        if (modoEdicion) {
            viewModel.actualizar(c);
        }
        else viewModel.insertar(c);

        finish();
    }

    private void cargarClase(int id) {
        viewModel.buscarPorId(id).observe(this, clase -> {
            if (clase == null) return;
            fechaSeleccionada = clase.fecha;
            horaInicioSeleccionada = clase.horaInicio;
            horaFinSeleccionada = clase.horaFin;

            SimpleDateFormat sdfF = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat sdfH = new SimpleDateFormat("HH:mm", Locale.getDefault());

            etFecha.setText(sdfF.format(clase.fecha));
            etHoraInicio.setText(sdfH.format(clase.horaInicio));
            etHoraFin.setText(sdfH.format(clase.horaFin));

            spinnerNivel.setText(clase.nivel, false);
            spinnerDisciplina.setText(clase.disciplina, false);

            // Importante cargar primero el texto de pista y profesor para que no se borren al filtrar
            // Nota: Podría requerir lógica adicional si las listas aún no están cargadas
            actualizarOpcionesDisponibles();
        });
    }

    private Profesor buscarDetalle(int idUsuario) {
        for (Profesor p : detallesProfesores) {
            if (p.idProfesor == idUsuario) return p;
        }
        return null;
    }

    private boolean cumpleRequisitos(Profesor p, String disciplina, String nivelClase) {
        if (disciplina.isEmpty() || nivelClase.isEmpty()) return true;
        int pClase = nivelAInt(nivelClase);
        if (disciplina.equals(Clase.DOMA)) {
            return p.puedeDarDoma && nivelAInt(p.nivelMaximoDoma) >= pClase;
        } else {
            return p.puedeDarSalto && nivelAInt(p.nivelMaximoSalto) >= pClase;
        }
    }

    private int nivelAInt(String nivel) {
        if (nivel == null) return 0;
        if (nivel.equals(Clase.AVANZADO)) return 3;
        if (nivel.equals(Clase.INTERMEDIO)) return 2;
        return 1;
    }

    private boolean profesorEstaLibre(int idProfesor) {
        for (Clase c : todasLasClasesCargadas) {
            if (modoEdicion && c.idClase == claseId) continue;
            if (c.fecha == fechaSeleccionada && c.idProfesor == idProfesor) {
                if (horaInicioSeleccionada < c.horaFin && horaFinSeleccionada > c.horaInicio) return false;
            }
        }
        return true;
    }

    private boolean pistaEstaLibre(int idPista) {
        for (Clase c : todasLasClasesCargadas) {
            if (modoEdicion && c.idClase == claseId) continue;
            if (c.fecha == fechaSeleccionada && c.idPista == idPista) {
                if (horaInicioSeleccionada < c.horaFin && horaFinSeleccionada > c.horaInicio) return false;
            }
        }
        return true;
    }
}