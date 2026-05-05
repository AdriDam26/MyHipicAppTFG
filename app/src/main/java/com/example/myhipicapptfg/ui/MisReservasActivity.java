package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.adapter.ReservaClaseAdapter;
import com.example.myhipicapptfg.viewmodel.ReservaClaseViewModel;
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
        // Observar las clases que el alumno ya tiene reservadas
        viewModel.getClasesReservadas().observe(this, clases -> {
            if (clases != null) {
                adapter.submitList(clases);

                // Control de visibilidad si no hay reservas
                boolean estaVacio = clases.isEmpty();
                layoutEmpty.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
                recyclerMisReservas.setVisibility(estaVacio ? View.GONE : View.VISIBLE);
            }
        });

        viewModel.getProfesores().observe(this, usuarios -> {
            viewModel.getPistas().observe(this, pistas -> {
                viewModel.getReservas().observe(this, reservas -> {

                    // Pasamos las listas al adapter para que funcionen sus métodos de búsqueda
                    if (usuarios != null && pistas != null && reservas != null) {
                        adapter.setDatosReferencia(usuarios, pistas, reservas);
                    }

                });
            });
        });

        // Observar el estado de las operaciones (en este caso, cancelación)
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;

            if ("CANCELADA".equals(estado)) {
                Snackbar.make(recyclerMisReservas, "Reserva cancelada correctamente", Snackbar.LENGTH_SHORT).show();
            } else if (estado.startsWith("ERROR")) {
                Snackbar.make(recyclerMisReservas, "Error al procesar la cancelación", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Cuando el usuario vuelve a esta pantalla (p.ej. tras reservar una nueva), forzamos recarga
        viewModel.forzarActualizacion();
    }
}