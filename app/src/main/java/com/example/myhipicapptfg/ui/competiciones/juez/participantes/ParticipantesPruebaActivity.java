package com.example.myhipicapptfg.ui.competiciones.juez.participantes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.juez.calificacion.PuntuacionActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

/**
 * Activity que muestra los participantes de una prueba
 * y permite gestionar la publicación de resultados.
 */
public class ParticipantesPruebaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PRUEBA = "extra_id_prueba";
    public static final String EXTRA_NOMBRE_PRUEBA = "extra_nombre_prueba";

    private int idPrueba;
    private String nombrePrueba;

    private TextView tvEmpty;
    private RecyclerView rv;
    private MaterialButton btnPublicar;

    private ParticipanteAdapter adapter;
    private ParticipantesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantes_prueba);

        obtenerExtras();
        initViews();
        initToolbar();
        initViewModel();
        observarParticipantes();
        observarEstadoPublicacion();
        configurarBotonPublicar();
    }

    /**
     * Obtiene los datos enviados mediante el Intent.
     */
    private void obtenerExtras() {
        idPrueba = getIntent().getIntExtra(EXTRA_ID_PRUEBA, -1);
        nombrePrueba = getIntent().getStringExtra(EXTRA_NOMBRE_PRUEBA);
    }

    /**
     * Inicializa los componentes de la interfaz.
     */
    private void initViews() {

        tvEmpty = findViewById(R.id.tvEmptyParticipantes);
        rv = findViewById(R.id.rvParticipantes);
        btnPublicar = findViewById(R.id.btnPublicarResultados);

        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParticipanteAdapter();

        adapter.setOnParticipanteClickListener(item -> {

            Intent intent = new Intent(
                    this,
                    PuntuacionActivity.class
            );

            intent.putExtra(
                    PuntuacionActivity.EXTRA_ID_PARTICIPACION,
                    item.idParticipacion
            );

            intent.putExtra(
                    PuntuacionActivity.EXTRA_ID_PRUEBA,
                    idPrueba
            );

            intent.putExtra(
                    PuntuacionActivity.EXTRA_NOMBRE_JINETE,
                    item.nombreJinete
            );

            intent.putExtra(
                    PuntuacionActivity.EXTRA_NOMBRE_CABALLO,
                    item.nombreCaballo
            );

            intent.putExtra(
                    PuntuacionActivity.EXTRA_CORRECCION_PREVIA,
                    item.correccion
            );

            startActivity(intent);
        });

        rv.setAdapter(adapter);
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarParticipantes);

        if (nombrePrueba != null) {
            toolbar.setTitle(nombrePrueba);
        }

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el ViewModel.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(ParticipantesViewModel.class);
    }

    /**
     * Observa la lista de participantes de la prueba.
     */
    private void observarParticipantes() {

        viewModel.getParticipantes(idPrueba)
                .observe(this, lista -> {

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

    /**
     * Observa el estado de publicación de la prueba.
     */
    private void observarEstadoPublicacion() {

        viewModel.isPublicado().observe(this, publicado -> {

            if (publicado != null && publicado) {

                btnPublicar.setText("🔒 Ocultar resultados");

                btnPublicar.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.parseColor("#B71C1C")
                        )
                );

            } else {

                btnPublicar.setText("📢 Publicar resultados");

                btnPublicar.setBackgroundTintList(
                        ColorStateList.valueOf(
                                Color.parseColor("#8B1A2E")
                        )
                );
            }
        });
    }

    /**
     * Configura el botón para publicar u ocultar resultados.
     */
    private void configurarBotonPublicar() {

        btnPublicar.setOnClickListener(v -> {

            Boolean actual = viewModel.isPublicado().getValue();

            boolean nuevoEstado =
                    (actual == null) ? true : !actual;

            String mensaje = nuevoEstado
                    ? "¿Publicar los resultados? Los alumnos podrán verlos."
                    : "¿Ocultar los resultados? Los alumnos dejarán de verlos.";

            new AlertDialog.Builder(this)
                    .setTitle("Resultados")
                    .setMessage(mensaje)
                    .setPositiveButton(
                            "Confirmar",
                            (d, w) -> viewModel.actualizarPublicado(nuevoEstado)
                    )
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }
}