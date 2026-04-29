package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Participacion;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.GestionParticipacionesViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class ParticipacionFormActivity extends AppCompatActivity {

    private GestionParticipacionesViewModel viewModel;

    private TextInputLayout      layAlumno, layEquino;
    private AutoCompleteTextView spinnerAlumno, spinnerEquino;
    private TextInputEditText    etOrden;

    private List<Usuario> listaAlumnos = new ArrayList<>();
    private List<Equino>  listaEquinos = new ArrayList<>();

    private int     idPrueba        = -1;
    private int     idParticipacion = -1;
    private boolean modoEdicion     = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participacion_form);

        idPrueba = getIntent().getIntExtra("ID_PRUEBA", -1);

        if (getIntent().hasExtra("ID_PARTICIPACION")) {
            idParticipacion = getIntent().getIntExtra("ID_PARTICIPACION", -1);
            modoEdicion     = true;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(modoEdicion ? "Editar inscripción" : "Inscribir binomio");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(GestionParticipacionesViewModel.class);

        initViews();
        observarDatos();
        observarEstado();

        if (!modoEdicion) {
            viewModel.obtenerSiguienteOrden(idPrueba).observe(this, orden -> {
                if (orden != null) etOrden.setText(String.valueOf(orden));
            });
        }

        findViewById(R.id.btnGuardarParticipacion).setOnClickListener(v -> guardar());
        findViewById(R.id.btnVolverParticipacion).setOnClickListener(v -> finish());
    }

    // ─────────────────────────────────────────────────
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

    // ─────────────────────────────────────────────────
    private void observarDatos() {

        viewModel.obtenerAlumnosDomaConNombre().observe(this, alumnos -> {
            listaAlumnos = alumnos != null ? alumnos : new ArrayList<>();
            spinnerAlumno.setAdapter(new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, listaAlumnos));
            intentarPrecargar();
        });

        viewModel.obtenerEquinosDoma().observe(this, equinos -> {
            listaEquinos = equinos != null ? equinos : new ArrayList<>();
            spinnerEquino.setAdapter(new ArrayAdapter<>(
                    this, android.R.layout.simple_dropdown_item_1line, listaEquinos));
            intentarPrecargar();
        });
    }

    // ─────────────────────────────────────────────────
    // Precarga los datos solo cuando ambas listas han llegado (modo edición)
    private void intentarPrecargar() {
        if (!modoEdicion || listaAlumnos.isEmpty() || listaEquinos.isEmpty()) return;

        viewModel.buscarPorId(idParticipacion).observe(this, p -> {
            if (p == null) return;

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

    // ─────────────────────────────────────────────────
    private boolean validar() {
        boolean ok = true;

        if (spinnerAlumno.getText().toString().trim().isEmpty()) {
            layAlumno.setError("Selecciona un alumno");
            ok = false;
        } else layAlumno.setError(null);

        if (spinnerEquino.getText().toString().trim().isEmpty()) {
            layEquino.setError("Selecciona un equino");
            ok = false;
        } else layEquino.setError(null);

        return ok;
    }

    // ─────────────────────────────────────────────────
    private void guardar() {
        if (!validar()) return;

        int idAlu  = -1;
        int idEqui = -1;

        String selAlu  = spinnerAlumno.getText().toString();
        String selEqui = spinnerEquino.getText().toString();

        for (Usuario u : listaAlumnos)
            if (u.toString().equals(selAlu)) { idAlu = u.idUsuario; break; }

        for (Equino e : listaEquinos)
            if (e.toString().equals(selEqui)) { idEqui = e.idEquino; break; }

        int orden = Integer.parseInt(etOrden.getText().toString());

        Participacion p = new Participacion(orden, idAlu, idEqui, idPrueba);

        if (modoEdicion) {
            p.idParticipacion = idParticipacion;
            viewModel.actualizar(p);
        } else {
            viewModel.insertar(p);
        }
    }

    // ─────────────────────────────────────────────────
    private void observarEstado() {
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;
            switch (estado) {
                case "EXITO":
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case "ERROR_YA_INSCRITO":
                    layAlumno.setError("Este binomio ya está inscrito en esta prueba");
                    break;
                case "ERROR_BD":
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}