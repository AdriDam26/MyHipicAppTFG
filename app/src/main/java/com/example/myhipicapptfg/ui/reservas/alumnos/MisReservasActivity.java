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


/**
 * Activity encargada de mostrar y gestionar las reservas del alumno.
 *
 * Permite:
 * - Listar las clases en las que el alumno está inscrito
 * - Cancelar reservas existentes
 * - Mostrar estado vacío si no hay reservas
 * - Mostrar feedback de operaciones (éxito o error)
 *
 */
public class MisReservasActivity extends AppCompatActivity {

    /**
     * ViewModel que gestiona la lógica de reservas del alumno.
     */
    private MisReservasViewModel viewModel;

    /**
     * Adapter del RecyclerView para mostrar las clases reservadas.
     */
    private ReservaClaseAdapter adapter;

    /**
     * ID del alumno recibido desde el Intent.
     */
    private int idAlumno;

    private RecyclerView recyclerMisReservas;
    private View layoutEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reservas);

        idAlumno = getIntent().getIntExtra("ID_ALUMNO", -1);


        initViews();
        initViewModel();
        setupRecyclerView();
        observarDatos();
    }

    /**
     * Inicializa las referencias a vistas de la Activity.
     */
    private void initViews() {
        recyclerMisReservas = findViewById(R.id.recyclerMisReservas);
        layoutEmpty = findViewById(R.id.layoutEmptyReservas);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Botón de navegación atrás
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(MisReservasViewModel.class);
    }

    /**
     * Configura el RecyclerView con su adapter en modo cancelación.
     */
    private void setupRecyclerView() {
        adapter = new ReservaClaseAdapter(true, clase ->
                viewModel.cancelarReserva(idAlumno, clase.idClase)
        );
        recyclerMisReservas.setLayoutManager(new LinearLayoutManager(this));
        recyclerMisReservas.setAdapter(adapter);
    }

    /**
     * Observa los cambios en los datos del ViewModel y actualiza la UI.
     */
    private void observarDatos() {
        // Lista de reservas del alumno
        viewModel.getClasesReservadas(idAlumno).observe(this, clases -> {
            if (clases != null) {
                adapter.submitList(clases);
                boolean estaVacio = clases.isEmpty();
                layoutEmpty.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
                recyclerMisReservas.setVisibility(estaVacio ? View.GONE : View.VISIBLE);
            }
        });

        // Estado de la operación de cancelación
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) {
                return;
            }
            if ("CANCELADA".equals(estado)) {
                Snackbar.make(recyclerMisReservas, "Reserva cancelada correctamente",
                        Snackbar.LENGTH_SHORT).show();
            } else if (estado.startsWith("ERROR")) {
                Snackbar.make(recyclerMisReservas, "Error al procesar la cancelación",
                        Snackbar.LENGTH_LONG).show();
            }
            // Reinicia el estado después de mostrar el mensaje
            viewModel.resetearEstado();
        });
    }
}