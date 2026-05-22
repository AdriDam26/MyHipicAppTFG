package com.example.myhipicapptfg.ui.admin.pistas;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PistaFormActivity extends AppCompatActivity {

    private GestionPistaViewModel viewModel;

    private TextInputLayout layoutNombre, layoutAncho, layoutLargo;
    private TextInputEditText etNombre, etAncho, etLargo;

    private View progressBar;

    private int idPistaEditar = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pista_form);

        viewModel = new ViewModelProvider(this)
                .get(GestionPistaViewModel.class);

        initViews();
        observarEstado();

        if (getIntent().hasExtra("ID_PISTA")) {
            idPistaEditar = getIntent().getIntExtra("ID_PISTA", -1);
            cargarDatos(idPistaEditar);
        }

        findViewById(R.id.btnGuardarPista)
                .setOnClickListener(v -> guardar());


    }

    private void initViews() {
        // ANTES: R.id.layoutEtNombrePista -> AHORA: R.id.layoutNombrePista
        layoutNombre = findViewById(R.id.layoutNombrePista);

        // ANTES: R.id.layoutEtAnchoPista -> AHORA: R.id.layoutAnchoPista
        layoutAncho = findViewById(R.id.layoutAnchoPista);

        // ANTES: R.id.layoutEtLargoPista -> AHORA: R.id.layoutLargoPista
        layoutLargo = findViewById(R.id.layoutLargoPista);

        etNombre = findViewById(R.id.etNombrePista);
        etAncho = findViewById(R.id.etAnchoPista);
        etLargo = findViewById(R.id.etLargoPista);

        progressBar = findViewById(R.id.progressBarPista);
    }

    // =========================
    // CARGAR DATOS (EDITAR)
    // =========================
    private void cargarDatos(int id) {

        viewModel.buscarPorId(id).observe(this, pista -> {
            if (pista == null) return;

            etNombre.setText(pista.nombre);
            etAncho.setText(String.valueOf(pista.ancho));
            etLargo.setText(String.valueOf(pista.largo));
        });
    }

    // =========================
    // VALIDAR
    // =========================
    private boolean validar() {

        boolean ok = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            layoutNombre.setError("Obligatorio");
            ok = false;
        } else layoutNombre.setError(null);

        if (etAncho.getText().toString().trim().isEmpty()) {
            layoutAncho.setError("Obligatorio");
            ok = false;
        } else layoutAncho.setError(null);

        if (etLargo.getText().toString().trim().isEmpty()) {
            layoutLargo.setError("Obligatorio");
            ok = false;
        } else layoutLargo.setError(null);

        return ok;
    }

    // =========================
    // GUARDAR
    // =========================
    private void guardar() {

        if (!validar()) return;

        progressBar.setVisibility(View.VISIBLE);

        String nombre = etNombre.getText().toString().trim();
        String anchoStr = etAncho.getText().toString().trim();
        String largoStr = etLargo.getText().toString().trim();

        double ancho;
        double largo;

        try {
            ancho = Double.parseDouble(anchoStr);
            largo = Double.parseDouble(largoStr);
        } catch (NumberFormatException e) {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(this, "Introduce números válidos", Toast.LENGTH_SHORT).show();
            return;
        }

        Pista pista = new Pista(nombre, ancho, largo);

        if (idPistaEditar > 0) {
            pista.idPista = idPistaEditar;
            viewModel.actualizar(pista);
        } else {
            viewModel.insertar(pista);
        }
    }

    // =========================
    // ESTADO
    // =========================
    private void observarEstado() {

        viewModel.getEstadoOperacion().observe(this, estado -> {

            progressBar.setVisibility(View.GONE);

            if (estado == null) return;

            switch (estado) {

                case "ERROR_NOMBRE_DUPLICADO":
                    layoutNombre.setError("Ya existe una pista con ese nombre");
                    break;

                case "ERROR_BD":
                    Toast.makeText(this, "Error en base de datos", Toast.LENGTH_SHORT).show();
                    break;

                case "EXITO":
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
            }
        });
    }
}