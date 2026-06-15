package com.example.myhipicapptfg.ui.admin.usuarios;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Alumno;
import com.example.myhipicapptfg.datos.local.entidades.Juez;
import com.example.myhipicapptfg.datos.local.entidades.Profesor;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Activity encargada de crear y editar un Usuario del sistema.
 *
 * Gestiona tres tipos de usuarios con formularios dinámicos:
 * - Alumno: disciplinas practicadas (doma/salto) y niveles.
 * - Profesor: disciplinas impartidas, niveles máximos y experiencia.
 * - Juez: número de licencia, federación y estado activo.
 *
 * Funcionalidades principales:
 * - Introducción y validación de datos personales básicos.
 * - Sección específica visible según el tipo de usuario seleccionado.
 * - Carga de foto de perfil desde galería o cámara.
 * - Persistencia mediante ViewModel (MVVM).
 */
public class UsuarioFormActivity extends AppCompatActivity {



    private GestionUsuariosViewModel viewModel;


    private TextInputEditText etNombre;
    private TextInputEditText etApellido1;
    private TextInputEditText etApellido2;
    private TextInputEditText etDni;
    private TextInputEditText etEmail;
    private TextInputEditText etTelefono;
    private TextInputEditText etFechaNacimiento;

    private AutoCompleteTextView spinnerSexo;
    private AutoCompleteTextView spinnerTipo;
    private AutoCompleteTextView spinnerPrefijo;

    private TextInputLayout layNombre;
    private TextInputLayout layApellido1;
    private TextInputLayout layApellido2;
    private TextInputLayout layDni;
    private TextInputLayout layEmail;
    private TextInputLayout layTelefono;
    private TextInputLayout layFechaNacimiento;


    private View     layoutAlumno;
    private CheckBox cbDoma;
    private CheckBox cbSalto;
    private TextInputLayout      layNivelDoma;
    private TextInputLayout      layNivelSalto;
    private AutoCompleteTextView spinnerNivelDoma;
    private AutoCompleteTextView spinnerNivelSalto;


    private View     layoutProfesor;
    private CheckBox cbProfesorDoma;
    private CheckBox cbProfesorSalto;
    private CheckBox cbProfesorActivo;
    private TextInputLayout      layNivelMaximoDoma;
    private TextInputLayout      layNivelMaximoSalto;
    private TextInputLayout      layAniosExperiencia;
    private AutoCompleteTextView spinnerNivelMaximoDoma;
    private AutoCompleteTextView spinnerNivelMaximoSalto;
    private TextInputEditText    etAniosExperiencia;


    private View     layoutJuez;
    private CheckBox cbJuezActivo;
    private TextInputLayout      layNumeroLicencia;
    private AutoCompleteTextView spinnerFederacion;
    private TextInputEditText    etNumeroLicencia;



    private ShapeableImageView imgFotoPerfil;

    /** Launcher para seleccionar una foto desde la galería. */
    private ActivityResultLauncher<String> seleccionarFotoLauncher;

    /** Launcher para tomar una foto con la cámara. */
    private ActivityResultLauncher<Uri>    tomarFotoLauncher;

    /** Launcher para solicitar el permiso de cámara. */
    private ActivityResultLauncher<String> permisoLauncher;


    /** URI de la foto seleccionada o tomada para el perfil. */
    private Uri fotoUri = null;

    /** URI temporal utilizada durante la captura con cámara. */
    private Uri uriCamaraTemp = null;


