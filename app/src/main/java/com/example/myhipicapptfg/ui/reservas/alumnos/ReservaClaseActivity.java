package com.example.myhipicapptfg.ui.reservas.alumnos;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Activity encargada de mostrar la pantalla de reserva de clases para alumnos.
 *
 * Responsabilidades:
 * - Mostrar clases disponibles en un RecyclerView
 * - Permitir seleccionar fecha mediante un DatePicker
 * - Gestionar reservas de clases
 */
public class ReservaClaseActivity extends AppCompatActivity {

    private ReservaClaseViewModel viewModel;
    private ReservaClaseAdapter adapter;

    /**
     * ID del alumno recibido desde el Intent.
     */
    private int idAlumno;

    private RecyclerView recyclerClases;
    private View layoutEmpty;
    private TextInputEditText etFecha;

    /**
     * Formateador de fecha para mostrar en la interfaz.
     */
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_clase);

        // Obteniendo el ID del alumno desde el Intent
        idAlumno = getIntent().getIntExtra("ID_ALUMNO", -1);


        // Vincular el Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);

        // Configuración de la acción para ir hacia atrás
        toolbar.setNavigationOnClickListener(v -> finish());

        initViews();
        initViewModel();
        setupRecyclerView();
        observarDatos();

        // Le pasamos el id del alumno al ViewModel
        viewModel.init(idAlumno);

        // Cargamos la fecha de hoy y se lo pasamos al ViewModel
        long hoy = MaterialDatePicker.todayInUtcMilliseconds();

        etFecha.setText(dateFormat.format(hoy));
        viewModel.setFechaFiltro(hoy);
    }

    /**
     * Inicializa referencias de vistas.
     */
    private void initViews() {
        recyclerClases = findViewById(R.id.recyclerClases);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        etFecha = findViewById(R.id.etFechaFiltro);
        etFecha.setOnClickListener(v -> abrirCalendario());
    }

    /**
     * Inicializa el ViewModel.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ReservaClaseViewModel.class);
    }

    /**
     * Configura el RecyclerView y su adapter.
     */
    private void setupRecyclerView() {
        adapter = new ReservaClaseAdapter(false, clase ->
                viewModel.reservarClase(clase.idClase)
        );

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        recyclerClases.setAdapter(adapter);
    }

    /**
     * Observa cambios en los datos del ViewModel.
     */
    private void observarDatos() {

        viewModel.getClases().observe(this, clases -> {
            if (clases != null) {
                adapter.submitList(clases);
                actualizarInterfazVacia(clases.isEmpty());
            }
        });

        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;
            mostrarMensajeEstado(estado);
            viewModel.resetearEstado();
        });
    }

    /**
     * Muestra mensajes según el resultado de la operación de reserva.
     */
    private void mostrarMensajeEstado(String estado) {
        String mensaje;
        int duracion = Snackbar.LENGTH_SHORT;

        switch (estado) {
            case "EXITO":
                mensaje = "¡Reserva confirmada con éxito!";
                break;
            case "ERROR_YA_RESERVADO":
                mensaje = "Ya tienes una reserva para esta clase";
                duracion = Snackbar.LENGTH_LONG;
                break;
            case "ERROR_CLASE_LLENA":
                mensaje = "Lo sentimos, la clase ya está completa";
                duracion = Snackbar.LENGTH_LONG;
                break;
            default:
                mensaje = "Error al procesar la reserva";
                break;
        }

        Snackbar.make(recyclerClases, mensaje, duracion).show();
    }

    /**
     * Muestra u oculta la vista vacía dependiendo de si hay datos.
     */
    private void actualizarInterfazVacia(boolean estaVacio) {
        layoutEmpty.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
        recyclerClases.setVisibility(estaVacio ? View.GONE : View.VISIBLE);
    }

    /**
     * Muestra u oculta la vista vacía dependiendo de si hay datos.
     */
    private void abrirCalendario() {
        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecciona una fecha")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .build();

        datePicker.addOnPositiveButtonClickListener(selection -> {
            etFecha.setText(dateFormat.format(selection));
            viewModel.setFechaFiltro(selection);
        });

        datePicker.show(getSupportFragmentManager(), "DATE_PICKER");
    }
}