package com.example.myhipicapptfg.ui.competiciones.juez.pruebas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.juez.participantes.ParticipantesPruebaActivity;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity que muestra las pruebas asignadas a un juez.
 */
public class PruebasJuezActivity extends AppCompatActivity {

    public static final String EXTRA_ID_JUEZ = "extra_id_juez";

    private RecyclerView rv;
    private TextView tvEmpty;
    private PruebaJuezAdapter adapter;
    private PruebasJuezViewModel viewModel;

    private int idJuez;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas_juez);

        obtenerExtras();
        initViews();
        initToolbar();
        initViewModel();
        observarPruebas();
    }

    /**
     * Obtiene el identificador del juez recibido mediante el Intent.
     */
    private void obtenerExtras() {
        idJuez = getIntent().getIntExtra(EXTRA_ID_JUEZ, -1);
    }

    /**
     * Inicializa las vistas de la pantalla.
     */
    private void initViews() {

        rv = findViewById(R.id.rvPruebas);
        tvEmpty = findViewById(R.id.tvEmptyPruebas);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PruebaJuezAdapter(prueba -> {

            Intent intent = new Intent(
                    this,
                    ParticipantesPruebaActivity.class
            );

            intent.putExtra(
                    ParticipantesPruebaActivity.EXTRA_ID_PRUEBA,
                    prueba.idPrueba
            );

            intent.putExtra(
                    ParticipantesPruebaActivity.EXTRA_NOMBRE_PRUEBA,
                    prueba.nombrePrueba
            );

            startActivity(intent);
        });

        rv.setAdapter(adapter);
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarPruebas);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(PruebasJuezViewModel.class);
    }

    /**
     * Observa las pruebas asignadas al juez y actualiza la interfaz.
     */
    private void observarPruebas() {

        viewModel.getPruebas(idJuez).observe(this, lista -> {

            if (lista == null || lista.isEmpty()) {

                tvEmpty.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);

            } else {

                tvEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);

                adapter.submitList(lista);
            }
        });
    }
}