    /** Timestamp en milisegundos de la fecha de nacimiento seleccionada. */
    private long    fechaNacimientoSeleccionada = -1;
    private boolean modoEdicion                = false;
    private int     usuarioId                  = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_form);

        viewModel = new ViewModelProvider(this)
                .get(GestionUsuariosViewModel.class);

        // Los launchers deben registrarse siempre antes
        registrarLaunchers();

        initViews();
        setupToolbar();
        setupBotones();
        setupSpinners();
        setupFechaNacimiento();
        setupTipoUsuario();
        setupCheckBoxListeners();
        observarEstado();
        recogerExtras();
    }

    // =========================================================
    // INIT
    // =========================================================

    /**
     * Inicializa las referencias a todas las vistas del layout,
     * incluyendo los botones de selección y captura de foto.
     */
    private void initViews() {

        etNombre           = findViewById(R.id.etNombre);
        etApellido1        = findViewById(R.id.etApellido1);
        etApellido2        = findViewById(R.id.etApellido2);
        etDni              = findViewById(R.id.etDni);
        etEmail            = findViewById(R.id.etEmail);
        etTelefono         = findViewById(R.id.etTelefono);
        etFechaNacimiento  = findViewById(R.id.etFechaNacimiento);

        spinnerSexo        = findViewById(R.id.spinnerSexo);
        spinnerTipo        = findViewById(R.id.spinnerTipo);
        spinnerPrefijo     = findViewById(R.id.spinnerPrefijo);

        layNombre          = findViewById(R.id.layNombre);
        layApellido1       = findViewById(R.id.layApellido1);
        layApellido2       = findViewById(R.id.layApellido2);
        layDni             = findViewById(R.id.layDni);
        layEmail           = findViewById(R.id.layEmail);
        layTelefono        = findViewById(R.id.layTelefono);
        layFechaNacimiento = findViewById(R.id.layFechaNacimiento);

        layoutAlumno       = findViewById(R.id.layoutAlumno);
        cbDoma             = findViewById(R.id.cbDoma);
        cbSalto            = findViewById(R.id.cbSalto);
        layNivelDoma       = findViewById(R.id.layNivelDoma);
        layNivelSalto      = findViewById(R.id.layNivelSalto);
        spinnerNivelDoma   = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto  = findViewById(R.id.spinnerNivelSalto);

        layoutProfesor          = findViewById(R.id.layoutProfesor);
        cbProfesorDoma          = findViewById(R.id.cbProfesorDoma);
        cbProfesorSalto         = findViewById(R.id.cbProfesorSalto);
        cbProfesorActivo        = findViewById(R.id.cbProfesorActivo);
        layNivelMaximoDoma      = findViewById(R.id.layNivelMaximoDoma);
        layNivelMaximoSalto     = findViewById(R.id.layNivelMaximoSalto);
        layAniosExperiencia     = findViewById(R.id.layAniosExperiencia);
        spinnerNivelMaximoDoma  = findViewById(R.id.spinnerNivelMaximoDoma);
        spinnerNivelMaximoSalto = findViewById(R.id.spinnerNivelMaximoSalto);
        etAniosExperiencia      = findViewById(R.id.etAniosExperiencia);

        layoutJuez        = findViewById(R.id.layoutJuez);
        layNumeroLicencia = findViewById(R.id.layNumeroLicencia);
        spinnerFederacion = findViewById(R.id.spinnerFederacion);
        cbJuezActivo      = findViewById(R.id.cbJuezActivo);
        etNumeroLicencia  = findViewById(R.id.etNumeroLicencia);

        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);

        findViewById(R.id.btnSeleccionarFoto).setOnClickListener(v ->
                seleccionarFotoLauncher.launch("image/*")
        );

        findViewById(R.id.btnTomarFoto).setOnClickListener(v ->
                solicitarCamara()
        );
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Asigna el listener al botón de guardar.
     */
    private void setupBotones() {
        findViewById(R.id.btnGuardar)
                .setOnClickListener(v -> guardarUsuario());
    }

    /**
     * Configura los adaptadores de todos los spinners del formulario
     * con los valores constantes definidos en las entidades.
     */
    private void setupSpinners() {

        spinnerSexo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{ Usuario.SEXO_FEMENINO, Usuario.SEXO_MASCULINO }));

        spinnerTipo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{
                        Usuario.TIPO_ALUMNO,
                        Usuario.TIPO_PROFESOR,
                        Usuario.TIPO_PROPIETARIO,
                        Usuario.TIPO_JUEZ
                }));

        spinnerPrefijo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{ "+34", "+33", "+44", "+49", "+39" }));

        String[] niveles = { Alumno.PRINCIPIANTE, Alumno.INTERMEDIO, Alumno.AVANZADO };
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, niveles);
        spinnerNivelDoma.setAdapter(adapterNiveles);
        spinnerNivelSalto.setAdapter(adapterNiveles);

        String[] nivelesProf = { Profesor.PRINCIPIANTE, Profesor.INTERMEDIO, Profesor.AVANZADO };
        ArrayAdapter<String> adapterProf = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, nivelesProf);
        spinnerNivelMaximoDoma.setAdapter(adapterProf);
        spinnerNivelMaximoSalto.setAdapter(adapterProf);

        spinnerFederacion.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{ Juez.ESPAÑOLA, Juez.BRITANICA, Juez.FRANCESA, Juez.INTERNACIONAL }));
    }

    /**
     * Configura el campo de fecha de nacimiento para abrir
     * un DatePickerDialog al ser pulsado.
     */
    private void setupFechaNacimiento() {

        etFechaNacimiento.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            new DatePickerDialog(this,
                    (view, year, month, day) -> {

                        Calendar fecha = Calendar.getInstance();
                        fecha.set(year, month, day);
                        fechaNacimientoSeleccionada = fecha.getTimeInMillis();
                        etFechaNacimiento.setText(
                                day + "/" + (month + 1) + "/" + year
                        );
                        layFechaNacimiento.setError(null);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    /**
     * Configura el spinner de tipo de usuario para mostrar u ocultar
     * dinámicamente la sección específica según el tipo seleccionado.
     */
    private void setupTipoUsuario() {

        spinnerTipo.setOnItemClickListener((parent, view, position, id) ->
                actualizarVisibilidadPorTipo()
        );

        actualizarVisibilidadPorTipo();
    }

    /**
     * Configura los listeners de los checkboxes de disciplinas para
     * mostrar u ocultar los spinners de nivel correspondientes.
     */
    private void setupCheckBoxListeners() {

        cbDoma.setOnCheckedChangeListener((b, checked) ->
                layNivelDoma.setVisibility(checked ? View.VISIBLE : View.GONE));

        cbSalto.setOnCheckedChangeListener((b, checked) ->
                layNivelSalto.setVisibility(checked ? View.VISIBLE : View.GONE));

        cbProfesorDoma.setOnCheckedChangeListener((b, checked) ->
                layNivelMaximoDoma.setVisibility(checked ? View.VISIBLE : View.GONE));

        cbProfesorSalto.setOnCheckedChangeListener((b, checked) ->
                layNivelMaximoSalto.setVisibility(checked ? View.VISIBLE : View.GONE));
    }

    /**
     * Lee los extras del Intent y, si corresponde a modo edición,
     * carga los datos del usuario en el formulario.
     */
    private void recogerExtras() {

        if (getIntent().hasExtra("ID_USUARIO")) {
            usuarioId   = getIntent().getIntExtra("ID_USUARIO", -1);
            modoEdicion = true;
            cargarUsuario(usuarioId);
        }
    }


    /**
     * Observa el usuario por su ID y rellena los campos básicos
     * del formulario con sus datos actuales.
     *
     * Una vez cargado el tipo, delega la carga de los datos
     * específicos a {@link #cargarDatosEspecificos(int, String)}.
     *
     * @param id Identificador del usuario a cargar.
     */
    private void cargarUsuario(int id) {

        viewModel.buscarPorId(id).observe(this, usuario -> {

            if (usuario == null) return;

            etNombre.setText(usuario.nombre);
            etApellido1.setText(usuario.apellido1);
            etApellido2.setText(usuario.apellido2);
            etDni.setText(usuario.dni);
            etEmail.setText(usuario.email);

            cargarTelefono(usuario.telefono);

            spinnerSexo.setText(usuario.sexo, false);
            spinnerTipo.setText(usuario.tipo, false);

            fechaNacimientoSeleccionada = usuario.fechaNacimiento;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(usuario.fechaNacimiento);
            etFechaNacimiento.setText(
                    c.get(Calendar.DAY_OF_MONTH) + "/"
                            + (c.get(Calendar.MONTH) + 1) + "/"
                            + c.get(Calendar.YEAR)
            );

            if (usuario.fotoPerfil != null && !usuario.fotoPerfil.isEmpty()) {
                fotoUri = Uri.parse(usuario.fotoPerfil);
                imgFotoPerfil.setImageURI(fotoUri);
            }

            actualizarVisibilidadPorTipo();
            cargarDatosEspecificos(usuario.idUsuario, usuario.tipo);
        });
    }

    /**
     * Separa el prefijo internacional del número de teléfono almacenado
     * y los muestra en sus respectivos campos del formulario.
     *
     * @param telefonoCompleto Teléfono con prefijo tal como está guardado.
     */
    private void cargarTelefono(String telefonoCompleto) {

        if (telefonoCompleto == null) return;

        telefonoCompleto = telefonoCompleto.trim();

        String prefijoEncontrado  = "+34";
        String numeroSinPrefijo   = telefonoCompleto;

        ArrayAdapter<String> adapter =
                (ArrayAdapter<String>) spinnerPrefijo.getAdapter();

        if (adapter != null) {
            for (int i = 0; i < adapter.getCount(); i++) {
                String prefijo = adapter.getItem(i);
                if (prefijo != null && telefonoCompleto.startsWith(prefijo)) {
                    prefijoEncontrado = prefijo;
                    numeroSinPrefijo  = telefonoCompleto
                            .substring(prefijo.length()).trim();
                    break;
                }
            }
        }

        spinnerPrefijo.setText(prefijoEncontrado, false);
        etTelefono.setText(numeroSinPrefijo);
    }

    /**
     * Carga los datos específicos del usuario según su tipo
     * (Alumno, Profesor o Juez) y rellena la sección correspondiente.
     *
     * @param id   Identificador del usuario.
     * @param tipo Tipo de usuario (constante de {@link Usuario}).
     */
    private void cargarDatosEspecificos(int id, String tipo) {

        if (Usuario.TIPO_ALUMNO.equals(tipo)) {

            viewModel.getAlumno(id).observe(this, alumno -> {

                if (alumno == null) return;

                cbDoma.setChecked(alumno.practicaDoma);
                cbSalto.setChecked(alumno.practicaSalto);
                spinnerNivelDoma.setText(alumno.nivelDoma, false);
                spinnerNivelSalto.setText(alumno.nivelSalto, false);
            });

        } else if (Usuario.TIPO_PROFESOR.equals(tipo)) {

            viewModel.getProfesor(id).observe(this, profesor -> {

                if (profesor == null) return;

                cbProfesorDoma.setChecked(profesor.puedeDarDoma);
                cbProfesorSalto.setChecked(profesor.puedeDarSalto);
                cbProfesorActivo.setChecked(profesor.activo);
                spinnerNivelMaximoDoma.setText(profesor.nivelMaximoDoma, false);
                spinnerNivelMaximoSalto.setText(profesor.nivelMaximoSalto, false);
                etAniosExperiencia.setText(String.valueOf(profesor.aniosExperiencia));
            });

        } else if (Usuario.TIPO_JUEZ.equals(tipo)) {

            viewModel.getJuez(id).observe(this, juez -> {

                if (juez == null) return;

                etNumeroLicencia.setText(juez.numeroLicencia);
                spinnerFederacion.setText(juez.federacion, false);
                cbJuezActivo.setChecked(juez.activo);
            });
        }
    }

    /**
     * Muestra únicamente la sección del formulario correspondiente
     * al tipo de usuario seleccionado en el spinner, ocultando el resto.
     */
    private void actualizarVisibilidadPorTipo() {

        String tipo = spinnerTipo.getText().toString().trim();

        layoutAlumno.setVisibility(View.GONE);
        layoutProfesor.setVisibility(View.GONE);
        layoutJuez.setVisibility(View.GONE);

        switch (tipo) {
            case Usuario.TIPO_ALUMNO:
                layoutAlumno.setVisibility(View.VISIBLE);
                break;
            case Usuario.TIPO_PROFESOR:
                layoutProfesor.setVisibility(View.VISIBLE);
                break;
            case Usuario.TIPO_JUEZ:
                layoutJuez.setVisibility(View.VISIBLE);
                break;
        }

        resetVisibilidadSubniveles();
    }

    /**
     * Oculta y limpia los campos de subnivel que se muestran
     * condicionalmente al marcar los checkboxes de disciplina.
     */
    private void resetVisibilidadSubniveles() {

        layNivelDoma.setVisibility(View.GONE);
        layNivelSalto.setVisibility(View.GONE);
        layNivelMaximoDoma.setVisibility(View.GONE);
        layNivelMaximoSalto.setVisibility(View.GONE);

        etNumeroLicencia.setText("");
        spinnerFederacion.setText("");
        cbJuezActivo.setChecked(false);
    }


    /**
     * Observa el estado de la última operación de guardado.
     *
     * Muestra mensajes de éxito o error y cierra la Activity
     * si la operación fue exitosa. En caso de error de negocio,
     * señala el campo afectado.
     */
    private void observarEstado() {

        viewModel.getEstado().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_EMAIL_DUPLICADO":
                    layEmail.setError("Email duplicado");
                    break;

                case "ERROR_DNI_DUPLICADO":
                    layDni.setError("DNI duplicado");
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error BD",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }


    /**
     * Valida el formulario, construye los objetos Usuario y su entidad
     * específica (Alumno, Profesor o Juez) y delega el guardado
     * al ViewModel.
     *
     * En modo edición actualiza el usuario existente.
     * En modo creación inserta uno nuevo junto a su entidad específica.
     */
    private void guardarUsuario() {

        if (!validar()) return;

        String tipo = spinnerTipo.getText().toString().trim();

        Usuario u = new Usuario(
                etEmail.getText().toString().trim(),
                spinnerPrefijo.getText().toString().trim()
                        + " " + etTelefono.getText().toString().trim(),
                etApellido1.getText().toString().trim(),
                etApellido2.getText().toString().trim(),
                etDni.getText().toString().trim(),
                etNombre.getText().toString().trim(),
                fechaNacimientoSeleccionada,
                System.currentTimeMillis(),
                spinnerSexo.getText().toString().trim(),
                tipo,
                fotoUri != null ? fotoUri.toString() : null
        );

        if (modoEdicion) {
            u.idUsuario = usuarioId;
        }

        Alumno   alumno   = construirAlumno(tipo);
        Profesor profesor = construirProfesor(tipo);
        Juez     juez     = construirJuez(tipo);

        if (modoEdicion) {
            viewModel.actualizarUsuarioCompleto(u, alumno, profesor, juez);
        } else {
            viewModel.guardarUsuarioCompleto(u, alumno, profesor, juez);
        }
    }

    /**
     * Construye el objeto Alumno con los datos del formulario
     * si el tipo seleccionado es Alumno, o null en caso contrario.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @return Alumno construido, o null si el tipo no corresponde.
     */
    private Alumno construirAlumno(String tipo) {

        if (!Usuario.TIPO_ALUMNO.equals(tipo)) return null;

        Alumno alumno = new Alumno();
        alumno.idAlumno      = usuarioId;
        alumno.practicaDoma  = cbDoma.isChecked();
        alumno.practicaSalto = cbSalto.isChecked();
        alumno.nivelDoma     = cbDoma.isChecked()
                ? spinnerNivelDoma.getText().toString().trim() : null;
        alumno.nivelSalto    = cbSalto.isChecked()
                ? spinnerNivelSalto.getText().toString().trim() : null;

        return alumno;
    }

    /**
     * Construye el objeto Profesor con los datos del formulario
     * si el tipo seleccionado es Profesor, o null en caso contrario.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @return Profesor construido, o null si el tipo no corresponde.
     */
    private Profesor construirProfesor(String tipo) {

        if (!Usuario.TIPO_PROFESOR.equals(tipo)) return null;

        Profesor profesor = new Profesor();
        profesor.idProfesor        = usuarioId;
        profesor.puedeDarDoma      = cbProfesorDoma.isChecked();
        profesor.puedeDarSalto     = cbProfesorSalto.isChecked();
        profesor.activo            = cbProfesorActivo.isChecked();
        profesor.nivelMaximoDoma   = cbProfesorDoma.isChecked()
                ? spinnerNivelMaximoDoma.getText().toString().trim() : null;
        profesor.nivelMaximoSalto  = cbProfesorSalto.isChecked()
                ? spinnerNivelMaximoSalto.getText().toString().trim() : null;
        profesor.aniosExperiencia  = Integer.parseInt(
                etAniosExperiencia.getText().toString().trim()
        );

        return profesor;
    }

    /**
     * Construye el objeto Juez con los datos del formulario
     * si el tipo seleccionado es Juez, o null en caso contrario.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @return Juez construido, o null si el tipo no corresponde.
     */
    private Juez construirJuez(String tipo) {

        if (!Usuario.TIPO_JUEZ.equals(tipo)) return null;

        Juez juez = new Juez();
        juez.idJuez         = usuarioId;
        juez.numeroLicencia = etNumeroLicencia.getText().toString().trim();
        juez.federacion     = spinnerFederacion.getText().toString().trim();
        juez.activo         = cbJuezActivo.isChecked();

        return juez;
    }


    /**
     * Valida todos los campos del formulario antes de guardar.
     *
     * Comprobaciones comunes:
     * - Nombre, apellidos y teléfono (obligatorios).
     * - DNI con formato 8 dígitos + letra mayúscula.
     * - Email con formato válido.
     * - Fecha de nacimiento seleccionada.
     *
     * Comprobaciones específicas por tipo:
     * - Alumno: al menos una disciplina y nivel si está marcada.
     * - Profesor: al menos una disciplina, niveles máximos y experiencia.
     * - Juez: número de licencia con formato válido y federación.
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validar() {

        boolean ok = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            layNombre.setError("Obligatorio");
            ok = false;
        } else layNombre.setError(null);

        if (etApellido1.getText().toString().trim().isEmpty()) {
            layApellido1.setError("Obligatorio");
            ok = false;
        } else layApellido1.setError(null);

        if (etApellido2.getText().toString().trim().isEmpty()) {
            layApellido2.setError("Obligatorio");
            ok = false;
        } else layApellido2.setError(null);

        if (etTelefono.getText().toString().trim().isEmpty()) {
            layTelefono.setError("Obligatorio");
            ok = false;
        } else layTelefono.setError(null);

        if (!etDni.getText().toString().trim().matches("\\d{8}[A-Z]")) {
            layDni.setError("DNI inválido");
            ok = false;
        } else layDni.setError(null);

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(etEmail.getText().toString().trim()).matches()) {
            layEmail.setError("Email inválido");
            ok = false;
        } else layEmail.setError(null);

        if (fechaNacimientoSeleccionada <= 0) {
            layFechaNacimiento.setError("Selecciona fecha");
            ok = false;
        } else layFechaNacimiento.setError(null);

        String tipo = spinnerTipo.getText().toString().trim();

        ok = validarAlumno(tipo, ok);
        ok = validarProfesor(tipo, ok);
        ok = validarJuez(tipo, ok);

        return ok;
    }

    /**
     * Valida los campos específicos de la sección Alumno.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @param ok   Estado de validación acumulado.
     * @return Estado de validación actualizado.
     */
    private boolean validarAlumno(String tipo, boolean ok) {

        if (!Usuario.TIPO_ALUMNO.equals(tipo)) return ok;

        if (!cbDoma.isChecked() && !cbSalto.isChecked()) {
            Toast.makeText(this,
                    "Selecciona al menos una disciplina",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (cbDoma.isChecked()
                && spinnerNivelDoma.getText().toString().trim().isEmpty()) {
            layNivelDoma.setError("Selecciona nivel");
            ok = false;
        } else layNivelDoma.setError(null);

        if (cbSalto.isChecked()
                && spinnerNivelSalto.getText().toString().trim().isEmpty()) {
            layNivelSalto.setError("Selecciona nivel");
            ok = false;
        } else layNivelSalto.setError(null);

        return ok;
    }

    /**
     * Valida los campos específicos de la sección Profesor.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @param ok   Estado de validación acumulado.
     * @return Estado de validación actualizado.
     */
    private boolean validarProfesor(String tipo, boolean ok) {

        if (!Usuario.TIPO_PROFESOR.equals(tipo)) return ok;

        if (!cbProfesorDoma.isChecked() && !cbProfesorSalto.isChecked()) {
            Toast.makeText(this,
                    "Selecciona al menos una disciplina",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (cbProfesorDoma.isChecked()
                && spinnerNivelMaximoDoma.getText().toString().trim().isEmpty()) {
            layNivelMaximoDoma.setError("Selecciona nivel máximo");
            ok = false;
        } else layNivelMaximoDoma.setError(null);

        if (cbProfesorSalto.isChecked()
                && spinnerNivelMaximoSalto.getText().toString().trim().isEmpty()) {
            layNivelMaximoSalto.setError("Selecciona nivel máximo");
            ok = false;
        } else layNivelMaximoSalto.setError(null);

        if (etAniosExperiencia.getText().toString().trim().isEmpty()) {
            layAniosExperiencia.setError("Introduce años de experiencia");
            ok = false;
        } else layAniosExperiencia.setError(null);

        return ok;
    }

    /**
     * Valida los campos específicos de la sección Juez.
     *
     * @param tipo Tipo de usuario seleccionado.
     * @param ok   Estado de validación acumulado.
     * @return Estado de validación actualizado.
     */
    private boolean validarJuez(String tipo, boolean ok) {

        if (!Usuario.TIPO_JUEZ.equals(tipo)) return ok;

        String lic = etNumeroLicencia.getText().toString().trim();

        if (lic.isEmpty()) {
            layNumeroLicencia.setError("Introduce número de licencia");
            ok = false;
        } else if (!lic.matches("^(RFHE|FEI|FFE|BEF)-?\\d{4,10}$")) {
            layNumeroLicencia.setError("Formato inválido (ej: FEI-12345)");
            ok = false;
        } else {
            layNumeroLicencia.setError(null);
        }

        if (spinnerFederacion.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Selecciona federación",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        return ok;
    }


    /**
     * Registra los launchers de actividad necesarios para la gestión
     * de la foto de perfil antes de que se cree la Activity.
     *
     * Launchers registrados:
     * - Selección de foto desde galería.
     * - Captura de foto con la cámara.
     * - Solicitud del permiso de cámara.
     */
    private void registrarLaunchers() {

        seleccionarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri == null) return;

                    getContentResolver().takePersistableUriPermission(
                            uri,
                            android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

                    fotoUri = uri;
                    imgFotoPerfil.setImageURI(uri);
                }
        );

        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && uriCamaraTemp != null) {
                        fotoUri = uriCamaraTemp;
                        imgFotoPerfil.setImageURI(fotoUri);
                    }
                }
        );

        permisoLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        abrirCamara();
                    } else {
                        Toast.makeText(this,
                                "Permiso de cámara denegado",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * Comprueba si el permiso de cámara está concedido y,
     * en caso contrario, lo solicita antes de abrir la cámara.
     */
    private void solicitarCamara() {

        boolean tienePermiso = ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;

        if (tienePermiso) {
            abrirCamara();
        } else {
            permisoLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Crea un archivo temporal para la foto y lanza la cámara
     * del dispositivo apuntando a ese archivo mediante FileProvider.
     *
     * Si la creación del archivo falla, muestra un mensaje de error.
     */
    private void abrirCamara() {

        try {
            String ts = new SimpleDateFormat(
                    "yyyyMMdd_HHmmss", Locale.getDefault()
            ).format(new Date());

            File foto = File.createTempFile(
                    "FOTO_" + ts, ".jpg",
                    getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );

            uriCamaraTemp = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    foto
            );

            tomarFotoLauncher.launch(uriCamaraTemp);

        } catch (IOException e) {
            Toast.makeText(this,
                    "Error al crear archivo de foto",
                    Toast.LENGTH_SHORT).show();
        }
    }
}