package com.example.myhipicapptfg.ui.competiciones.juez.calificacion;

import android.os.Bundle;
import android.view.View;
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

public class PuntuacionActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PARTICIPACION  = "id_participacion";
    public static final String EXTRA_ID_PRUEBA         = "id_prueba";
    public static final String EXTRA_NOMBRE_JINETE     = "nombre_jinete";
    public static final String EXTRA_NOMBRE_CABALLO    = "nombre_caballo";
    public static final String EXTRA_CORRECCION_PREVIA = "correccion_previa";

    private PuntuacionViewModel vm;
    private MovimientoNotaAdapter adapter;

    private TextView   tvNotaFinal, tvPorcentaje;
    private RadioGroup rgCorreccion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntuacion);

        // ── Extras ───────────────────────────────────────────────────────────
        int    idParticipacion  = getIntent().getIntExtra(EXTRA_ID_PARTICIPACION, -1);
        int    idPrueba         = getIntent().getIntExtra(EXTRA_ID_PRUEBA, -1);
        String jinete           = getIntent().getStringExtra(EXTRA_NOMBRE_JINETE);
        String caballo          = getIntent().getStringExtra(EXTRA_NOMBRE_CABALLO);
        double correccionPrevia = getIntent().getDoubleExtra(EXTRA_CORRECCION_PREVIA, 0.0);

        // ── Toolbar ──────────────────────────────────────────────────────────
        // Vincula el Toolbar usando su ID
        MaterialToolbar toolbar = findViewById(R.id.toolbarPuntuacion);

// Configura la acción para ir hacia atrás
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // ── Info participante ─────────────────────────────────────────────────
        ((TextView) findViewById(R.id.tvJinete)).setText(jinete);
        ((TextView) findViewById(R.id.tvCaballo)).setText("🐴 " + caballo);

        // ── RecyclerView ──────────────────────────────────────────────────────
        adapter = new MovimientoNotaAdapter();
        adapter.setOnNotaCambiadaListener(this::recalcular);

        RecyclerView rv = findViewById(R.id.rvMovimientos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // ── Referencias UI ────────────────────────────────────────────────────
        tvNotaFinal  = findViewById(R.id.tvNotaFinal);
        tvPorcentaje = findViewById(R.id.tvPorcentaje);
        rgCorreccion = findViewById(R.id.rgCorreccion);

        // ── ViewModel ─────────────────────────────────────────────────────────
        vm = new ViewModelProvider(this).get(PuntuacionViewModel.class);
        vm.init(idPrueba, idParticipacion, correccionPrevia);

        vm.getMovimientosConNota().observe(this, lista -> {
            adapter.setItems(lista);
            // Restaurar corrección previa la primera vez que llegan los datos
            restaurarCorreccion(vm.getCorreccionGuardada());
            recalcular();
        });

        rgCorreccion.setOnCheckedChangeListener((g, id) -> recalcular());

        // ── Botón guardar ─────────────────────────────────────────────────────
        MaterialButton btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> guardar(idParticipacion));
    }

    /** Restaura el RadioButton según la corrección guardada en BD. */
    private void restaurarCorreccion(double correccion) {
        if (correccion < 0) {             // -1.0 → eliminado
            rgCorreccion.check(R.id.rbElim);
        } else if (correccion == 4.0) {
            rgCorreccion.check(R.id.rb04);
        } else if (correccion == 2.0) {
            rgCorreccion.check(R.id.rb02);
        } else {
            rgCorreccion.check(R.id.rb00);
        }
    }

    /** Recalcula nota y porcentaje en tiempo real usando el ViewModel. */
    private void recalcular() {
        List<MovimientoConNota> items = adapter.getItems();
        if (items == null || items.isEmpty()) return;

        if (isEliminado()) {
            tvNotaFinal.setText("ELIMINADO");
            tvNotaFinal.setTextColor(getColor(android.R.color.holo_red_dark));
            tvPorcentaje.setText("");
            return;
        }

        double[] resultado = vm.calcular(items, getCorreccion());
        double notaFinal   = resultado[0];
        double porcentaje  = resultado[1];

        tvNotaFinal.setText(String.format(Locale.getDefault(), "Nota: %.2f", notaFinal));
        tvNotaFinal.setTextColor(getColor(android.R.color.black));
        tvPorcentaje.setText(String.format(Locale.getDefault(), "%.3f%%", porcentaje));
    }

    private boolean isEliminado() {
        return rgCorreccion.getCheckedRadioButtonId() == R.id.rbElim;
    }

    private double getCorreccion() {
        int id = rgCorreccion.getCheckedRadioButtonId();
        if (id == R.id.rb02) return 2.0;
        if (id == R.id.rb04) return 4.0;
        return 0.0;
    }

    private boolean validarNotas() {

        List<MovimientoConNota> items = adapter.getItems();

        for (MovimientoConNota m : items) {

            if (m.nota < 0 || m.nota > 10) {
                Toast.makeText(this,
                        "❌ La nota de \"" + m.ejercicio + "\" debe estar entre 0 y 10",
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (m.observacion == null || m.observacion.trim().isEmpty()) {
                Toast.makeText(this,
                        "❌ Falta observación en: " + m.ejercicio,
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    private void guardar(int idParticipacion) {
        List<MovimientoConNota> items = adapter.getItems();

        // 1. Corrección seleccionada
        if (rgCorreccion.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this,
                    "Debes seleccionar una opción de corrección.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        boolean eliminado  = isEliminado();
        double  correccion = eliminado ? -1.0 : getCorreccion();

        // 2. Validar notas y observaciones (solo si no está eliminado)
        if (!eliminado) {
            if (!validarNotas()) {
                return;
            }
        }

        // 3. Construir lista de NotaMovimiento
        List<NotaMovimiento> notas = new ArrayList<>();
        for (MovimientoConNota m : items) {
            notas.add(new NotaMovimiento(
                    m.nota, m.observacion != null ? m.observacion.trim() : "",
                    m.idMovimiento, idParticipacion));
        }

        // 4. Calcular con el ViewModel (única fuente de verdad)
        double[] resultado = vm.calcular(items, correccion);
        double   notaFinal  = resultado[0];
        double   porcentaje = resultado[1];

        // 5. Guardar
        vm.guardar(idParticipacion, notas, notaFinal, porcentaje, correccion, eliminado);

        Toast.makeText(this, "✅ Puntuación guardada", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}