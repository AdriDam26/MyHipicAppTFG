package com.example.myhipicapptfg.ui.admin.competiciones.pruebas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones.GestionParticipacionesActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

/**
 * Activity encargada de la gestión de pruebas dentro de una competición.
 *
 * Responsabilidades:
 * - Mostrar listado de pruebas
 * - Crear, editar y eliminar pruebas
 * - Navegar a participaciones de cada prueba
 * - Mostrar jueces y conteos asociados
 *
 * Arquitectura: MVVM
 */
public class GestionPruebasActivity extends AppCompatActivity {

    // ViewModel de pruebas
    private GestionPruebasViewModel viewModel;

    // Adapter del RecyclerView
    private PruebaAdapter adapter;

    // ID de la competición actual
    private int idCompeticion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pruebas);

        initToolbar();
        obtenerDatosIntent();

        initActionBar();
        initViewModel();
        initRecyclerView();
        initObservers();
        initFab();
    }

    // =========================================================
    // INIT UI
    // =========================================================

    /**
     * Configura toolbar y botón de navegación
     */
    private void initToolbar() {
        MaterialToolbar toolbarPruebas = findViewById(R.id.toolbarPruebas);
        toolbarPruebas.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Obtiene datos recibidos desde la pantalla anterior
     */
    private void obtenerDatosIntent() {
        idCompeticion = getIntent().getIntExtra("ID_COMPETICION", -1);
    }

    /**
     * Configura título de ActionBar
     */
    private void initActionBar() {
        String nombreCompeticion =
                getIntent().getStringExtra("NOMBRE_COMPETICION");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nombreCompeticion);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Inicializa ViewModel
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(GestionPruebasViewModel.class);
    }

    /**
     * Configura RecyclerView y Adapter
     */
    private void initRecyclerView() {
        RecyclerView rv = findViewById(R.id.recyclerPruebas);

        adapter = new PruebaAdapter(new PruebaAdapter.OnClick() {

            /**
             * Abrir participaciones de una prueba
             */
            @Override
            public void abrir(Prueba p) {
                Intent i = new Intent(
                        GestionPruebasActivity.this,
                        GestionParticipacionesActivity.class
                );

                i.putExtra("ID_PRUEBA", p.idPrueba);
                i.putExtra("NOMBRE_PRUEBA", p.nombre);

                startActivity(i);
            }

            /**
             * Editar prueba existente
             */
            @Override
            public void editar(Prueba p) {
                Intent i = new Intent(
                        GestionPruebasActivity.this,
                        PruebaFormActivity.class
                );

                i.putExtra("ID_PRUEBA", p.idPrueba);
                i.putExtra("ID_COMPETICION", idCompeticion);

                startActivity(i);
            }

            /**
             * Eliminar prueba con confirmación
             */
            @Override
            public void eliminar(Prueba p) {
                mostrarDialogoEliminar(p);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    /**
     * Observa datos del ViewModel (LiveData)
     */
    private void initObservers() {

        // Lista de pruebas
        viewModel.getPruebasPorCompeticion(idCompeticion)
                .observe(this, lista -> adapter.actualizar(lista));

        // Jueces activos
        viewModel.getJuecesActivos().observe(this, jueces -> {
            if (jueces == null) return;

            Map<Integer, String> mapa = new HashMap<>();

            for (Usuario u : jueces) {
                mapa.put(u.idUsuario, u.nombre + " " + u.apellido1);
            }

            adapter.actualizarJueces(mapa);
        });

        // Conteo de participantes por prueba
        viewModel.getConteosParticipantes().observe(this, conteos -> {
            adapter.actualizarConteos(conteos);
        });
    }

    /**
     * FloatingActionButton para crear nuevas pruebas
     */
    private void initFab() {
        FloatingActionButton fab = findViewById(R.id.fabAddPrueba);

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, PruebaFormActivity.class);
            i.putExtra("ID_COMPETICION", idCompeticion);
            startActivity(i);
        });
    }


    /**
     * Muestra diálogo de confirmación para eliminar una prueba
     */
    private void mostrarDialogoEliminar(Prueba p) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar prueba")
                .setMessage("¿Seguro que quieres eliminar \"" +
                        p.nombre + "\"?\nSe eliminarán todos sus movimientos.")
                .setCancelable(false)
                .setPositiveButton("Eliminar",
                        (d, w) -> viewModel.eliminarPrueba(p))
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}