package com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity encargada de crear y editar una Participacion
 * (binomio jinete-caballo) dentro de una prueba de doma.
 *
 * Funcionalidades principales:
 * - Selección de alumno y equino habilitados para doma.
 * - Asignación automática del orden de salida en modo creación.
 * - Precarga de los datos del binomio en modo edición,
 *   preservando la nota final y el porcentaje ya registrados.
 * - Validación del formulario antes de guardar.
 * - Gestión de estados de operación mediante ViewModel (MVVM).
 */
public class ParticipacionFormActivity extends AppCompatActivity {



    private GestionParticipacionesViewModel viewModel;


    private TextInputLayout      layAlumno;
    private TextInputLayout      layEquino;
    private AutoCompleteTextView spinnerAlumno;
    private AutoCompleteTextView spinnerEquino;
    private TextInputEditText    etOrden;


    private List<Usuario> listaAlumnos = new ArrayList<>();
    private List<Equino>  listaEquinos = new ArrayList<>();

    /**
     * Referencia completa a la participación cargada en modo edición.
     *
     * Se conserva para no perder campos que no se editan
     * en pantalla, como la nota final o el porcentaje.
     */
    private Participacion participacionCargada;



    private int     idPrueba        = -1;
    private int     idParticipacion = -1;
    private boolean modoEdicion     = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participacion_form);

        recogerExtras();
        configurarActionBar();

        viewModel = new ViewModelProvider(this)
                .get(GestionParticipacionesViewModel.class);

        initViews();
        setupToolbar();
        setupBotones();
        observarDatos();
        observarEstado();

        if (!modoEdicion) {
            observarSiguienteOrden();
        }
    }

    // =========================================================
    // INIT
    // =========================================================

    /**
     * Recoge los extras del Intent y determina si la Activity
     * se abre en modo creación o en modo edición.
     */
    private void recogerExtras() {

        idPrueba = getIntent().getIntExtra("ID_PRUEBA", -1);

        if (getIntent().hasExtra("ID_PARTICIPACION")) {
            idParticipacion = getIntent().getIntExtra("ID_PARTICIPACION", -1);
            modoEdicion     = true;
        }
    }

    /**
     * Configura el título del ActionBar según el modo activo
     * y habilita el botón de navegación hacia atrás.
     */
    private void configurarActionBar() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    modoEdicion ? "Editar inscripción" : "Inscribir binomio"
            );
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa las referencias a las vistas del layout.
     *
     * El campo de orden de salida se deja no editable
     * ya que se asigna automáticamente.
     */
    private void initViews() {
        layAlumno     = findViewById(R.id.layAlumnoParticipacion);
        layEquino     = findViewById(R.id.layEquinoParticipacion);
        spinnerAlumno = findViewById(R.id.spinnerAlumno);
        spinnerEquino = findViewById(R.id.spinnerEquino);
        etOrden       = findViewById(R.id.etOrdenSalida);

        etOrden.setEnabled(false);
        etOrden.setFocusable(false);
        etOrden.setClickable(false);
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarParticipacionForm);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Asigna el listener al botón de guardar.
     */
    private void setupBotones() {
        findViewById(R.id.btnGuardarParticipacion)
                .setOnClickListener(v -> guardar());
    }

    // =========================================================
    // OBSERVADORES
    // =========================================================

    /**
     * Observa los alumnos y equinos habilitados para doma
     * y actualiza los spinners correspondientes.
     *
     * Una vez ambas listas están disponibles, intenta precargar
     * los datos de la participación en modo edición.
     */
    private void observarDatos() {

        viewModel.obtenerAlumnosDomaConNombre().observe(this, alumnos -> {

            listaAlumnos = alumnos != null ? alumnos : new ArrayList<>();

            spinnerAlumno.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    listaAlumnos
            ));

            intentarPrecargar();
        });

        viewModel.obtenerEquinosDoma().observe(this, equinos -> {

            listaEquinos = equinos != null ? equinos : new ArrayList<>();

            spinnerEquino.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    listaEquinos
            ));

            intentarPrecargar();
        });
    }

    /**
     * Observa el siguiente número de orden de salida disponible
     * para la prueba y lo muestra en el campo correspondiente.
     *
     * Solo se activa en modo creación.
     */
    private void observarSiguienteOrden() {

        viewModel.obtenerSiguienteOrden(idPrueba).observe(this, orden -> {
            if (orden != null) {
                etOrden.setText(String.valueOf(orden));
            }
        });
    }

    /**
     * Observa el estado de la última operación de guardado.
     *
     * Muestra mensajes de éxito o error y cierra la Activity
     * si la operación fue exitosa.
     */
    private void observarEstado() {

        viewModel.getEstadoOperacion().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_YA_INSCRITO":
                    layAlumno.setError(
                            "Este binomio ya está inscrito en esta prueba"
                    );
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error al guardar",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }


    /**
     * Precarga los datos de la participación en los campos del
     * formulario cuando ambas listas (alumnos y equinos) ya están
     * disponibles y la Activity está en modo edición.
     *
     * Guarda la referencia completa del objeto cargado para
     * preservar campos como la nota final o el porcentaje
     * que no se modifican desde esta pantalla.
     */
    private void intentarPrecargar() {

        if (!modoEdicion
                || listaAlumnos.isEmpty()
                || listaEquinos.isEmpty()) return;

        viewModel.buscarPorId(idParticipacion).observe(this, p -> {

            if (p == null) return;

            participacionCargada = p;

            etOrden.setText(String.valueOf(p.ordenSalida));

            for (Usuario u : listaAlumnos) {
                if (u.idUsuario == p.idAlumno) {
                    spinnerAlumno.setText(u.toString(), false);
                    break;
                }
            }

            for (Equino e : listaEquinos) {
                if (e.idEquino == p.idEquino) {
                    spinnerEquino.setText(e.toString(), false);
                    break;
                }
            }
        });
    }


    /**
     * Valida el formulario, resuelve los IDs seleccionados
     * y delega el guardado al ViewModel.
     *
     * En modo edición modifica únicamente el alumno y el equino
     * del objeto cargado, preservando el resto de campos.
     * En modo creación construye una nueva participación con
     * el orden de salida asignado automáticamente.
     */
    private void guardar() {

        if (!validar()) {
            return;
        }

        int idAlu  = obtenerIdAlumnoSeleccionado();
        int idEqui = obtenerIdEquinoSeleccionado();

        if (modoEdicion) {

            if (participacionCargada != null) {
                participacionCargada.idAlumno = idAlu;
                participacionCargada.idEquino = idEqui;
                viewModel.actualizar(participacionCargada);
            }

        } else {

            int orden = Integer.parseInt(etOrden.getText().toString());
            viewModel.insertar(new Participacion(orden, idAlu, idEqui, idPrueba));
        }
    }


    /**
     * Valida que tanto el alumno como el equino hayan sido
     * seleccionados antes de intentar guardar.
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validar() {

        boolean ok = true;

        if (spinnerAlumno.getText().toString().trim().isEmpty()) {
            layAlumno.setError("Selecciona un alumno");
            ok = false;
        } else {
            layAlumno.setError(null);
        }

        if (spinnerEquino.getText().toString().trim().isEmpty()) {
            layEquino.setError("Selecciona un equino");
            ok = false;
        } else {
            layEquino.setError(null);
        }

        return ok;
    }



    /**
     * Busca y devuelve el ID del alumno seleccionado en el spinner
     * comparando por su representación textual.
     *
     * @return ID del alumno seleccionado, o -1 si no se encuentra.
     */
    private int obtenerIdAlumnoSeleccionado() {

        String seleccionado = spinnerAlumno.getText().toString();

        for (Usuario u : listaAlumnos) {
            if (u.toString().equals(seleccionado)) {
                return u.idUsuario;
            }
        }

        return -1;
    }

    /**
     * Busca y devuelve el ID del equino seleccionado en el spinner
     * comparando por su representación textual.
     *
     * @return ID del equino seleccionado, o -1 si no se encuentra.
     */
    private int obtenerIdEquinoSeleccionado() {

        String seleccionado = spinnerEquino.getText().toString();

        for (Equino e : listaEquinos) {
            if (e.toString().equals(seleccionado)) {
                return e.idEquino;
            }
        }

        return -1;
    }


    /**
     * Cierra la Activity al pulsar la flecha de retroceso
     * del ActionBar nativo.
     *
     * @return true para indicar que la navegación ha sido gestionada.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}