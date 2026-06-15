package com.example.myhipicapptfg.ui.competiciones.participantes.calificacion;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.participantes.AlumnoResultadosViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

/**
 * Activity que muestra la hoja de calificaciones de una participación.
 *
 * Permite visualizar las notas obtenidas en cada movimiento,
 * así como el resumen final de la prueba y las correcciones aplicadas.
 */
public class HojaCalificacionesActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PARTICIPACION = "extra_id_participacion";
    public static final String EXTRA_NOMBRE_PRUEBA = "extra_nombre_prueba";

    private AlumnoResultadosViewModel viewModel;
    private HojaCalifAdapter adapter;

    private RecyclerView recyclerView;
    private TextView tvResumen;
    private TextView tvCorreccion;

    private int idParticipacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoja_calificaciones);

        obtenerExtras();
        initViews();
        initToolbar();
        initViewModel();
        observarHojaCalificaciones();
        observarParticipacion();
    }

    /**
     * Obtiene los datos recibidos mediante el Intent.
     */
    private void obtenerExtras() {
        idParticipacion = getIntent().getIntExtra(EXTRA_ID_PARTICIPACION, -1);
    }

    /**
     * Inicializa las vistas de la pantalla.
     */
    private void initViews() {

        tvResumen = findViewById(R.id.tvResumen);
        tvCorreccion = findViewById(R.id.tvCorreccion);
        recyclerView = findViewById(R.id.rvHoja);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new HojaCalifAdapter();
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
     * Observa la hoja de calificaciones y actualiza la lista de movimientos.
     */
    private void observarHojaCalificaciones() {

        viewModel.getHoja(idParticipacion).observe(this, lista -> {

            if (lista == null || lista.isEmpty()) {
                return;
            }

            adapter.submitList(lista);
        });
    }

    /**
     * Observa la participación y muestra el resumen final de resultados.
     */
    private void observarParticipacion() {

        viewModel.getParticipacion(idParticipacion).observe(this, participacion -> {

            if (participacion == null) {
                return;
            }

            if (participacion.eliminado) {

                tvResumen.setText("ELIMINADO");
                tvResumen.setBackgroundColor(
                        getColor(android.R.color.holo_red_dark)
                );

            } else {

                tvResumen.setText(
                        String.format(
                                Locale.getDefault(),
                                "Nota: %.2f  |  Porcentaje: %.3f%%",
                                participacion.notaFinal,
                                participacion.porcentaje
                        )
                );
            }

            String etiquetaCorreccion;

            if (participacion.correccion == 0.0) {
                etiquetaCorreccion = "Sin corrección";

            } else if (participacion.correccion == 2.0) {
                etiquetaCorreccion = "1 corrección";

            } else if (participacion.correccion == 4.0) {
                etiquetaCorreccion = "2 correcciones";

            } else {
                etiquetaCorreccion = "3 correcciones";
            }

            tvCorreccion.setText(
                    String.format(
                            Locale.getDefault(),
                            "Correcciones: %s",
                            etiquetaCorreccion
                    )
            );
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}