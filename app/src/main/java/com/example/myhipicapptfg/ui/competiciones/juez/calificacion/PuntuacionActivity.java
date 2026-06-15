package com.example.myhipicapptfg.ui.competiciones.juez.calificacion;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.NotaMovimiento;
import com.example.myhipicapptfg.model.MovimientoConNota;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Activity encargada de gestionar la puntuación de una participación.
 *
 * Permite al juez:
 * - Introducir notas para cada movimiento.
 * - Registrar observaciones.
 * - Aplicar correcciones o eliminación.
 * - Calcular la nota final en tiempo real.
 * - Guardar los resultados en la base de datos.
 */
public class PuntuacionActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PARTICIPACION = "id_participacion";
    public static final String EXTRA_ID_PRUEBA = "id_prueba";
    public static final String EXTRA_NOMBRE_JINETE = "nombre_jinete";
    public static final String EXTRA_NOMBRE_CABALLO = "nombre_caballo";
    public static final String EXTRA_CORRECCION_PREVIA = "correccion_previa";

    private PuntuacionViewModel vm;
    private MovimientoNotaAdapter adapter;

    private TextView tvNotaFinal;
    private TextView tvPorcentaje;
    private RadioGroup rgCorreccion;

    private int idParticipacion;
    private int idPrueba;
    private String nombreJinete;
    private String nombreCaballo;
    private double correccionPrevia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuacion);

        obtenerExtras();
        initViews();
        initToolbar();
        initRecyclerView();
        initViewModel();
        observarDatos();
        configurarEventos();
    }

    /**
     * Obtiene los datos recibidos mediante el Intent.
     */
    private void obtenerExtras() {

        idParticipacion = getIntent().getIntExtra(
                EXTRA_ID_PARTICIPACION, -1);

        idPrueba = getIntent().getIntExtra(
                EXTRA_ID_PRUEBA, -1);

        nombreJinete = getIntent().getStringExtra(
                EXTRA_NOMBRE_JINETE);

        nombreCaballo = getIntent().getStringExtra(
                EXTRA_NOMBRE_CABALLO);

        correccionPrevia = getIntent().getDoubleExtra(
                EXTRA_CORRECCION_PREVIA, 0.0);
    }

    /**
     * Inicializa las vistas de la pantalla.
     */
    private void initViews() {

        tvNotaFinal = findViewById(R.id.tvNotaFinal);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        rgCorreccion = findViewById(R.id.rgCorreccion);

        ((TextView) findViewById(R.id.tvJinete))
                .setText(nombreJinete);

        ((TextView) findViewById(R.id.tvCaballo))
                .setText("🐴 " + nombreCaballo);
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {

        MaterialToolbar toolbar =
                findViewById(R.id.toolbarPuntuacion);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Configura el RecyclerView encargado de mostrar
     * los movimientos a puntuar.
     */
    private void initRecyclerView() {

        adapter = new MovimientoNotaAdapter();

        adapter.setOnNotaCambiadaListener(
                this::recalcular
        );

        RecyclerView rv =
                findViewById(R.id.rvMovimientos);

        rv.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rv.setAdapter(adapter);
    }

    /**
     * Inicializa el ViewModel y carga los datos necesarios.
     */
    private void initViewModel() {

        vm = new ViewModelProvider(this)
                .get(PuntuacionViewModel.class);

        vm.init(
                idPrueba,
                idParticipacion,
                correccionPrevia
        );
    }

    /**
     * Observa los movimientos y notas asociados
     * a la participación.
     */
    private void observarDatos() {

        vm.getMovimientosConNota().observe(this, lista -> {

            adapter.setItems(lista);

            restaurarCorreccion(
                    vm.getCorreccionGuardada()
            );

            recalcular();
        });
    }

    /**
     * Configura los eventos de la pantalla.
     */
    private void configurarEventos() {

        rgCorreccion.setOnCheckedChangeListener(
                (g, id) -> recalcular()
        );

        MaterialButton btnGuardar =
                findViewById(R.id.btnGuardar);

        btnGuardar.setOnClickListener(
                v -> guardar()
        );
    }

    /**
     * Restaura la corrección previamente guardada.
     *
     * @param correccion Valor almacenado en base de datos.
     */
    private void restaurarCorreccion(double correccion) {

        if (correccion < 0) {

            rgCorreccion.check(R.id.rbElim);

        } else if (correccion == 4.0) {

            rgCorreccion.check(R.id.rb04);

        } else if (correccion == 2.0) {

            rgCorreccion.check(R.id.rb02);

        } else {

            rgCorreccion.check(R.id.rb00);
        }
    }

    /**
     * Recalcula la nota final y el porcentaje
     * cada vez que cambia una nota o una corrección.
     */
    private void recalcular() {

        List<MovimientoConNota> items =
                adapter.getItems();

        if (items == null || items.isEmpty()) {
            return;
        }

        if (isEliminado()) {

            tvNotaFinal.setText("ELIMINADO");

            tvNotaFinal.setTextColor(
                    getColor(android.R.color.holo_red_dark)
            );

            tvPorcentaje.setText("");

            return;
        }

        double[] resultado =
                vm.calcular(items, getCorreccion());

        double notaFinal = resultado[0];
        double porcentaje = resultado[1];

        tvNotaFinal.setText(
                String.format(
                        Locale.getDefault(),
                        "Nota: %.2f",
                        notaFinal
                )
        );

        tvNotaFinal.setTextColor(
                getColor(android.R.color.black)
        );

        tvPorcentaje.setText(
                String.format(
                        Locale.getDefault(),
                        "%.3f%%",
                        porcentaje
                )
        );
    }

    /**
     * Comprueba si el participante ha sido eliminado.
     *
     * @return true si está marcado como eliminado.
     */
    private boolean isEliminado() {
        return rgCorreccion.getCheckedRadioButtonId()
                == R.id.rbElim;
    }

    /**
     * Obtiene la corrección seleccionada.
     *
     * @return Valor de penalización.
     */
    private double getCorreccion() {

        int id =
                rgCorreccion.getCheckedRadioButtonId();

        if (id == R.id.rb02) return 2.0;

        if (id == R.id.rb04) return 4.0;

        return 0.0;
    }

    /**
     * Valida que todas las notas y observaciones
     * hayan sido introducidas correctamente.
     *
     * @return true si los datos son válidos.
     */
    private boolean validarNotas() {

        List<MovimientoConNota> items =
                adapter.getItems();

        for (MovimientoConNota m : items) {

            if (m.nota < 0 || m.nota > 10) {

                Toast.makeText(
                        this,
                        "❌ La nota de \"" + m.ejercicio +
                                "\" debe estar entre 0 y 10",
                        Toast.LENGTH_LONG
                ).show();

                return false;
            }

            if (m.observacion == null
                    || m.observacion.trim().isEmpty()) {

                Toast.makeText(
                        this,
                        "❌ Falta observación en: "
                                + m.ejercicio,
                        Toast.LENGTH_LONG
                ).show();

                return false;
            }
        }

        return true;
    }

    /**
     * Guarda la puntuación introducida por el juez.
     */
    private void guardar() {

        List<MovimientoConNota> items =
                adapter.getItems();

        // Validación de corrección seleccionada
        if (rgCorreccion.getCheckedRadioButtonId() == -1) {

            Toast.makeText(
                    this,
                    "Debes seleccionar una opción de corrección.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        boolean eliminado = isEliminado();

        double correccion =
                eliminado ? -1.0 : getCorreccion();

        // Validación de notas
        if (!eliminado && !validarNotas()) {
            return;
        }

        // Construcción de notas
        List<NotaMovimiento> notas =
                new ArrayList<>();

        for (MovimientoConNota m : items) {

            notas.add(
                    new NotaMovimiento(
                            m.nota,
                            m.observacion != null
                                    ? m.observacion.trim()
                                    : "",
                            m.idMovimiento,
                            idParticipacion
                    )
            );
        }

        // Cálculo final
        double[] resultado =
                vm.calcular(items, correccion);

        double notaFinal = resultado[0];
        double porcentaje = resultado[1];

        // Persistencia
        vm.guardar(
                idParticipacion,
                notas,
                notaFinal,
                porcentaje,
                correccion,
                eliminado
        );

        Toast.makeText(
                this,
                "✅ Puntuación guardada",
                Toast.LENGTH_SHORT
        ).show();

        finish();
    }

    /**
     * Gestiona la navegación hacia atrás
     * desde la Toolbar.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}