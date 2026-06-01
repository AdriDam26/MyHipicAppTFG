package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.snackbar.Snackbar;

public class MisReservasActivity extends AppCompatActivity {

    private ReservaClaseViewModel viewModel;
    private ReservaClaseAdapter adapter;
    private int idAlumno;

    // Componentes de la UI
    private RecyclerView recyclerMisReservas;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        // 1. Obtener ID del alumno desde el Intent
        idAlumno = getIntent().getIntExtra("ID_ALUMNO", -1);
        if (idAlumno == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Vincula el Toolbar usando su ID (@id/toolbar)
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Configura la acción para ir hacia atrás
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        initViews();
        initViewModel();
        setupRecyclerView();
        observarDatos();

        // 2. Cargar los datos del alumno (esto dispara las consultas en el ViewModel)
        viewModel.cargarDatosAlumno(idAlumno);
    }

    private void initViews() {
        recyclerMisReservas = findViewById(R.id.recyclerMisReservas);
        layoutEmpty = findViewById(R.id.layoutEmptyReservas);

        // Configurar Toolbar con botón de atrás
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ReservaClaseViewModel.class);
    }

    private void setupRecyclerView() {
        // MUY IMPORTANTE: Pasamos 'true' para activar el modo cancelación en el Adapter
        adapter = new ReservaClaseAdapter(true, clase -> {
            // Acción al pulsar "Cancelar"
            viewModel.cancelarReserva(idAlumno, clase.idClase);
        });

        recyclerMisReservas.setLayoutManager(new LinearLayoutManager(this));
        recyclerMisReservas.setAdapter(adapter);
    }

    private void observarDatos() {
        // ✅ Un solo observer en lugar de los 3 anidados
        viewModel.getClasesReservadasUI().observe(this, clases -> {
            if (clases != null) {
                adapter.submitList(clases);
                boolean estaVacio = clases.isEmpty();
                layoutEmpty.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
                recyclerMisReservas.setVisibility(estaVacio ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;
            if ("CANCELADA".equals(estado)) {
                Snackbar.make(recyclerMisReservas, "Reserva cancelada correctamente",
                        Snackbar.LENGTH_SHORT).show();
            } else if (estado.startsWith("ERROR")) {
                Snackbar.make(recyclerMisReservas, "Error al procesar la cancelación",
                        Snackbar.LENGTH_LONG).show();
            }
            viewModel.resetearEstado(); // ✅ Añadido para evitar que el Snackbar se repita al rotar
        });
    }


}