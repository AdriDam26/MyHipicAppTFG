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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity encargada de crear y editar una Prueba de doma clásica.
 *
 * Permite al administrador definir los datos básicos de la prueba
 * (nombre, categoría, nivel y juez) y gestionar dinámicamente
 * los movimientos que la componen.
 *
 * Funcionalidades principales:
 * - Creación y edición de pruebas mediante MVVM.
 * - Selección de categoría, nivel y juez desde spinners.
 * - Adición y eliminación dinámica de filas de movimientos.
 * - Validación completa del formulario antes de guardar.
 * - Precarga del juez en modo edición cuando ambos datos
 *   (lista de jueces e ID del juez) están disponibles.
 */
public class PruebaFormActivity extends AppCompatActivity {

    // =========================================================
    // CONSTANTES
    // =========================================================

    /** Número mínimo de movimientos requeridos para guardar la prueba. */
    private static final int MIN_MOVIMIENTOS = Prueba.MIN_MOVIMIENTOS;

    // =========================================================
    // VIEWMODEL
    // =========================================================

    private GestionPruebasViewModel viewModel;

    // =========================================================
    // VISTAS
    // =========================================================

    private TextInputEditText    etNombrePrueba;
    private TextInputLayout      layNombrePrueba;
    private TextInputLayout      layJuez;
    private AutoCompleteTextView spinnerCategoria;
    private AutoCompleteTextView spinnerNivel;
    private AutoCompleteTextView spinnerJuez;
    private LinearLayout         containerMovimientos;
    private MaterialButton       btnAnadirMovimiento;
    private MaterialButton       btnGuardar;
    private TextView             tvContadorMovimientos;

    // =========================================================
    // LISTAS DE CONTROL DE MOVIMIENTOS
    // =========================================================

    /**
     * Cada lista almacena los campos de texto de su atributo
     * correspondiente para cada fila de movimiento, en el mismo
     * orden en que aparecen en el contenedor visual.
     */
    private final List<TextInputEditText> listEjercicio   = new ArrayList<>();
    private final List<TextInputEditText> listLetra        = new ArrayList<>();
    private final List<TextInputEditText> listCoeficiente  = new ArrayList<>();
    private final List<TextInputEditText> listDirectriz    = new ArrayList<>();

    // =========================================================
    // DATOS
    // =========================================================

    /**
     * Lista de jueces cargados desde la base de datos.
     * Se utiliza para resolver el ID del juez seleccionado al guardar.
     */
    private List<Usuario> listaJueces = new ArrayList<>();

    // =========================================================
    // ESTADO
    // =========================================================

    private int     idCompeticion = -1;
    private int     idPrueba      = -1;
    private boolean modoEdicion   = false;

