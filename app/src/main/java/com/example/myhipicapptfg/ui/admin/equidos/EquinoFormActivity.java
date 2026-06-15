package com.example.myhipicapptfg.ui.admin.equidos;

import android.app.DatePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Activity encargada de crear y editar un Equino.
 *
 * Funcionalidades principales:
 * - Introducción y validación de los datos del equino
 *   (nombre, microchip, fecha de nacimiento, raza, sexo,
 *   temperamento, estado de salud, cuadra, altura y peso).
 * - Selección opcional de propietario desde un spinner dinámico.
 * - Selección opcional de especialidades (doma y/o salto).
 * - Carga de foto de perfil desde galería o cámara.
 * - Persistencia mediante ViewModel (MVVM).
 */
public class EquinoFormActivity extends AppCompatActivity {


    private GestionEquinoViewModel viewModel;

    // Vistas
    private TextInputEditText etNombre;
    private TextInputEditText etMicrochip;
    private TextInputEditText etFechaNacimiento;
    private TextInputEditText etAltura;
    private TextInputEditText etPeso;
    private TextInputEditText etCuadra;

    // Layout para mostrar errores
    private TextInputLayout layNombre;
    private TextInputLayout layMicrochip;
    private TextInputLayout layFechaNacimiento;
    private TextInputLayout layAltura;
    private TextInputLayout layPeso;
    private TextInputLayout layCuadra;
    private TextInputLayout layRaza;
    private TextInputLayout layPropietario;

   // Spinners
    private AutoCompleteTextView spinnerRaza;
    private AutoCompleteTextView spinnerSexo;
    private AutoCompleteTextView spinnerTemperamento;
    private AutoCompleteTextView spinnerSalud;
    private AutoCompleteTextView spinnerPropietario;


    private CheckBox cbTienePropietario;
    private CheckBox cbTieneEspecialidades;
    private CheckBox cbDoma;
    private CheckBox cbSalto;
    private View     layoutEspecialidades;


    // Foto del Equino
    private ShapeableImageView imgFotoEquino;


    /** Launcher para seleccionar una foto desde la galería. */
    private ActivityResultLauncher<String>  seleccionarFotoLauncher;

    /** Launcher para tomar una foto con la cámara. */
    private ActivityResultLauncher<Uri>     tomarFotoLauncher;

    /** Launcher para solicitar el permiso de cámara. */
    private ActivityResultLauncher<String>  permisoLauncher;


    /**
     * Lista de propietarios cargados desde la base de datos.
     * Se utiliza para resolver el ID del propietario seleccionado al guardar.
     */
    private List<Usuario> listaPropietariosCargados = new ArrayList<>();


    /** URI de la foto seleccionada o tomada para el equino. */
    private Uri fotoUri = null;

    /** URI temporal utilizada durante la captura con cámara. */
    private Uri uriCamaraTemp = null;

    /** Timestamp en milisegundos de la fecha de nacimiento seleccionada. */
    private long fechaNacimientoSeleccionada = -1;

