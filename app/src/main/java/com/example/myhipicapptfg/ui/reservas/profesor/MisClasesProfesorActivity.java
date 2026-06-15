package com.example.myhipicapptfg.ui.reservas.profesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity que muestra las clases asignadas a un profesor.
 *
 * Funcionalidades principales:
 * - Mostrar listado de clases del profesor
 * - Permitir acceder al detalle de alumnos de cada clase
 * - Mostrar estado vacío cuando no hay clases asignadas
 *
 * Forma parte del módulo de gestión de clases del profesor.
 */
public class MisClasesProfesorActivity extends AppCompatActivity {

    /**
     * Clave para recibir el ID del profesor desde el Intent.
     */
    public static final String EXTRA_ID_PROFESOR = "extra_id_profesor";

    private ProfesorClasesViewModel viewModel;
    private ClaseProfesorAdapter adapter;

    private RecyclerView recyclerView;
    private View tvVacio;
    private int idProfesor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_clase_profesor);

        // OBTENER ID DEL PROFESOR
        idProfesor = getIntent().getIntExtra(EXTRA_ID_PROFESOR, -1);

        initViews();
        initToolbar();
        initViewModel();
        observarClases();
    }

    /**
     * Inicializa las vistas principales de la pantalla.
     */
    private void initViews() {

        recyclerView = findViewById(R.id.rv_mis_clases);
        tvVacio = findViewById(R.id.tv_sin_clases);

        // Configuración del RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter con acción al pulsar una clase
        adapter = new ClaseProfesorAdapter(this, clase -> {

            // Abre la pantalla de alumnos de la clase seleccionada
            Intent intent = new Intent(this, AlumnosDeClaseActivity.class);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_ID_CLASE, clase.idClase);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_DISCIPLINA, clase.disciplina);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_NIVEL, clase.nivel);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Configura el toolbar y su botón de navegación.
     */
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbarMisClases);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfesorClasesViewModel.class);
    }

    /**
     * Observa los cambios en las clases del profesor y actualiza la UI.
     */
    private void observarClases() {

        viewModel.getClasesDelProfesor(idProfesor).observe(this, clases -> {

            // Si no hay clases → mostrar estado vacío
            if (clases == null || clases.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);

            } else {
                // Si hay clases → mostrar lista
                recyclerView.setVisibility(View.VISIBLE);
                tvVacio.setVisibility(View.GONE);

                adapter.setClases(clases);
            }
        });
    }
}