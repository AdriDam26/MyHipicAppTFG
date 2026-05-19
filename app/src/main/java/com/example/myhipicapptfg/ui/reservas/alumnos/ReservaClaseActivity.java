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

public class ReservaClaseActivity extends AppCompatActivity {

    private ReservaClaseViewModel viewModel;
    private ReservaClaseAdapter adapter;
    private int idAlumno;

    private RecyclerView recyclerClases;
    private View layoutEmpty;
    private TextInputEditText etFecha;

    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_clase);

        idAlumno = getIntent().getIntExtra("ID_ALUMNO", -1);
        if (idAlumno == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        initViewModel();
        setupRecyclerView();
        observarDatos();

        // 👇 IMPORTANTE: cargar alumno primero
        viewModel.cargarDatosAlumno(idAlumno);

        // FECHA POR DEFECTO (HOY)
        long hoy = MaterialDatePicker.todayInUtcMilliseconds();

        etFecha.setText(dateFormat.format(hoy)); // 👈 ESTO ES LO QUE TE FALTABA
        viewModel.setFechaFiltro(hoy);
    }

    private void initViews() {
        recyclerClases = findViewById(R.id.recyclerClases);
        layoutEmpty = findViewById(R.id.layoutEmpty);
        etFecha = findViewById(R.id.etFechaFiltro);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("Reservar Clase");

        toolbar.setNavigationOnClickListener(v -> finish());

        etFecha.setOnClickListener(v -> abrirCalendario());
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ReservaClaseViewModel.class);
    }

    private void setupRecyclerView() {
        adapter = new ReservaClaseAdapter(false, clase ->
                viewModel.reservarClase(idAlumno, clase.idClase)
        );

        recyclerClases.setLayoutManager(new LinearLayoutManager(this));
        recyclerClases.setAdapter(adapter);
    }

    private void observarDatos() {

        viewModel.getClasesUI().observe(this, clases -> {
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

    private void actualizarInterfazVacia(boolean estaVacio) {
        layoutEmpty.setVisibility(estaVacio ? View.VISIBLE : View.GONE);
        recyclerClases.setVisibility(estaVacio ? View.GONE : View.VISIBLE);
    }

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