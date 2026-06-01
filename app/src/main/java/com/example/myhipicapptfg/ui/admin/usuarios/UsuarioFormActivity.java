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

public class UsuarioFormActivity extends AppCompatActivity {

    private GestionUsuariosViewModel viewModel;

    // ── Campos básicos ──────────────────────────────────────────────
    private TextInputEditText etNombre, etApellido1, etApellido2,
            etDni, etEmail, etTelefono, etFechaNacimiento;

    private AutoCompleteTextView spinnerSexo, spinnerTipo, spinnerPrefijo;

    private TextInputLayout layNombre, layApellido1, layApellido2,
            layDni, layEmail, layTelefono, layFechaNacimiento;

    // ── Alumno ──────────────────────────────────────────────────────
    private View layoutAlumno;
    private CheckBox cbDoma, cbSalto;
    private TextInputLayout layNivelDoma, layNivelSalto;
    private AutoCompleteTextView spinnerNivelDoma, spinnerNivelSalto;

    // ── Profesor ────────────────────────────────────────────────────
    private View layoutProfesor;
    private CheckBox cbProfesorDoma, cbProfesorSalto, cbProfesorActivo;
    private TextInputLayout layNivelMaximoDoma, layNivelMaximoSalto, layAniosExperiencia;
    private AutoCompleteTextView spinnerNivelMaximoDoma, spinnerNivelMaximoSalto;
    private TextInputEditText etAniosExperiencia;

    // ── Juez ────────────────────────────────────────────────────────
    private View layoutJuez;
    private TextInputLayout layNumeroLicencia;
    private AutoCompleteTextView spinnerFederacion;
    private CheckBox cbJuezActivo;
    private TextInputEditText etNumeroLicencia;

    // ── Foto ────────────────────────────────────────────────────────
    private ShapeableImageView imgFotoPerfil;
    private Uri fotoUri = null;
    private Uri uriCamaraTemp = null;

    private ActivityResultLauncher<Uri> tomarFotoLauncher;
    private ActivityResultLauncher<String> seleccionarFotoLauncher;
    private ActivityResultLauncher<String> permisoLauncher;

    // ── Estado ──────────────────────────────────────────────────────
    private long fechaNacimientoSeleccionada = -1;
    private boolean modoEdicion = false;
    private int usuarioId = -1;

