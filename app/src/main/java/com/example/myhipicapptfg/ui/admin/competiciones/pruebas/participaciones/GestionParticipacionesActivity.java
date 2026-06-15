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

/**
 * Activity encargada de gestionar las participaciones
 * (binomios jinete-caballo) inscritas en una prueba concreta.
 *
 * Funcionalidades principales:
 * - Visualizar la lista de participaciones de una prueba.
 * - Añadir nuevas participaciones mediante un formulario.
 * - Editar participaciones existentes.
 * - Eliminar participaciones con confirmación previa.
 * - Resolver los nombres de alumnos y equinos para mostrarlos
 *   en el adaptador a partir de sus identificadores.
 */
public class GestionParticipacionesActivity extends AppCompatActivity {


    private GestionParticipacionesViewModel viewModel;
    private ParticipacionAdapter adapter;


    /** Identificador de la prueba cuyos inscritos se gestionan. */
    private int idPrueba;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_participaciones);

        recogerExtras();
        setupToolbar();
        configurarActionBar();

        viewModel = new ViewModelProvider(this)
                .get(GestionParticipacionesViewModel.class);

        setupRecyclerView();
        setupFab();
        observarNombresAlumnos();
        observarNombresEquinos();
        observarDatos();
    }


    /**
     * Recoge los extras del Intent.
     *
     * Obtiene el identificador y el nombre de la prueba
     * que se utilizarán durante toda la Activity.
     */
    private void recogerExtras() {
        idPrueba = getIntent().getIntExtra("ID_PRUEBA", -1);
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarParticipaciones);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Configura el título del ActionBar con el nombre de la prueba
     * y habilita el botón de navegación hacia atrás.
     */
    private void configurarActionBar() {

        String nombrePrueba = getIntent().getStringExtra("NOMBRE_PRUEBA");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Inscritos: " + nombrePrueba);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa el RecyclerView y su adaptador con los listeners
     * de edición y eliminación de participaciones.
     */
    private void setupRecyclerView() {

        RecyclerView rv = findViewById(R.id.recyclerParticipaciones);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ParticipacionAdapter(new ParticipacionAdapter.OnClick() {

            @Override
            public void eliminar(Participacion p) {
                confirmarEliminar(p);
            }

            @Override
            public void editar(Participacion p) {

                Intent intent = new Intent(
                        GestionParticipacionesActivity.this,
                        ParticipacionFormActivity.class
                );

                intent.putExtra("ID_PRUEBA", idPrueba);
                intent.putExtra("ID_PARTICIPACION", p.idParticipacion);

                startActivity(intent);
            }
        });

        rv.setAdapter(adapter);
    }

    /**
     * Configura el botón flotante para abrir el formulario
     * de creación de una nueva participación.
     */
    private void setupFab() {

        FloatingActionButton fab = findViewById(R.id.fabAddParticipacion);

        fab.setOnClickListener(v -> {

            Intent intent = new Intent(this, ParticipacionFormActivity.class);
            intent.putExtra("ID_PRUEBA", idPrueba);
            startActivity(intent);
        });
    }


    /**
     * Observa la lista de participaciones de la prueba y
     * actualiza el adaptador cuando cambian los datos.
     *
     * También escucha el estado de las operaciones para
     * mostrar mensajes de confirmación al usuario.
     */
    private void observarDatos() {

        viewModel.getParticipacionesPorPrueba(idPrueba)
                .observe(this, lista -> {

                    if (lista != null) {
                        adapter.actualizar(lista);
                    }
                });

        viewModel.getEstadoOperacion().observe(this, estado -> {

            if (estado == null) return;

            if (estado.equals("EXITO_ELIMINAR")) {
                Toast.makeText(this,
                        "Inscripción eliminada",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Observa los alumnos habilitados para doma y construye un mapa
     * de identificador a nombre completo para su uso en el adaptador.
     */
    private void observarNombresAlumnos() {

        viewModel.obtenerAlumnosDomaConNombre().observe(this, alumnos -> {

            if (alumnos == null) return;

            Map<Integer, String> mapa = new HashMap<>();

            for (Usuario u : alumnos) {
                mapa.put(u.idUsuario, u.nombre + " " + u.apellido1);
            }

            adapter.actualizarNombresAlumnos(mapa);
        });
    }

    /**
     * Observa los equinos disponibles para doma y construye un mapa
     * de identificador a nombre para su uso en el adaptador.
     */
    private void observarNombresEquinos() {

        viewModel.obtenerEquinosDoma().observe(this, equinos -> {

            if (equinos == null) return;

            Map<Integer, String> mapa = new HashMap<>();

            for (Equino e : equinos) {
                mapa.put(e.idEquino, e.nombre);
            }

            adapter.actualizarNombresEquinos(mapa);
        });
    }



    /**
     * Muestra un diálogo de confirmación antes de eliminar
     * una participación de la prueba.
     *
     * Si el usuario confirma, delega la eliminación al ViewModel.
     *
     * @param p Participación que se desea eliminar.
     */
    private void confirmarEliminar(Participacion p) {

        new AlertDialog.Builder(this)
                .setTitle("Anular inscripción")
                .setMessage("¿Deseas eliminar este binomio de la prueba?")
                .setPositiveButton("Eliminar",
                        (d, w) -> viewModel.eliminar(p))
                .setNegativeButton("Cancelar", null)
                .show();
    }


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