    private boolean modoEdicion = false;
    private int     equinoId   = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_equino_form);

        viewModel = new ViewModelProvider(this)
                .get(GestionEquinoViewModel.class);

        registrarLaunchers();
        initViews();
        setupToolbar();
        setupBotones();
        setupSpinners();
        setupFechaNacimiento();
        setupCheckBoxListeners();
        observarEstado();
        recogerExtras();
    }


    /**
     * Inicializa las referencias a todas las vistas del layout.
     *
     * Configura también los listeners de los botones de foto
     * para seleccionar desde galería o tomar con cámara.
     */
    private void initViews() {

        etNombre          = findViewById(R.id.etNombreEquino);
        etMicrochip       = findViewById(R.id.etMicrochip);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etAltura          = findViewById(R.id.etAltura);
        etPeso            = findViewById(R.id.etPeso);
        etCuadra          = findViewById(R.id.etCuadra);

        layNombre          = findViewById(R.id.layNombreEquino);
        layMicrochip       = findViewById(R.id.layMicrochip);
        layFechaNacimiento = findViewById(R.id.layFechaNacimiento);
        layCuadra          = findViewById(R.id.layCuadra);
        layRaza            = findViewById(R.id.layRaza);
        layPropietario     = findViewById(R.id.layPropietario);

        spinnerRaza        = findViewById(R.id.etRaza);
        spinnerSexo        = findViewById(R.id.spinnerSexoEquino);
        spinnerTemperamento= findViewById(R.id.spinnerTemperamento);
        spinnerSalud       = findViewById(R.id.spinnerSalud);
        spinnerPropietario = findViewById(R.id.spinnerPropietario);

        cbTienePropietario    = findViewById(R.id.cbTienePropietario);
        cbTieneEspecialidades = findViewById(R.id.cbTieneEspecialidadesEquino);
        cbDoma                = findViewById(R.id.cbDomaClasicaEquino);
        cbSalto               = findViewById(R.id.cbSaltoEquino);
        layoutEspecialidades  = findViewById(R.id.layoutEspecialidadesEquino);

        imgFotoEquino = findViewById(R.id.imgFotoEquino);

        findViewById(R.id.btnSeleccionarFotoEquino)
                .setOnClickListener(v ->
                        seleccionarFotoLauncher.launch("image/*")
                );

        findViewById(R.id.btnTomarFotoEquino)
                .setOnClickListener(v -> solicitarCamara());
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarEquino);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Asigna el listener al botón de guardar.
     */
    private void setupBotones() {
        findViewById(R.id.btnGuardarEquino)
                .setOnClickListener(v -> guardarEquino());
    }

    /**
     * Configura los adaptadores de los spinners con los valores
     * constantes de la entidad Equino y carga los propietarios
     * disponibles desde el ViewModel.
     */
    private void setupSpinners() {

        String[] razas = {
                "Pura Raza Española (PRE)", "Árabe", "Cuarto de Milla",
                "Pura Sangre Inglés", "Appaloosa", "Frisón", "CDE",
                "Lusitano", "Hispano-Árabe", "Anglo-Árabe",
                "Poni Galés", "Shetland"
        };

        spinnerRaza.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, razas));

        spinnerSexo.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new String[]{ Equino.SEXO_MACHO, Equino.SEXO_HEMBRA }));

        spinnerTemperamento.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new String[]{ Equino.FACIL, Equino.MANEJABLE, Equino.DIFICIL }));

        spinnerSalud.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line,
                new String[]{ Equino.BUENO, Equino.REGULAR, Equino.MALO }));

        observarPropietarios();
    }

    /**
     * Configura el campo de fecha de nacimiento para abrir
     * un DatePickerDialog al ser pulsado.
     */
    private void setupFechaNacimiento() {

        etFechaNacimiento.setOnClickListener(v -> {

            Calendar c = Calendar.getInstance();

            if (fechaNacimientoSeleccionada > 0) {
                c.setTimeInMillis(fechaNacimientoSeleccionada);
            }

            new DatePickerDialog(this, (view, year, month, day) -> {

                Calendar fecha = Calendar.getInstance();
                fecha.set(year, month, day);
                fechaNacimientoSeleccionada = fecha.getTimeInMillis();

                etFechaNacimiento.setText(
                        day + "/" + (month + 1) + "/" + year
                );

                layFechaNacimiento.setError(null);

            }, c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    /**
     * Configura los listeners de los checkboxes de propietario
     * y especialidades para mostrar u ocultar sus secciones asociadas.
     */
    private void setupCheckBoxListeners() {

        cbTienePropietario.setOnCheckedChangeListener((b, isChecked) -> {
            layPropietario.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) spinnerPropietario.setText("");
        });

        cbTieneEspecialidades.setOnCheckedChangeListener((b, isChecked) ->
                layoutEspecialidades.setVisibility(
                        isChecked ? View.VISIBLE : View.GONE
                )
        );
    }

    /**
     * Lee los extras del Intent y, si corresponde a modo edición,
     * carga los datos del equino en el formulario.
     */
    private void recogerExtras() {

        if (getIntent().hasExtra("ID_EQUINO")) {
            equinoId   = getIntent().getIntExtra("ID_EQUINO", -1);
            modoEdicion = true;
            cargarEquino(equinoId);
        }
    }



    /**
     * Observa el equino por su ID y rellena todos los campos
     * del formulario con sus datos actuales.
     *
     * @param id Identificador del equino a cargar.
     */
    private void cargarEquino(int id) {

        viewModel.buscarPorId(id).observe(this, equino -> {

            if (equino == null) {
                return;
            }

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
            etFechaNacimiento.setText(
                    c.get(Calendar.DAY_OF_MONTH) + "/"
                            + (c.get(Calendar.MONTH) + 1) + "/"
                            + c.get(Calendar.YEAR)
            );

            cbDoma.setChecked(equino.sabeDoma);
            cbSalto.setChecked(equino.sabeSalto);

            if (equino.sabeDoma || equino.sabeSalto) {
                cbTieneEspecialidades.setChecked(true);
            }

            if (equino.idUsuario != null) {
                cbTienePropietario.setChecked(true);
                layPropietario.setVisibility(View.VISIBLE);
            }

            if (equino.fotoPerfil != null && !equino.fotoPerfil.isEmpty()) {
                fotoUri = Uri.parse(equino.fotoPerfil);
                imgFotoEquino.setImageURI(fotoUri);
            }
        });
    }


    /**
     * Observa la lista de propietarios disponibles y actualiza
     * el spinner de propietario cuando cambian los datos.
     */
    private void observarPropietarios() {

        viewModel.obtenerPropietarios().observe(this, usuarios -> {

            if (usuarios == null) {
                return;
            }

            listaPropietariosCargados = usuarios;

            spinnerPropietario.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    usuarios
            ));
        });
    }

    /**
     * Observa el estado de la última operación de guardado
     * y muestra mensajes de éxito o error en la interfaz.
     *
     * En caso de éxito cierra la Activity.
     * En caso de error de negocio, señala el campo afectado.
     */
    private void observarEstado() {

        viewModel.getEstadoOperacion().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Caballo guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_MICROCHIP_DUPLICADO":
                    layMicrochip.setError(
                            "Este microchip ya pertenece a otro equino"
                    );
                    etMicrochip.requestFocus();
                    break;

                case "ERROR_CUADRA_OCUPADA":
                    layCuadra.setError(
                            "Esta cuadra ya está asignada a otro caballo"
                    );
                    etCuadra.requestFocus();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error crítico en la base de datos",
                            Toast.LENGTH_LONG).show();
                    break;
            }
        });
    }


    /**
     * Valida el formulario, construye el objeto Equino con los
     * datos introducidos y delega el guardado al ViewModel.
     *
     * En modo edición actualiza el equino existente.
     * En modo creación inserta uno nuevo.
     */
    private void guardarEquino() {

        if (!validar()) {
            return;
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
                obtenerIdPropietarioSeleccionado(),
                Integer.parseInt(etCuadra.getText().toString().trim()),
                fotoUri != null ? fotoUri.toString() : null
        );

        if (modoEdicion) {
            e.idEquino = equinoId;
            viewModel.actualizar(e);
        } else {
            viewModel.insertar(e);
        }
    }

    /**
     * Busca y devuelve el ID del propietario seleccionado en el spinner
     * comparando por representación textual.
     *
     * @return ID del propietario seleccionado, o null si no hay ninguno
     *         seleccionado o el checkbox de propietario está desmarcado.
     */
    private Integer obtenerIdPropietarioSeleccionado() {

        if (!cbTienePropietario.isChecked()) return null;

        String seleccion = spinnerPropietario.getText().toString();

        for (Usuario u : listaPropietariosCargados) {
            if (u.toString().equals(seleccion)) {
                return u.idUsuario;
            }
        }

        return null;
    }

    // =========================================================
    // VALIDACIÓN
    // =========================================================

    /**
     * Valida todos los campos obligatorios del formulario.
     *
     * Comprobaciones realizadas:
     * - Nombre del equino (obligatorio).
     * - Raza seleccionada (obligatoria).
     * - Microchip de 15 dígitos numéricos (obligatorio).
     * - Fecha de nacimiento seleccionada (obligatoria).
     * - Número de cuadra (obligatorio).
     * - Propietario seleccionado si el checkbox está marcado.
     * - Al menos una disciplina si el checkbox de especialidades
     *   está marcado.
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validar() {

        boolean ok = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            layNombre.setError("El nombre es obligatorio");
            ok = false;
        } else {
            layNombre.setError(null);
        }

        if (spinnerRaza.getText().toString().trim().isEmpty()) {
            layRaza.setError("Selecciona una raza");
            ok = false;
        } else {
            layRaza.setError(null);
        }

        String microchip = etMicrochip.getText().toString().trim();
        if (microchip.isEmpty()) {
            layMicrochip.setError("Obligatorio");
            ok = false;
        } else if (!microchip.matches("\\d{15}")) {
            layMicrochip.setError(
                    "Debe tener 15 dígitos numéricos. Ej: 981001234567890"
            );
            ok = false;
        } else {
            layMicrochip.setError(null);
        }

        if (fechaNacimientoSeleccionada <= 0) {
            layFechaNacimiento.setError("Selecciona fecha");
            ok = false;
        } else {
            layFechaNacimiento.setError(null);
        }

        if (etCuadra.getText().toString().trim().isEmpty()) {
            layCuadra.setError("Obligatorio");
            ok = false;
        } else {
            layCuadra.setError(null);
        }

        if (cbTienePropietario.isChecked()) {
            if (spinnerPropietario.getText().toString().trim().isEmpty()) {
                layPropietario.setError("Debes seleccionar un propietario");
                ok = false;
            } else {
                layPropietario.setError(null);
            }
        }

        if (cbTieneEspecialidades.isChecked()) {
            if (!cbDoma.isChecked() && !cbSalto.isChecked()) {
                Toast.makeText(this,
                        "Selecciona al menos una disciplina",
                        Toast.LENGTH_SHORT).show();
                ok = false;
            }
        }

        return ok;
    }



    /**
     * Registra los launchers de actividad necesarios para la gestión
     * de la foto de perfil del equino antes de que se cree la Activity.
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
                    imgFotoEquino.setImageURI(uri);
                }
        );

        tomarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && uriCamaraTemp != null) {
                        fotoUri = uriCamaraTemp;
                        imgFotoEquino.setImageURI(fotoUri);
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

        boolean tienePermiso =
                androidx.core.content.ContextCompat.checkSelfPermission(
                        this, android.Manifest.permission.CAMERA
                ) == android.content.pm.PackageManager.PERMISSION_GRANTED;

        if (tienePermiso) {
            abrirCamara();
        } else {
            permisoLauncher.launch(android.Manifest.permission.CAMERA);
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
            String ts = new java.text.SimpleDateFormat(
                    "yyyyMMdd_HHmmss",
                    java.util.Locale.getDefault()
            ).format(new java.util.Date());

            java.io.File foto = java.io.File.createTempFile(
                    "EQUINO_" + ts, ".jpg",
                    getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
            );

            uriCamaraTemp = androidx.core.content.FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    foto
            );

            tomarFotoLauncher.launch(uriCamaraTemp);

        } catch (java.io.IOException e) {
            Toast.makeText(this,
                    "Error al crear archivo de foto",
                    Toast.LENGTH_SHORT).show();
        }
    }
}