    // ================================================================
    // LIFECYCLE
    // ================================================================
    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_usuario_form);

        viewModel = new ViewModelProvider(this).get(GestionUsuariosViewModel.class);

        // ✅ Los launchers SIEMPRE primero (requisito de AndroidX)
        registrarLaunchers();

        // ✅ initViews ANTES de cargarUsuario para que las vistas existan
        initViews();
        setupSpinners();
        setupFechaNacimiento();
        setupTipoUsuario();
        setupCheckBoxListeners();
        observarEstadoRepository();

        // ✅ Ahora sí es seguro cargar datos (vistas ya inicializadas)
        if (getIntent().hasExtra("ID_USUARIO")) {
            usuarioId = getIntent().getIntExtra("ID_USUARIO", -1);
            modoEdicion = true;
            cargarUsuario(usuarioId);
        }

        // Vincula la MaterialToolbar mediante su ID
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Configura la acción para regresar a la pantalla anterior
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        findViewById(R.id.btnGuardar).setOnClickListener(v -> guardarUsuario());
    }

    // ================================================================
    // LAUNCHERS (foto)
    // ================================================================
    private void registrarLaunchers() {

        seleccionarFotoLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        // Persistir permiso para poder leer el URI después de reiniciar
                        getContentResolver().takePersistableUriPermission(
                                uri,
                                android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                        );
                        fotoUri = uri;
                        imgFotoPerfil.setImageURI(uri);
                    }
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
                                "Permiso de cámara denegado", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void abrirCamara() {
        try {
            String ts = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                    .format(new Date());
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
            Toast.makeText(this, "Error al crear archivo de foto", Toast.LENGTH_SHORT).show();
        }
    }

    // ================================================================
    // CARGAR USUARIO (modo edición)
    // ================================================================
    private void cargarUsuario(int id) {

        viewModel.buscarPorId(id).observe(this, usuario -> {

            if (usuario == null) return;

            etNombre.setText(usuario.nombre);
            etApellido1.setText(usuario.apellido1);
            etApellido2.setText(usuario.apellido2);
            etDni.setText(usuario.dni);
            etEmail.setText(usuario.email);
            // Separar prefijo y número al cargar
            String telefonoCompleto = usuario.telefono != null ? usuario.telefono.trim() : "";
            String prefijoEncontrado = "+34"; // solo el default
            String numeroSinPrefijo = telefonoCompleto;

            ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerPrefijo.getAdapter();
            if (adapter != null) {
                for (int i = 0; i < adapter.getCount(); i++) {
                    String prefijo = adapter.getItem(i);
                    if (prefijo != null && telefonoCompleto.startsWith(prefijo)) {
                        prefijoEncontrado = prefijo;
                        numeroSinPrefijo = telefonoCompleto.substring(prefijo.length()).trim();
                        break;
                    }
                }
            }

            spinnerPrefijo.setText(prefijoEncontrado, false);
            etTelefono.setText(numeroSinPrefijo);

            spinnerSexo.setText(usuario.sexo, false);
            spinnerTipo.setText(usuario.tipo, false);

            fechaNacimientoSeleccionada = usuario.fechaNacimiento;
            Calendar c = Calendar.getInstance();
            c.setTimeInMillis(usuario.fechaNacimiento);
            etFechaNacimiento.setText(
                    c.get(Calendar.DAY_OF_MONTH) + "/" +
                            (c.get(Calendar.MONTH) + 1) + "/" +
                            c.get(Calendar.YEAR)
            );

            // ✅ Foto: seguro porque initViews() ya se ejecutó
            if (usuario.fotoPerfil != null && !usuario.fotoPerfil.isEmpty()) {
                fotoUri = Uri.parse(usuario.fotoPerfil);
                imgFotoPerfil.setImageURI(fotoUri);
            }

            actualizarVisibilidadPorTipo();
            cargarDatosEspecificos(usuario.idUsuario, usuario.tipo);
        });
    }

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

    // ================================================================
    // INIT VIEWS
    // ================================================================
    private void initViews() {

        // Básicos
        etNombre            = findViewById(R.id.etNombre);
        etApellido1         = findViewById(R.id.etApellido1);
        etApellido2         = findViewById(R.id.etApellido2);
        etDni               = findViewById(R.id.etDni);
        etEmail             = findViewById(R.id.etEmail);
        etTelefono          = findViewById(R.id.etTelefono);
        etFechaNacimiento   = findViewById(R.id.etFechaNacimiento);

        spinnerSexo         = findViewById(R.id.spinnerSexo);
        spinnerTipo         = findViewById(R.id.spinnerTipo);
        spinnerPrefijo      = findViewById(R.id.spinnerPrefijo);

        layNombre           = findViewById(R.id.layNombre);
        layApellido1        = findViewById(R.id.layApellido1);
        layApellido2        = findViewById(R.id.layApellido2);
        layDni              = findViewById(R.id.layDni);
        layEmail            = findViewById(R.id.layEmail);
        layTelefono         = findViewById(R.id.layTelefono);
        layFechaNacimiento  = findViewById(R.id.layFechaNacimiento);

        // Alumno
        layoutAlumno        = findViewById(R.id.layoutAlumno);
        cbDoma              = findViewById(R.id.cbDoma);
        cbSalto             = findViewById(R.id.cbSalto);
        layNivelDoma        = findViewById(R.id.layNivelDoma);
        layNivelSalto       = findViewById(R.id.layNivelSalto);
        spinnerNivelDoma    = findViewById(R.id.spinnerNivelDoma);
        spinnerNivelSalto   = findViewById(R.id.spinnerNivelSalto);

        // Profesor
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

        // Juez
        layoutJuez          = findViewById(R.id.layoutJuez);
        layNumeroLicencia   = findViewById(R.id.layNumeroLicencia);
        spinnerFederacion   = findViewById(R.id.spinnerFederacion);
        cbJuezActivo        = findViewById(R.id.cbJuezActivo);
        etNumeroLicencia    = findViewById(R.id.etNumeroLicencia);

        // Foto
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);

        findViewById(R.id.btnSeleccionarFoto).setOnClickListener(v ->
                seleccionarFotoLauncher.launch("image/*")
        );

        findViewById(R.id.btnTomarFoto).setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {
                abrirCamara();
            } else {
                permisoLauncher.launch(Manifest.permission.CAMERA);
            }
        });
    }

    // ================================================================
    // SPINNERS
    // ================================================================
    private void setupSpinners() {

        spinnerSexo.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{Usuario.SEXO_FEMENINO, Usuario.SEXO_MASCULINO}));

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
                new String[]{"+34", "+33", "+44", "+49", "+39"}));

        String[] niveles = {Alumno.PRINCIPIANTE, Alumno.INTERMEDIO, Alumno.AVANZADO};
        ArrayAdapter<String> adapterNiveles = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, niveles);
        spinnerNivelDoma.setAdapter(adapterNiveles);
        spinnerNivelSalto.setAdapter(adapterNiveles);

        String[] nivelesProf = {Profesor.PRINCIPIANTE, Profesor.INTERMEDIO, Profesor.AVANZADO};
        ArrayAdapter<String> adapterProf = new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, nivelesProf);
        spinnerNivelMaximoDoma.setAdapter(adapterProf);
        spinnerNivelMaximoSalto.setAdapter(adapterProf);

        String[] feds = {Juez.ESPAÑOLA, Juez.BRITANICA, Juez.FRANCESA, Juez.INTERNACIONAL};
        spinnerFederacion.setAdapter(new ArrayAdapter<>(
                this, android.R.layout.simple_dropdown_item_1line, feds));
    }

    // ================================================================
    // TIPO USUARIO
    // ================================================================
    private void setupTipoUsuario() {
        spinnerTipo.setOnItemClickListener((parent, view, position, id) ->
                actualizarVisibilidadPorTipo());
        actualizarVisibilidadPorTipo();
    }

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

    private void resetVisibilidadSubniveles() {
        layNivelDoma.setVisibility(View.GONE);
        layNivelSalto.setVisibility(View.GONE);
        layNivelMaximoDoma.setVisibility(View.GONE);
        layNivelMaximoSalto.setVisibility(View.GONE);
        etNumeroLicencia.setText("");
        spinnerFederacion.setText("");
        cbJuezActivo.setChecked(false);
    }

    // ================================================================
    // CHECKBOXES
    // ================================================================
    private void setupCheckBoxListeners() {

        cbDoma.setOnCheckedChangeListener((b, c) ->
                layNivelDoma.setVisibility(c ? View.VISIBLE : View.GONE));

        cbSalto.setOnCheckedChangeListener((b, c) ->
                layNivelSalto.setVisibility(c ? View.VISIBLE : View.GONE));

        cbProfesorDoma.setOnCheckedChangeListener((b, c) ->
                layNivelMaximoDoma.setVisibility(c ? View.VISIBLE : View.GONE));

        cbProfesorSalto.setOnCheckedChangeListener((b, c) ->
                layNivelMaximoSalto.setVisibility(c ? View.VISIBLE : View.GONE));
    }

    // ================================================================
    // FECHA
    // ================================================================
    private void setupFechaNacimiento() {

        etFechaNacimiento.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this,
                    (view, year, month, day) -> {
                        Calendar fecha = Calendar.getInstance();
                        fecha.set(year, month, day);
                        fechaNacimientoSeleccionada = fecha.getTimeInMillis();
                        etFechaNacimiento.setText(day + "/" + (month + 1) + "/" + year);
                        layFechaNacimiento.setError(null);
                    },
                    c.get(Calendar.YEAR),
                    c.get(Calendar.MONTH),
                    c.get(Calendar.DAY_OF_MONTH)
            ).show();
        });
    }

    // ================================================================
    // VALIDACIÓN
    // ================================================================
    private boolean validar() {

        boolean ok = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            layNombre.setError("Obligatorio"); ok = false;
        } else layNombre.setError(null);

        if (etApellido1.getText().toString().trim().isEmpty()) {
            layApellido1.setError("Obligatorio"); ok = false;
        } else layApellido1.setError(null);

        if (etApellido2.getText().toString().trim().isEmpty()) {
            layApellido2.setError("Obligatorio"); ok = false;
        } else layApellido2.setError(null);

        if (etTelefono.getText().toString().trim().isEmpty()) {
            layTelefono.setError("Obligatorio"); ok = false;
        } else layTelefono.setError(null);

        if (!etDni.getText().toString().trim().matches("\\d{8}[A-Z]")) {
            layDni.setError("DNI inválido"); ok = false;
        } else layDni.setError(null);

        if (!android.util.Patterns.EMAIL_ADDRESS
                .matcher(etEmail.getText().toString().trim()).matches()) {
            layEmail.setError("Email inválido"); ok = false;
        } else layEmail.setError(null);

        if (fechaNacimientoSeleccionada <= 0) {
            layFechaNacimiento.setError("Selecciona fecha"); ok = false;
        } else layFechaNacimiento.setError(null);

        String tipo = spinnerTipo.getText().toString().trim();

        if (Usuario.TIPO_ALUMNO.equals(tipo)) {

            if (!cbDoma.isChecked() && !cbSalto.isChecked()) {
                Toast.makeText(this,
                        "Selecciona al menos una disciplina", Toast.LENGTH_SHORT).show();
                ok = false;
            }
            if (cbDoma.isChecked() && spinnerNivelDoma.getText().toString().trim().isEmpty()) {
                layNivelDoma.setError("Selecciona nivel"); ok = false;
            } else layNivelDoma.setError(null);

            if (cbSalto.isChecked() && spinnerNivelSalto.getText().toString().trim().isEmpty()) {
                layNivelSalto.setError("Selecciona nivel"); ok = false;
            } else layNivelSalto.setError(null);
        }

        if (Usuario.TIPO_PROFESOR.equals(tipo)) {

            if (!cbProfesorDoma.isChecked() && !cbProfesorSalto.isChecked()) {
                Toast.makeText(this,
                        "Selecciona al menos una disciplina", Toast.LENGTH_SHORT).show();
                ok = false;
            }
            if (cbProfesorDoma.isChecked() &&
                    spinnerNivelMaximoDoma.getText().toString().trim().isEmpty()) {
                layNivelMaximoDoma.setError("Selecciona nivel máximo"); ok = false;
            } else layNivelMaximoDoma.setError(null);

            if (cbProfesorSalto.isChecked() &&
                    spinnerNivelMaximoSalto.getText().toString().trim().isEmpty()) {
                layNivelMaximoSalto.setError("Selecciona nivel máximo"); ok = false;
            } else layNivelMaximoSalto.setError(null);

            if (etAniosExperiencia.getText().toString().trim().isEmpty()) {
                layAniosExperiencia.setError("Introduce años de experiencia"); ok = false;
            } else layAniosExperiencia.setError(null);
        }

        if (Usuario.TIPO_JUEZ.equals(tipo)) {

            String lic = etNumeroLicencia.getText().toString().trim();
            if (lic.isEmpty()) {
                layNumeroLicencia.setError("Introduce número de licencia"); ok = false;
            } else if (!lic.matches("^(RFHE|FEI|FFE|BEF)-?\\d{4,10}$")) {
                layNumeroLicencia.setError("Formato inválido (ej: FEI-12345)"); ok = false;
            } else layNumeroLicencia.setError(null);

            if (spinnerFederacion.getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "Selecciona federación", Toast.LENGTH_SHORT).show();
                ok = false;
            }
        }

        return ok;
    }

    // ================================================================
    // GUARDAR
    // ================================================================
    private void guardarUsuario() {

        if (!validar()) return;

        String tipo = spinnerTipo.getText().toString().trim();

        Usuario u = new Usuario(
                etEmail.getText().toString().trim(),
                spinnerPrefijo.getText().toString().trim() + " "
                        + etTelefono.getText().toString().trim(),
                etApellido1.getText().toString().trim(),
                etApellido2.getText().toString().trim(),
                etDni.getText().toString().trim(),
                etNombre.getText().toString().trim(),
                fechaNacimientoSeleccionada,
                System.currentTimeMillis(),
                spinnerSexo.getText().toString().trim(),
                tipo,
                fotoUri != null ? fotoUri.toString() : null  // ✅
        );


        if (modoEdicion) {
            u.idUsuario = usuarioId;
        }

        Alumno alumno = null;
        Profesor profesor = null;
        Juez juez = null;

        if (Usuario.TIPO_ALUMNO.equals(tipo)) {

            alumno = new Alumno();
            alumno.idAlumno = usuarioId;
            alumno.practicaDoma = cbDoma.isChecked();
            alumno.practicaSalto = cbSalto.isChecked();
            alumno.nivelDoma  = cbDoma.isChecked()
                    ? spinnerNivelDoma.getText().toString().trim() : null;
            alumno.nivelSalto = cbSalto.isChecked()
                    ? spinnerNivelSalto.getText().toString().trim() : null;

        } else if (Usuario.TIPO_PROFESOR.equals(tipo)) {

            profesor = new Profesor();
            profesor.idProfesor = usuarioId;
            profesor.puedeDarDoma  = cbProfesorDoma.isChecked();
            profesor.puedeDarSalto = cbProfesorSalto.isChecked();
            profesor.activo        = cbProfesorActivo.isChecked();
            profesor.nivelMaximoDoma  = cbProfesorDoma.isChecked()
                    ? spinnerNivelMaximoDoma.getText().toString().trim() : null;
            profesor.nivelMaximoSalto = cbProfesorSalto.isChecked()
                    ? spinnerNivelMaximoSalto.getText().toString().trim() : null;
            profesor.aniosExperiencia =
                    Integer.parseInt(etAniosExperiencia.getText().toString().trim());

        } else if (Usuario.TIPO_JUEZ.equals(tipo)) {

            juez = new Juez();
            juez.idJuez = usuarioId;
            juez.numeroLicencia = etNumeroLicencia.getText().toString().trim();
            juez.federacion     = spinnerFederacion.getText().toString().trim();
            juez.activo         = cbJuezActivo.isChecked();
        }

        if (modoEdicion) {
            viewModel.actualizarUsuarioCompleto(u, alumno, profesor, juez);
        } else {
            viewModel.guardarUsuarioCompleto(u, alumno, profesor, juez);
        }
    }

    // ================================================================
    // ESTADO REPOSITORIO
    // ================================================================
    private void observarEstadoRepository() {

        viewModel.getEstado().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {
                case "ERROR_EMAIL_DUPLICADO":
                    layEmail.setError("Email duplicado");
                    break;
                case "ERROR_DNI_DUPLICADO":
                    layDni.setError("DNI duplicado");
                    break;
                case "ERROR_BD":
                    Toast.makeText(this, "Error BD", Toast.LENGTH_SHORT).show();
                    break;
                case "EXITO":
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        });
    }
}