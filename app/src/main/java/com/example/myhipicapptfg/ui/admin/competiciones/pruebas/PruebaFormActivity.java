package com.example.myhipicapptfg.ui.admin.competiciones.pruebas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Movimiento;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

public class PruebaFormActivity extends AppCompatActivity {

    private static final int MIN_MOVIMIENTOS = 10;

    private GestionPruebasViewModel viewModel;

    private TextInputEditText    etNombrePrueba;
    private TextInputLayout      layNombrePrueba, layJuez;
    private AutoCompleteTextView spinnerCategoria, spinnerNivel, spinnerJuez;
    private LinearLayout         containerMovimientos;
    private MaterialButton       btnAnadirMovimiento;
    private TextView             tvContadorMovimientos;
    private MaterialButton       btnGuardar;

    private final List<TextInputEditText> listEjercicio  = new ArrayList<>();
    private final List<TextInputEditText> listLetra       = new ArrayList<>();
    private final List<TextInputEditText> listCoeficiente = new ArrayList<>();
    private final List<TextInputEditText> listDirectriz   = new ArrayList<>();

    // Lista de jueces cargados para poder obtener su ID al guardar
    private List<Usuario> listaJueces = new ArrayList<>();

    private int     idCompeticion = -1;
    private int     idPrueba      = -1;
    private boolean modoEdicion   = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_form);

        idCompeticion = getIntent().getIntExtra("ID_COMPETICION", -1);

        if (getIntent().hasExtra("ID_PRUEBA")) {
            idPrueba    = getIntent().getIntExtra("ID_PRUEBA", -1);
            modoEdicion = true;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(modoEdicion ? "Editar prueba" : "Nueva prueba");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        viewModel = new ViewModelProvider(this).get(GestionPruebasViewModel.class);

        initViews();
        setupSpinners();
        observarJueces();
        observarEstado();

        for (int i = 0; i < MIN_MOVIMIENTOS; i++) {
            agregarFilaMovimiento(null);
        }

        if (modoEdicion) cargarDatos();

        btnAnadirMovimiento.setOnClickListener(v -> agregarFilaMovimiento(null));
        btnGuardar.setOnClickListener(v -> guardar());
    }

    // ─────────────────────────────────────────────────
    private void initViews() {
        etNombrePrueba        = findViewById(R.id.etNombrePrueba);
        layNombrePrueba       = findViewById(R.id.layNombrePrueba);
        layJuez               = findViewById(R.id.layJuezPrueba);
        spinnerCategoria      = findViewById(R.id.spinnerCategoria);
        spinnerNivel          = findViewById(R.id.spinnerNivel);
        spinnerJuez           = findViewById(R.id.spinnerJuezPrueba);
        containerMovimientos  = findViewById(R.id.containerMovimientos);
        btnAnadirMovimiento   = findViewById(R.id.btnAnadirMovimiento);
        tvContadorMovimientos = findViewById(R.id.tvContadorMovimientos);
        btnGuardar            = findViewById(R.id.btnGuardarPrueba);
    }

    // ─────────────────────────────────────────────────
    private void setupSpinners() {
        spinnerCategoria.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{
                        Prueba.ALEVIN, Prueba.INFANTIL, Prueba.JUVENIL_0,
                        Prueba.JUVENIL_1, Prueba.JOVEN_JINETE, Prueba.U25, Prueba.ADULTO
                }));

        spinnerNivel.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{
                        Prueba.NIVEL_0, Prueba.NIVEL_1, Prueba.NIVEL_2, Prueba.NIVEL_3,
                        Prueba.NIVEL_4, Prueba.SAN_JORGE, Prueba.INTERMEDIA, Prueba.GRAN_PREMIO
                }));
    }

    // ─────────────────────────────────────────────────
    // Carga los jueces activos en el spinner
    private void observarJueces() {
        viewModel.getJuecesActivos().observe(this, jueces -> {
            if (jueces == null) return;

            listaJueces = jueces;

            spinnerJuez.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    jueces // Usuario.toString() debe devolver nombre completo
            ));

            // En modo edición precargamos el juez una vez lleguen los datos
            if (modoEdicion) precargarJuezSiDisponible();
        });
    }

    // ─────────────────────────────────────────────────
    private void cargarDatos() {
        viewModel.buscarPorId(idPrueba).observe(this, prueba -> {
            if (prueba == null) return;

            etNombrePrueba.setText(prueba.nombre);
            spinnerCategoria.setText(prueba.categoria, false);
            spinnerNivel.setText(prueba.nivel, false);

            // Guardamos el idJuez de la prueba para precargar el spinner
            // cuando la lista de jueces ya esté disponible
            if (prueba.idJuez != null) {
                getWindow().getDecorView().setTag(R.id.spinnerJuezPrueba, prueba.idJuez);
                precargarJuezSiDisponible();
            }
        });

        viewModel.getMovimientos(idPrueba).observe(this, movimientos -> {
            if (movimientos == null || movimientos.isEmpty()) return;

            containerMovimientos.removeAllViews();
            listEjercicio.clear();
            listLetra.clear();
            listCoeficiente.clear();
            listDirectriz.clear();

            for (Movimiento m : movimientos) agregarFilaMovimiento(m);

            while (listEjercicio.size() < MIN_MOVIMIENTOS) agregarFilaMovimiento(null);
        });
    }

    /**
     * Precarga el juez en el spinner solo cuando AMBOS están disponibles:
     * la lista de jueces Y el idJuez de la prueba guardado en el tag.
     */
    private void precargarJuezSiDisponible() {
        if (listaJueces.isEmpty()) return;

        Object tag = getWindow().getDecorView().getTag(R.id.spinnerJuezPrueba);
        if (tag == null) return;

        int idJuez = (int) tag;

        for (Usuario u : listaJueces) {
            if (u.idUsuario == idJuez) {
                spinnerJuez.setText(u.toString(), false);
                break;
            }
        }
    }

    // ─────────────────────────────────────────────────
    private void agregarFilaMovimiento(@Nullable Movimiento movimiento) {
        int numero = listEjercicio.size() + 1;

        View fila = LayoutInflater.from(this)
                .inflate(R.layout.item_movimiento_form, containerMovimientos, false);

        TextView tvOrden = fila.findViewById(R.id.tvOrdenMovimiento);
        tvOrden.setText("Movimiento " + numero);

        TextInputEditText etEj  = fila.findViewById(R.id.etEjercicioMovimiento);
        TextInputEditText etLet = fila.findViewById(R.id.etLetraMovimiento);
        TextInputEditText etCoe = fila.findViewById(R.id.etCoeficienteMovimiento);
        TextInputEditText etDir = fila.findViewById(R.id.etDirectrizMovimiento);

        fila.findViewById(R.id.btnEliminarMovimiento)
                .setOnClickListener(v -> eliminarFila(fila));

        if (movimiento != null) {
            etEj.setText(movimiento.ejercicio);
            etLet.setText(movimiento.letra);
            etCoe.setText(String.valueOf(movimiento.coeficiente));
            etDir.setText(movimiento.directriz);
        }

        listEjercicio.add(etEj);
        listLetra.add(etLet);
        listCoeficiente.add(etCoe);
        listDirectriz.add(etDir);

        containerMovimientos.addView(fila);
        actualizarContador();
    }

    // ─────────────────────────────────────────────────
    private void eliminarFila(View fila) {
        if (listEjercicio.size() <= MIN_MOVIMIENTOS) {
            Toast.makeText(this, "Mínimo " + MIN_MOVIMIENTOS + " movimientos",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        int index = containerMovimientos.indexOfChild(fila);
        listEjercicio.remove(index);
        listLetra.remove(index);
        listCoeficiente.remove(index);
        listDirectriz.remove(index);
        containerMovimientos.removeView(fila);
        renumerarFilas();
        actualizarContador();
    }

    // ─────────────────────────────────────────────────
    private void renumerarFilas() {
        for (int i = 0; i < containerMovimientos.getChildCount(); i++) {
            View fila = containerMovimientos.getChildAt(i);
            TextView tv = fila.findViewById(R.id.tvOrdenMovimiento);
            if (tv != null) tv.setText("Movimiento " + (i + 1));
        }
    }

    // ─────────────────────────────────────────────────
    private void actualizarContador() {
        int total = listEjercicio.size();
        if (total < MIN_MOVIMIENTOS) {
            tvContadorMovimientos.setText(total + " movimientos — faltan "
                    + (MIN_MOVIMIENTOS - total) + " para guardar");
            tvContadorMovimientos.setTextColor(getColor(android.R.color.holo_red_dark));
            btnGuardar.setEnabled(false);
        } else {
            tvContadorMovimientos.setText(total + " movimientos ✓");
            tvContadorMovimientos.setTextColor(getColor(android.R.color.holo_green_dark));
            btnGuardar.setEnabled(true);
        }
    }

    // ─────────────────────────────────────────────────
    private boolean validar() {
        boolean ok = true;

        if (etNombrePrueba.getText().toString().trim().isEmpty()) {
            layNombrePrueba.setError("Obligatorio");
            ok = false;
        } else layNombrePrueba.setError(null);

        if (spinnerCategoria.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (spinnerNivel.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Selecciona un nivel", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        // Juez obligatorio
        if (spinnerJuez.getText().toString().trim().isEmpty()) {
            layJuez.setError("Selecciona un juez");
            ok = false;
        } else layJuez.setError(null);

        if (listEjercicio.size() < MIN_MOVIMIENTOS) {
            Toast.makeText(this, "Se necesitan al menos " + MIN_MOVIMIENTOS
                    + " movimientos", Toast.LENGTH_SHORT).show();
            ok = false;
        }

        for (int i = 0; i < listEjercicio.size(); i++) {
            if (listEjercicio.get(i).getText().toString().trim().isEmpty()) {
                Toast.makeText(this, "El movimiento " + (i + 1)
                        + " necesita un ejercicio", Toast.LENGTH_SHORT).show();
                ok = false;
                break;
            }
        }

        for (int i = 0; i < listCoeficiente.size(); i++) {
            String coefStr = listCoeficiente.get(i).getText().toString().trim();
            if (!coefStr.isEmpty()) {
                try {
                    double coef = Double.parseDouble(coefStr);
                    if (coef <= 0 || coef > 2) {
                        Toast.makeText(this, "El coeficiente del movimiento " + (i + 1)
                                + " debe estar entre 0 y 2", Toast.LENGTH_SHORT).show();
                        ok = false;
                        break;
                    }
                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Coeficiente inválido en el movimiento " + (i + 1),
                            Toast.LENGTH_SHORT).show();
                    ok = false;
                    break;
                }
            }
        }

        for (int i = 0; i < listLetra.size(); i++) {
            String letra = listLetra.get(i).getText().toString().trim().toUpperCase();

            if (!letra.isEmpty()) {
                if (!validarFormatoLetras(letra)) {
                    Toast.makeText(this,
                            "Formato de letras inválido en el movimiento " + (i + 1)
                                    + " (ej: E-B-M)",
                            Toast.LENGTH_SHORT).show();
                    ok = false;
                    break;
                }
            }
        }

        return ok;
    }

    // ─────────────────────────────────────────────────
    private void guardar() {
        if (!validar()) return;

        // Obtener el ID del juez seleccionado
        String selJuez = spinnerJuez.getText().toString();
        Integer idJuezSeleccionado = null;
        for (Usuario u : listaJueces) {
            if (u.toString().equals(selJuez)) {
                idJuezSeleccionado = u.idUsuario;
                break;
            }
        }

        Prueba prueba = new Prueba(
                etNombrePrueba.getText().toString().trim(),
                spinnerCategoria.getText().toString().trim(),
                spinnerNivel.getText().toString().trim(),
                idCompeticion,
                idJuezSeleccionado   // ← nuevo parámetro
        );

        List<Movimiento> movimientos = new ArrayList<>();
        for (int i = 0; i < listEjercicio.size(); i++) {
            String coefStr = listCoeficiente.get(i).getText().toString().trim();
            double coef    = coefStr.isEmpty() ? 1.0 : Double.parseDouble(coefStr);
            movimientos.add(new Movimiento(
                    listEjercicio.get(i).getText().toString().trim(),
                    listLetra.get(i).getText().toString()
                            .trim()
                            .toUpperCase()
                            .replace(" ", ""),
                    i + 1, coef,
                    listDirectriz.get(i).getText().toString().trim(),
                    0
            ));
        }

        if (modoEdicion) {
            prueba.idPrueba = idPrueba;
            viewModel.actualizarPruebaConMovimientos(prueba, movimientos);
        } else {
            viewModel.guardarPruebaConMovimientos(prueba, movimientos);
        }
    }

    // ─────────────────────────────────────────────────
    private void observarEstado() {
        viewModel.getEstado().observe(this, estado -> {
            if (estado == null) return;
            switch (estado) {
                case "EXITO":
                    Toast.makeText(this, "Guardado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case "ERROR_NOMBRE_DUPLICADO":
                    layNombrePrueba.setError("Ya existe una prueba con ese nombre");
                    break;
                case "ERROR_COMPETICION_NO_EXISTE":
                    Toast.makeText(this, "Error: competición no encontrada",
                            Toast.LENGTH_SHORT).show();
                    break;
                case "ERROR_BD":
                    Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }


    private boolean validarFormatoLetras(String input) {
        if (input == null) return false;

        input = input.trim().toUpperCase();

        // Solo letras y guiones
        if (!input.matches("[A-Z\\-]+")) return false;

        // No empezar ni terminar con guion
        if (input.startsWith("-") || input.endsWith("-")) return false;

        // No dobles guiones
        if (input.contains("--")) return false;

        String letrasValidas = "ABCDEFGHKMPRSVWX";

        // Si NO hay guion → es una sola letra
        if (!input.contains("-")) {
            return input.length() == 1 && letrasValidas.contains(input);
        }

        // Si hay guiones → secuencia
        String[] partes = input.split("-");

        for (String p : partes) {
            if (p.isEmpty()) return false;
            if (!letrasValidas.contains(p)) return false;
        }

        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}