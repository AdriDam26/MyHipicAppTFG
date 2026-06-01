package com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.example.myhipicapptfg.datos.local.entidades.Participacion;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class GestionParticipacionesActivity extends AppCompatActivity {

    private GestionParticipacionesViewModel viewModel;
    private ParticipacionAdapter adapter;
    private int idPrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_participaciones);

        MaterialToolbar toolbarParticipaciones = findViewById(R.id.toolbarParticipaciones);
        toolbarParticipaciones.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // 1. Obtener datos del Intent
        idPrueba = getIntent().getIntExtra("ID_PRUEBA", -1);
        String nombrePrueba = getIntent().getStringExtra("NOMBRE_PRUEBA");

        // 2. Configurar Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inscritos: " + nombrePrueba);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // 3. Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(GestionParticipacionesViewModel.class);

        // 4. Configurar RecyclerView y Adapter
        setupRecyclerView();


        viewModel.obtenerAlumnosDomaConNombre().observe(this, alumnos -> {
            if (alumnos == null) return;
            Map<Integer, String> mapa = new HashMap<>();
            for (Usuario u : alumnos) {
                mapa.put(u.idUsuario, u.nombre + " " + u.apellido1);
            }
            adapter.actualizarNombresAlumnos(mapa);
        });

        viewModel.obtenerEquinosDoma().observe(this, equinos -> {
            if (equinos == null) return;
            Map<Integer, String> mapa = new HashMap<>();
            for (Equino e : equinos) {
                mapa.put(e.idEquino, e.nombre);
            }
            adapter.actualizarNombresEquinos(mapa);
        });

        // 5. Observar datos
        observarDatos();

        // 6. Botón añadir
        FloatingActionButton fab = findViewById(R.id.fabAddParticipacion);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, ParticipacionFormActivity.class);
            i.putExtra("ID_PRUEBA", idPrueba);
            startActivity(i);
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.recyclerParticipaciones);
        rv.setLayoutManager(new LinearLayoutManager(this));

        // Implementamos la interfaz del adapter (eliminar y quizás abrir detalle)
        adapter = new ParticipacionAdapter(new ParticipacionAdapter.OnClick() {
            @Override
            public void eliminar(Participacion p) {
                confirmarEliminar(p);
            }

            @Override
            public void editar(Participacion p) {
                // Abrimos el formulario pasando el ID de la prueba y el ID de la participación
                Intent i = new Intent(GestionParticipacionesActivity.this, ParticipacionFormActivity.class);
                i.putExtra("ID_PRUEBA", idPrueba);
                i.putExtra("ID_PARTICIPACION", p.idParticipacion); // Clave para activar el modo edición
                startActivity(i);
            }

        });



        rv.setAdapter(adapter);
    }

    private void observarDatos() {
        // Cargar lista de participaciones
        viewModel.getParticipacionesPorPrueba(idPrueba).observe(this, lista -> {
            if (lista != null) {
                adapter.actualizar(lista);
            }
        });

        // Observar estados de error o éxito
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;
            if (estado.equals("EXITO_ELIMINAR")) {
                Toast.makeText(this, "Inscripción eliminada", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmarEliminar(Participacion p) {
        new AlertDialog.Builder(this)
                .setTitle("Anular inscripción")
                .setMessage("¿Deseas eliminar este binomio de la prueba?")
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminar(p))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish(); // Volver atrás al pulsar la flecha
        return true;
    }
}