    // =========================================================
    // CICLO DE VIDA
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_form);

        recogerExtras();
        configurarActionBar();

        viewModel = new ViewModelProvider(this)
                .get(GestionPruebasViewModel.class);

        initViews();
        setupToolbar();
        setupSpinners();
        setupBotones();
        observarJueces();
        observarEstado();

        inicializarFilasMinimas();

        if (modoEdicion) {
            cargarDatos();
        }
    }

    // =========================================================
    // INIT
    // =========================================================

    /**
     * Recoge los extras del Intent que lanzó la Activity.
     * Determina si se trata de una creación o una edición.
     */
    private void recogerExtras() {

        idCompeticion = getIntent().getIntExtra("ID_COMPETICION", -1);

        if (getIntent().hasExtra("ID_PRUEBA")) {
            idPrueba    = getIntent().getIntExtra("ID_PRUEBA", -1);
            modoEdicion = true;
        }
    }

    /**
     * Configura el título del ActionBar según el modo activo.
     */
    private void configurarActionBar() {

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(
                    modoEdicion ? "Editar prueba" : "Nueva prueba"
            );
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa las referencias a las vistas del layout.
     */
    private void initViews() {
        etNombrePrueba       = findViewById(R.id.etNombrePrueba);
        layNombrePrueba      = findViewById(R.id.layNombrePrueba);
        layJuez              = findViewById(R.id.layJuezPrueba);
        spinnerCategoria     = findViewById(R.id.spinnerCategoria);
        spinnerNivel         = findViewById(R.id.spinnerNivel);
        spinnerJuez          = findViewById(R.id.spinnerJuezPrueba);
        containerMovimientos = findViewById(R.id.containerMovimientos);
        btnAnadirMovimiento  = findViewById(R.id.btnAnadirMovimiento);
        tvContadorMovimientos= findViewById(R.id.tvContadorMovimientos);
        btnGuardar           = findViewById(R.id.btnGuardarPrueba);
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarPruebaForm);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Asigna los listeners a los botones de añadir movimiento y guardar.
     */
    private void setupBotones() {
        btnAnadirMovimiento.setOnClickListener(v -> agregarFilaMovimiento(null));
        btnGuardar.setOnClickListener(v -> guardar());
    }

    /**
     * Configura los adaptadores de los spinners de categoría y nivel
     * con los valores constantes definidos en la entidad Prueba.
     */
    private void setupSpinners() {

        spinnerCategoria.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{
                        Prueba.ALEVIN, Prueba.INFANTIL, Prueba.JUVENIL_0,
                        Prueba.JUVENIL_1, Prueba.JOVEN_JINETE,
                        Prueba.U25, Prueba.ADULTO
                }));

        spinnerNivel.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                new String[]{
                        Prueba.NIVEL_0, Prueba.NIVEL_1, Prueba.NIVEL_2,
                        Prueba.NIVEL_3, Prueba.NIVEL_4, Prueba.SAN_JORGE,
                        Prueba.INTERMEDIA, Prueba.GRAN_PREMIO
                }));
    }

    /**
     * Añade el número mínimo de filas de movimiento vacías
     * al iniciar la Activity en modo creación.
     */
    private void inicializarFilasMinimas() {
        for (int i = 0; i < MIN_MOVIMIENTOS; i++) {
            agregarFilaMovimiento(null);
        }
    }

    // =========================================================
    // OBSERVADORES
    // =========================================================

    /**
     * Observa la lista de jueces activos y actualiza el spinner.
     *
     * En modo edición, intenta precargar el juez seleccionado
     * una vez que la lista esté disponible.
     */
    private void observarJueces() {

        viewModel.getJuecesActivos().observe(this, jueces -> {

            if (jueces == null) return;

            listaJueces = jueces;

            spinnerJuez.setAdapter(new ArrayAdapter<>(
                    this,
                    android.R.layout.simple_dropdown_item_1line,
                    jueces
            ));

            if (modoEdicion) precargarJuezSiDisponible();
        });
    }

    /**
     * Observa el estado de la última operación de guardado.
     *
     * Muestra mensajes de éxito o error en la interfaz y,
     * en caso de éxito, cierra la Activity.
     */
    private void observarEstado() {

        viewModel.getEstado().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Guardado correctamente",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    break;

                case "ERROR_NOMBRE_DUPLICADO":
                    layNombrePrueba.setError(
                            "Ya existe una prueba con ese nombre"
                    );
                    break;

                case "ERROR_COMPETICION_NO_EXISTE":
                    Toast.makeText(this,
                            "Error: competición no encontrada",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error al guardar",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }

    // =========================================================
    // CARGA DE DATOS (MODO EDICIÓN)
    // =========================================================

    /**
     * Carga los datos de la prueba y sus movimientos desde la base
     * de datos y rellena el formulario con ellos.
     *
     * Se ejecuta únicamente en modo edición.
     */
    private void cargarDatos() {

        cargarDatosPrueba();
        cargarMovimientos();
    }

    /**
     * Observa la prueba por su ID y rellena los campos básicos
     * del formulario (nombre, categoría, nivel y juez).
     */
    private void cargarDatosPrueba() {

        viewModel.buscarPorId(idPrueba).observe(this, prueba -> {

            if (prueba == null) return;

            etNombrePrueba.setText(prueba.nombre);
            spinnerCategoria.setText(prueba.categoria, false);
            spinnerNivel.setText(prueba.nivel, false);

            if (prueba.idJuez != null) {
                getWindow().getDecorView()
                        .setTag(R.id.spinnerJuezPrueba, prueba.idJuez);
                precargarJuezSiDisponible();
            }
        });
    }

    /**
     * Observa los movimientos asociados a la prueba y genera
     * dinámicamente las filas del formulario con sus datos.
     *
     * Garantiza que siempre haya al menos el mínimo de filas requerido.
     */
    private void cargarMovimientos() {

        viewModel.getMovimientos(idPrueba).observe(this, movimientos -> {

            if (movimientos == null || movimientos.isEmpty()) return;

            containerMovimientos.removeAllViews();
            listEjercicio.clear();
            listLetra.clear();
            listCoeficiente.clear();
            listDirectriz.clear();

            for (Movimiento m : movimientos) {
                agregarFilaMovimiento(m);
            }

            while (listEjercicio.size() < MIN_MOVIMIENTOS) {
                agregarFilaMovimiento(null);
            }
        });
    }

    /**
     * Precarga el juez en el spinner solo cuando ambos datos
     * están disponibles: la lista de jueces y el ID del juez
     * almacenado en el tag de la ventana.
     *
     * Este mecanismo resuelve la asincronía entre los dos
     * observadores independientes que proveen esos datos.
     */
    private void precargarJuezSiDisponible() {

        if (listaJueces.isEmpty()) return;

        Object tag = getWindow().getDecorView()
                .getTag(R.id.spinnerJuezPrueba);

        if (tag == null) return;

        int idJuez = (int) tag;

        for (Usuario u : listaJueces) {
            if (u.idUsuario == idJuez) {
                spinnerJuez.setText(u.toString(), false);
                break;
            }
        }
    }

    // =========================================================
    // GESTIÓN DE FILAS DE MOVIMIENTOS
    // =========================================================

    /**
     * Infla y añade una nueva fila de movimiento al contenedor.
     *
     * Si se proporciona un movimiento existente, rellena los campos
     * con sus datos. En caso contrario, los deja vacíos.
     *
     * El ID del movimiento se almacena como tag en la fila para
     * recuperarlo al guardar y distinguir inserciones de actualizaciones.
     *
     * @param movimiento Movimiento a precargar, o null para fila vacía.
     */
    private void agregarFilaMovimiento(@Nullable Movimiento movimiento) {

        int numero = listEjercicio.size() + 1;

        View fila = LayoutInflater.from(this)
                .inflate(R.layout.item_movimiento_form,
                        containerMovimientos, false);

        TextView tvOrden = fila.findViewById(R.id.tvOrdenMovimiento);
        tvOrden.setText("Movimiento " + numero);

        TextInputEditText etEj  = fila.findViewById(R.id.etEjercicioMovimiento);
        TextInputEditText etLet = fila.findViewById(R.id.etLetraMovimiento);
        TextInputEditText etCoe = fila.findViewById(R.id.etCoeficienteMovimiento);
        TextInputEditText etDir = fila.findViewById(R.id.etDirectrizMovimiento);

        fila.setTag(movimiento != null ? movimiento.idMovimiento : 0);

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

    /**
     * Elimina una fila de movimiento del contenedor.
     *
     * No permite eliminar si el número de movimientos resultante
     * sería inferior al mínimo requerido.
     *
     * @param fila Vista de la fila que se desea eliminar.
     */
    private void eliminarFila(View fila) {

        if (listEjercicio.size() <= MIN_MOVIMIENTOS) {
            Toast.makeText(this,
                    "Mínimo " + MIN_MOVIMIENTOS + " movimientos",
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

    /**
     * Actualiza el texto de orden de cada fila tras una eliminación,
     * garantizando que la numeración sea siempre consecutiva.
     */
    private void renumerarFilas() {

        for (int i = 0; i < containerMovimientos.getChildCount(); i++) {

            View fila = containerMovimientos.getChildAt(i);
            TextView tv = fila.findViewById(R.id.tvOrdenMovimiento);

            if (tv != null) tv.setText("Movimiento " + (i + 1));
        }
    }

    /**
     * Actualiza el contador visual de movimientos y habilita o
     * deshabilita el botón de guardar según si se cumple el mínimo.
     */
    private void actualizarContador() {

        int total = listEjercicio.size();

        if (total < MIN_MOVIMIENTOS) {

            tvContadorMovimientos.setText(
                    total + " movimientos — faltan "
                            + (MIN_MOVIMIENTOS - total) + " para guardar"
            );
            tvContadorMovimientos.setTextColor(
                    getColor(android.R.color.holo_red_dark)
            );
            btnGuardar.setEnabled(false);

        } else {

            tvContadorMovimientos.setText(total + " movimientos ✓");
            tvContadorMovimientos.setTextColor(
                    getColor(android.R.color.holo_green_dark)
            );
            btnGuardar.setEnabled(true);
        }
    }

    // =========================================================
    // GUARDAR
    // =========================================================

    /**
     * Valida el formulario, construye los objetos Prueba y Movimiento
     * y delega el guardado al ViewModel.
     *
     * En modo edición actualiza la prueba existente.
     * En modo creación inserta una nueva prueba junto a sus movimientos.
     */
    private void guardar() {

        if (!validar()) return;

        Prueba prueba = new Prueba(
                etNombrePrueba.getText().toString().trim(),
                spinnerCategoria.getText().toString().trim(),
                spinnerNivel.getText().toString().trim(),
                idCompeticion,
                obtenerIdJuezSeleccionado()
        );

        List<Movimiento> movimientos = construirListaMovimientos();

        if (modoEdicion) {
            prueba.idPrueba = idPrueba;
            viewModel.actualizarPruebaConMovimientos(prueba, movimientos);
        } else {
            viewModel.guardarPruebaConMovimientos(prueba, movimientos);
        }
    }

    /**
     * Construye la lista de movimientos a partir de los campos
     * de texto de cada fila del contenedor.
     *
     * Si el campo de coeficiente está vacío, asigna el valor por
     * defecto de 1.0. El ID de cada movimiento se recupera del
     * tag de su fila para distinguir inserciones de actualizaciones.
     *
     * @return Lista de movimientos listos para persistir.
     */
    private List<Movimiento> construirListaMovimientos() {

        List<Movimiento> movimientos = new ArrayList<>();

        for (int i = 0; i < listEjercicio.size(); i++) {

            String coefStr = listCoeficiente.get(i)
                    .getText().toString().trim();

            double coef = coefStr.isEmpty()
                    ? 1.0
                    : Double.parseDouble(coefStr);

            Movimiento m = new Movimiento(
                    listEjercicio.get(i).getText().toString().trim(),
                    listLetra.get(i).getText().toString()
                            .trim().toUpperCase().replace(" ", ""),
                    i + 1,
                    coef,
                    listDirectriz.get(i).getText().toString().trim(),
                    0
            );

            m.idMovimiento = (int) containerMovimientos
                    .getChildAt(i).getTag();

            movimientos.add(m);
        }

        return movimientos;
    }

    /**
     * Busca y devuelve el ID del juez actualmente seleccionado
     * en el spinner, comparando por representación textual.
     *
     * @return ID del juez seleccionado, o null si no se encuentra.
     */
    private Integer obtenerIdJuezSeleccionado() {

        String selJuez = spinnerJuez.getText().toString();

        for (Usuario u : listaJueces) {
            if (u.toString().equals(selJuez)) {
                return u.idUsuario;
            }
        }

        return null;
    }

    // =========================================================
    // VALIDACIÓN
    // =========================================================

    /**
     * Valida todos los campos del formulario antes de guardar.
     *
     * Comprobaciones realizadas:
     * - Nombre de la prueba (obligatorio).
     * - Categoría seleccionada (obligatoria).
     * - Nivel seleccionado (obligatorio).
     * - Juez seleccionado (obligatorio).
     * - Número mínimo de movimientos.
     * - Ejercicio de cada movimiento (obligatorio).
     * - Coeficiente de cada movimiento (rango 0-2 si se indica).
     * - Formato de las letras de cada movimiento.
     *
     * @return true si el formulario es válido, false en caso contrario.
     */
    private boolean validar() {

        boolean ok = true;

        if (etNombrePrueba.getText().toString().trim().isEmpty()) {
            layNombrePrueba.setError("Obligatorio");
            ok = false;
        } else {
            layNombrePrueba.setError(null);
        }

        if (spinnerCategoria.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Selecciona una categoría",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (spinnerNivel.getText().toString().trim().isEmpty()) {
            Toast.makeText(this,
                    "Selecciona un nivel",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        if (spinnerJuez.getText().toString().trim().isEmpty()) {
            layJuez.setError("Selecciona un juez");
            ok = false;
        } else {
            layJuez.setError(null);
        }

        if (listEjercicio.size() < MIN_MOVIMIENTOS) {
            Toast.makeText(this,
                    "Se necesitan al menos " + MIN_MOVIMIENTOS + " movimientos",
                    Toast.LENGTH_SHORT).show();
            ok = false;
        }

        ok = validarEjercicios(ok);
        ok = validarCoeficientes(ok);
        ok = validarLetras(ok);

        return ok;
    }

    /**
     * Comprueba que cada movimiento tenga un ejercicio definido.
     *
     * @param ok Estado de validación acumulado hasta este punto.
     * @return false si algún ejercicio está vacío, ok en caso contrario.
     */
    private boolean validarEjercicios(boolean ok) {

        for (int i = 0; i < listEjercicio.size(); i++) {

            if (listEjercicio.get(i).getText()
                    .toString().trim().isEmpty()) {

                Toast.makeText(this,
                        "El movimiento " + (i + 1)
                                + " necesita un ejercicio",
                        Toast.LENGTH_SHORT).show();

                return false;
            }
        }

        return ok;
    }

    /**
     * Comprueba que los coeficientes indicados estén en el rango (0, 2]
     * y sean valores numéricos válidos.
     *
     * @param ok Estado de validación acumulado hasta este punto.
     * @return false si algún coeficiente es inválido, ok en caso contrario.
     */
    private boolean validarCoeficientes(boolean ok) {

        for (int i = 0; i < listCoeficiente.size(); i++) {

            String coefStr = listCoeficiente.get(i)
                    .getText().toString().trim();

            if (coefStr.isEmpty()) continue;

            try {
                double coef = Double.parseDouble(coefStr);

                if (coef <= 0 || coef > 2) {
                    Toast.makeText(this,
                            "El coeficiente del movimiento " + (i + 1)
                                    + " debe estar entre 0 y 2",
                            Toast.LENGTH_SHORT).show();
                    return false;
                }

            } catch (NumberFormatException e) {
                Toast.makeText(this,
                        "Coeficiente inválido en el movimiento " + (i + 1),
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return ok;
    }

    /**
     * Comprueba que las letras de cada movimiento cumplan el formato
     * de las letras de pista de doma clásica (ej: E, A-K, B-M-F).
     *
     * @param ok Estado de validación acumulado hasta este punto.
     * @return false si alguna letra tiene formato incorrecto,
     *         ok en caso contrario.
     */
    private boolean validarLetras(boolean ok) {

        for (int i = 0; i < listLetra.size(); i++) {

            String letra = listLetra.get(i).getText()
                    .toString().trim().toUpperCase();

            if (!letra.isEmpty() && !validarFormatoLetras(letra)) {
                Toast.makeText(this,
                        "Formato de letras inválido en el movimiento "
                                + (i + 1) + " (ej: E-B-M)",
                        Toast.LENGTH_SHORT).show();
                return false;
            }
        }

        return ok;
    }

    /**
     * Valida que una cadena de letras cumpla el formato de
     * letras de pista de doma clásica.
     *
     * Reglas aplicadas:
     * - Solo letras del alfabeto de pista y guiones.
     * - No puede empezar ni terminar con guion.
     * - No se permiten guiones dobles consecutivos.
     * - Sin guion: debe ser una única letra válida.
     * - Con guiones: cada segmento debe ser una letra válida.
     *
     * Letras válidas: A B C D E F G H K M P R S V W X.
     *
     * @param input Cadena a validar.
     * @return true si el formato es correcto, false en caso contrario.
     */
    private boolean validarFormatoLetras(String input) {

        if (input == null) return false;

        input = input.trim().toUpperCase();

        if (!input.matches("[A-Z\\-]+"))        return false;
        if (input.startsWith("-"))              return false;
        if (input.endsWith("-"))                return false;
        if (input.contains("--"))               return false;

        String letrasValidas = "ABCDEFGHKMPRSVWX";

        if (!input.contains("-")) {
            return input.length() == 1
                    && letrasValidas.contains(input);
        }

        for (String parte : input.split("-")) {
            if (parte.isEmpty())                    {
                return false;
            }
            if (!letrasValidas.contains(parte))     {
                return false;
            }
        }

        return true;
    }

    // =========================================================
    // NAVEGACIÓN
    // =========================================================

    /**
     * Cierra la Activity al pulsar la flecha de retroceso
     * del ActionBar nativo.
     *
     * @return true para indicar que la navegación ha sido gestionada.
     */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}