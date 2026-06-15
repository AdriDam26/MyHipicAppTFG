package com.example.myhipicapptfg.ui.competiciones.participantes.pruebas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.participantes.AlumnoResultadosViewModel;
import com.example.myhipicapptfg.ui.competiciones.participantes.ranking.RankingPruebaActivity;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity que muestra las pruebas publicadas en las que participa un alumno.
 *
 * Permite consultar el listado de pruebas y acceder al ranking
 * de cada una de ellas.
 */
public class MisPruebasActivity extends AppCompatActivity {

    public static final String EXTRA_ID_ALUMNO = "extra_id_alumno";

    private AlumnoResultadosViewModel viewModel;
    private PruebaAlumnoAdapter adapter;

    private RecyclerView recyclerView;
    private TextView tvEmpty;

    private int idAlumno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pruebas);

        obtenerExtras();
        initViews();
        initToolbar();
        initViewModel();
        observarPruebas();
    }

    /**
     * Obtiene el identificador del alumno recibido mediante el Intent.
     */
    private void obtenerExtras() {
        idAlumno = getIntent().getIntExtra(EXTRA_ID_ALUMNO, -1);
    }

    /**
     * Inicializa las vistas de la pantalla.
     */
    private void initViews() {

        recyclerView = findViewById(R.id.rvMisPruebas);
        tvEmpty = findViewById(R.id.tvEmpty);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PruebaAlumnoAdapter();

        adapter.setOnClick(item -> {
            Intent intent = new Intent(
                    MisPruebasActivity.this,
                    RankingPruebaActivity.class
            );

            intent.putExtra(
                    RankingPruebaActivity.EXTRA_ID_PRUEBA,
                    item.idPrueba
            );

            intent.putExtra(
                    RankingPruebaActivity.EXTRA_ID_PARTICIPACION,
                    item.idParticipacion
            );

            intent.putExtra(
                    RankingPruebaActivity.EXTRA_NOMBRE_PRUEBA,
                    item.nombrePrueba
            );

            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(AlumnoResultadosViewModel.class);
    }

    /**
     * Observa las pruebas publicadas del alumno y actualiza la interfaz.
     */
    private void observarPruebas() {

        viewModel.getPruebas(idAlumno).observe(this, lista -> {

            if (lista == null || lista.isEmpty()) {

                tvEmpty.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);

            } else {

                tvEmpty.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);

                adapter.submitList(lista);
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}