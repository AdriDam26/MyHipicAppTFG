package com.example.myhipicapptfg.ui.admin.clases;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ClaseFormActivity extends AppCompatActivity {

    private GestionClaseViewModel viewModel;

    // =========================================================
    // VISTAS
    // =========================================================

    private TextInputEditText etFecha;
    private TextInputEditText etHoraInicio;
    private TextInputEditText etHoraFin;

    private TextInputLayout layFecha;
    private TextInputLayout layHoraInicio;
    private TextInputLayout layHoraFin;
    private TextInputLayout layProfesor;
    private TextInputLayout layPista;

    private AutoCompleteTextView spinnerNivel;
    private AutoCompleteTextView spinnerProfesor;
    private AutoCompleteTextView spinnerPista;
    private AutoCompleteTextView spinnerDisciplina;

    // =========================================================
    // DATOS
    // =========================================================

    private final List<Usuario> listaUsuariosProfesores =
            new ArrayList<>();

    private final List<Profesor> detallesProfesores =
            new ArrayList<>();

    private final List<Pista> listaPistasCargadas =
            new ArrayList<>();

    private final List<Clase> todasLasClasesCargadas =
            new ArrayList<>();

    // =========================================================
    // ESTADO
    // =========================================================

    private long fechaSeleccionada = -1;
    private long horaInicioSeleccionada = -1;
    private long horaFinSeleccionada = -1;

    private int claseId = -1;

    private boolean modoEdicion = false;

    // =========================================================
    // CICLO VIDA
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_clase_form);

        viewModel =
                new ViewModelProvider(this)
                        .get(GestionClaseViewModel.class);

        initViews();

        setupPickers();

        setupSpinners();

        observarDatos();

        comprobarModoEdicion();

        setupBotones();
    }

    // =========================================================
    // INIT
    // =========================================================

    private void initViews() {

        etFecha = findViewById(R.id.etFechaClase);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etHoraFin = findViewById(R.id.etHoraFin);

        layFecha = findViewById(R.id.layFechaClase);
        layHoraInicio = findViewById(R.id.layHoraInicio);
        layHoraFin = findViewById(R.id.layHoraFin);
        layProfesor = findViewById(R.id.layProfesorClase);
        layPista = findViewById(R.id.layPistaClase);

        spinnerNivel = findViewById(R.id.spinnerNivel);
        spinnerProfesor = findViewById(R.id.spinnerProfesor);
        spinnerPista = findViewById(R.id.spinnerPista);
        spinnerDisciplina = findViewById(R.id.spinnerDisciplina);
    }

    private void setupBotones() {

        findViewById(R.id.btnGuardarClase)
                .setOnClickListener(v -> guardarClase());


    }

    private void comprobarModoEdicion() {

        if (!getIntent().hasExtra("ID_CLASE"))
            return;

        claseId =
                getIntent().getIntExtra("ID_CLASE", -1);

        modoEdicion = true;

        cargarClase(claseId);
    }

    // =========================================================
    // OBSERVERS
    // =========================================================

    private void observarDatos() {

        viewModel.obtenerTodasLasClases()
                .observe(this, clases -> {

                    todasLasClasesCargadas.clear();
                    todasLasClasesCargadas.addAll(clases);

                    actualizarDisponibilidad();
                });

        viewModel.obtenerTodosLosUsuarios()
                .observe(this, usuarios -> {

                    listaUsuariosProfesores.clear();

                    for (Usuario usuario : usuarios) {

                        if (Usuario.TIPO_PROFESOR.equals(usuario.tipo)) {

                            listaUsuariosProfesores.add(usuario);
                        }
                    }

                    actualizarDisponibilidad();
                });

        viewModel.obtenerTodosLosProfesores()
                .observe(this, profesores -> {

                    detallesProfesores.clear();
                    detallesProfesores.addAll(profesores);

                    actualizarDisponibilidad();
                });

        viewModel.obtenerTodasLasPistas()
                .observe(this, pistas -> {

                    listaPistasCargadas.clear();
                    listaPistasCargadas.addAll(pistas);

                    actualizarDisponibilidad();
                });

        // =====================================================
        // PROFESORES DISPONIBLES
        // =====================================================

        viewModel.getProfesoresDisponibles()
                .observe(this, profesores -> {

                    ArrayAdapter<Usuario> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    profesores
                            );

                    spinnerProfesor.setAdapter(adapter);

                    // ✅ Mensaje si no hay profesores
                    if (profesores.isEmpty()) {

                        layProfesor.setHelperText(
                                "No hay profesores disponibles para esta combinación"
                        );

                    } else {

                        layProfesor.setHelperText(
                                profesores.size() + " profesores disponibles"
                        );
                    }

                    validarProfesorSeleccionado(profesores);
                });

        // =====================================================
        // PISTAS DISPONIBLES
        // =====================================================

        viewModel.getPistasDisponibles()
                .observe(this, pistas -> {

                    ArrayAdapter<Pista> adapter =
                            new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_dropdown_item_1line,
                                    pistas
                            );

                    spinnerPista.setAdapter(adapter);

                    // ✅ Mensaje si no hay pistas
                    if (pistas.isEmpty()) {

                        layPista.setHelperText(
                                "No hay pistas disponibles para esta combinación"
                        );

                    } else {

                        layPista.setHelperText(
                                pistas.size() + " pistas disponibles"
                        );
                    }

                    validarPistaSeleccionada(pistas);
                });
    }

    // =========================================================
    // SPINNERS
    // =========================================================

    private void setupSpinners() {

        spinnerNivel.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{
                                Clase.PRINCIPIANTE,
                                Clase.INTERMEDIO,
                                Clase.AVANZADO
                        }
                )
        );

        spinnerDisciplina.setAdapter(
                new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_dropdown_item_1line,
                        new String[]{
                                Clase.DOMA,
                                Clase.SALTO
                        }
                )
        );

        spinnerNivel.setOnItemClickListener(
                (parent, view, position, id) ->
                        actualizarDisponibilidad()
        );

        spinnerDisciplina.setOnItemClickListener(
                (parent, view, position, id) ->
                        actualizarDisponibilidad()
        );
    }

    // =========================================================
    // PICKERS
    // =========================================================

    private void setupPickers() {

        etFecha.setOnClickListener(v -> abrirDatePicker());

        etHoraInicio.setOnClickListener(v ->
                mostrarTimePicker(true));

        etHoraFin.setOnClickListener(v ->
                mostrarTimePicker(false));
    }

    private void abrirDatePicker() {

        Calendar calendario = Calendar.getInstance();

        if (fechaSeleccionada > 0) {
            calendario.setTimeInMillis(fechaSeleccionada);
        }

        new DatePickerDialog(
                this,
                (view, year, month, day) -> {

                    Calendar selected =
                            Calendar.getInstance();

                    selected.set(year, month, day);

                    selected.set(Calendar.HOUR_OF_DAY, 0);
                    selected.set(Calendar.MINUTE, 0);
                    selected.set(Calendar.SECOND, 0);
                    selected.set(Calendar.MILLISECOND, 0);

                    fechaSeleccionada =
                            selected.getTimeInMillis();

                    SimpleDateFormat sdf =
                            new SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                            );

                    etFecha.setText(
                            sdf.format(selected.getTime())
                    );

                    actualizarDisponibilidad();
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void mostrarTimePicker(boolean esInicio) {

        new TimePickerDialog(
                this,
                (view, hourOfDay, minute) -> {

                    Calendar calendario =
                            Calendar.getInstance();

                    if (fechaSeleccionada > 0) {

                        calendario.setTimeInMillis(
                                fechaSeleccionada
                        );
                    }

                    calendario.set(
                            Calendar.HOUR_OF_DAY,
                            hourOfDay
                    );

                    calendario.set(
                            Calendar.MINUTE,
                            minute
                    );

                    calendario.set(Calendar.SECOND, 0);
                    calendario.set(Calendar.MILLISECOND, 0);

                    long millis =
                            calendario.getTimeInMillis();

                    String horaTexto =
                            String.format(
                                    Locale.getDefault(),
                                    "%02d:%02d",
                                    hourOfDay,
                                    minute
                            );

                    if (esInicio) {

                        horaInicioSeleccionada = millis;

                        etHoraInicio.setText(horaTexto);

                    } else {

                        horaFinSeleccionada = millis;

                        etHoraFin.setText(horaTexto);
                    }

                    actualizarDisponibilidad();
                },
                12,
                0,
                true
        ).show();
    }

    // =========================================================
    // DISPONIBILIDAD
    // =========================================================

    private void actualizarDisponibilidad() {

        if (fechaSeleccionada <= 0)
            return;

        if (horaInicioSeleccionada <= 0)
            return;

        if (horaFinSeleccionada <= 0)
            return;

        viewModel.filtrarDisponibilidad(
                horaInicioSeleccionada,
                horaFinSeleccionada,
                spinnerDisciplina.getText().toString(),
                spinnerNivel.getText().toString(),
                listaUsuariosProfesores,
                detallesProfesores,
                listaPistasCargadas,
                todasLasClasesCargadas,
                modoEdicion ? claseId : -1
        );
    }

    // =========================================================
    // VALIDACIONES UI
    // =========================================================

    private void validarProfesorSeleccionado(
            List<Usuario> profesores
    ) {

        String actual =
                spinnerProfesor.getText().toString();

        boolean valido = false;

        for (Usuario usuario : profesores) {

            if (usuario.toString().equals(actual)) {

                valido = true;
                break;
            }
        }

        if (!valido) {

            spinnerProfesor.setText("", false);
        }
    }

    private void validarPistaSeleccionada(
            List<Pista> pistas
    ) {

        String actual =
                spinnerPista.getText().toString();

        boolean valido = false;

        for (Pista pista : pistas) {

            if (pista.toString().equals(actual)) {

                valido = true;
                break;
            }
        }

        if (!valido) {

            spinnerPista.setText("", false);
        }
    }

    // =========================================================
    // VALIDAR FORMULARIO
    // =========================================================

    private boolean validar() {

        boolean ok = true;

        if (fechaSeleccionada <= 0) {

            layFecha.setError("Obligatorio");

            ok = false;

        } else {

            layFecha.setError(null);
        }

        if (horaInicioSeleccionada <= 0) {

            layHoraInicio.setError("Obligatorio");

            ok = false;

        } else {

            layHoraInicio.setError(null);
        }

        if (horaFinSeleccionada <= 0) {

            layHoraFin.setError("Obligatorio");

            ok = false;

        } else {

            layHoraFin.setError(null);
        }

        if (horaInicioSeleccionada > 0 &&
                horaFinSeleccionada > 0 &&
                horaFinSeleccionada <= horaInicioSeleccionada) {

            layHoraFin.setError(
                    "La hora fin debe ser posterior"
            );

            ok = false;
        }

        if (spinnerProfesor.getText().toString().isEmpty()) {

            layProfesor.setError("Selecciona profesor");

            ok = false;

        } else {

            layProfesor.setError(null);
        }

        if (spinnerPista.getText().toString().isEmpty()) {

            layPista.setError("Selecciona pista");

            ok = false;

        } else {

            layPista.setError(null);
        }

        return ok;
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    private void guardarClase() {

        if (!validar())
            return;

        int idProfesor = obtenerIdProfesorSeleccionado();

        int idPista = obtenerIdPistaSeleccionada();

        Clase clase = new Clase(
                horaInicioSeleccionada,
                horaFinSeleccionada,
                fechaSeleccionada,
                spinnerNivel.getText().toString(),
                spinnerDisciplina.getText().toString(),
                idPista,
                idProfesor
        );

        if (modoEdicion) {
            clase.idClase = claseId;
        }

        if (modoEdicion) {

            viewModel.actualizar(clase);

        } else {

            viewModel.insertar(clase);
        }

        finish();
    }

    // =========================================================
    // CARGAR
    // =========================================================

    private void cargarClase(int id) {

        viewModel.buscarPorId(id)
                .observe(this, clase -> {

                    if (clase == null)
                        return;

                    fechaSeleccionada = clase.fecha;
                    horaInicioSeleccionada = clase.horaInicio;
                    horaFinSeleccionada = clase.horaFin;

                    SimpleDateFormat sdfFecha =
                            new SimpleDateFormat(
                                    "dd/MM/yyyy",
                                    Locale.getDefault()
                            );

                    SimpleDateFormat sdfHora =
                            new SimpleDateFormat(
                                    "HH:mm",
                                    Locale.getDefault()
                            );

                    etFecha.setText(
                            sdfFecha.format(clase.fecha)
                    );

                    etHoraInicio.setText(
                            sdfHora.format(clase.horaInicio)
                    );

                    etHoraFin.setText(
                            sdfHora.format(clase.horaFin)
                    );

                    spinnerNivel.setText(
                            clase.nivel,
                            false
                    );

                    spinnerDisciplina.setText(
                            clase.disciplina,
                            false
                    );

                    actualizarDisponibilidad();
                });
    }

    // =========================================================
    // AUXILIARES
    // =========================================================

    private int obtenerIdProfesorSeleccionado() {

        String seleccionado =
                spinnerProfesor.getText().toString();

        for (Usuario usuario : listaUsuariosProfesores) {

            if (usuario.toString().equals(seleccionado)) {

                return usuario.idUsuario;
            }
        }

        return -1;
    }

    private int obtenerIdPistaSeleccionada() {

        String seleccionado =
                spinnerPista.getText().toString();

        for (Pista pista : listaPistasCargadas) {

            if (pista.toString().equals(seleccionado)) {

                return pista.idPista;
            }
        }

        return -1;
    }
}