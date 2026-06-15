package com.example.myhipicapptfg.ui.competiciones.participantes.ranking;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.participantes.AlumnoResultadosViewModel;
import com.example.myhipicapptfg.ui.competiciones.participantes.calificacion.HojaCalificacionesActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * Activity que muestra el ranking de una prueba.
 *
 * Permite visualizar la clasificación de los participantes
 * y acceder a la hoja de calificaciones del alumno.
 */
public class RankingPruebaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PRUEBA = "extra_id_prueba";
    public static final String EXTRA_ID_PARTICIPACION = "extra_id_participacion";
    public static final String EXTRA_NOMBRE_PRUEBA = "extra_nombre_prueba";

    private AlumnoResultadosViewModel viewModel;
    private RankingAdapter adapter;

    private int idPrueba;
    private int idParticipacion;
    private String nombrePrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_prueba);

        obtenerExtras();
        initToolbar();
        initRecyclerView();
        initViewModel();
        observarRanking();
        initBotonHoja();
    }

    /**
     * Obtiene los datos recibidos mediante el Intent.
     */
    private void obtenerExtras() {
        idPrueba = getIntent().getIntExtra(EXTRA_ID_PRUEBA, -1);
        idParticipacion = getIntent().getIntExtra(EXTRA_ID_PARTICIPACION, -1);
        nombrePrueba = getIntent().getStringExtra(EXTRA_NOMBRE_PRUEBA);
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        toolbar.setTitle(nombrePrueba != null ? nombrePrueba : "Ranking");

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Configura el RecyclerView que muestra el ranking.
     */
    private void initRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.rvRanking);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new RankingAdapter();
        recyclerView.setAdapter(adapter);
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(AlumnoResultadosViewModel.class);
    }

    /**
     * Observa los cambios en el ranking y actualiza la lista.
     */
    private void observarRanking() {
        viewModel.getRanking(idPrueba)
                .observe(this, adapter::submitList);
    }

    /**
     * Configura el botón para acceder a la hoja de calificaciones.
     */
    private void initBotonHoja() {
        MaterialButton btnHoja = findViewById(R.id.btnVerHoja);

        btnHoja.setOnClickListener(v -> {
            Intent intent = new Intent(
                    this,
                    HojaCalificacionesActivity.class
            );

            intent.putExtra(
                    HojaCalificacionesActivity.EXTRA_ID_PARTICIPACION,
                    idParticipacion
            );

            intent.putExtra(
                    HojaCalificacionesActivity.EXTRA_NOMBRE_PRUEBA,
                    nombrePrueba
            );

            startActivity(intent);
        });
    }
}