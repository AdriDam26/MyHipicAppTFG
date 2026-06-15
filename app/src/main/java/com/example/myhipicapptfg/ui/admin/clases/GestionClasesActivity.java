package com.example.myhipicapptfg.ui.admin.clases;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Clase;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Activity encargada de la gestión de clases.
 *
 * Permite:
 * - Visualizar todas las clases registradas.
 * - Crear nuevas clases.
 * - Editar clases existentes.
 * - Eliminar clases.
 *
 * Además, observa los cambios en clases, profesores y pistas
 * para mantener la interfaz sincronizada con la base de datos.
 */
public class GestionClasesActivity extends AppCompatActivity {

    private GestionClaseViewModel viewModel;
    private ClaseAdapter adapter;

    /**
     * Listas locales utilizadas por el adaptador para
     * resolver los nombres de profesores y pistas.
     */
    private List<Usuario> listaProfesores = new ArrayList<>();
    private List<Pista> listaPistas = new ArrayList<>();
    private List<Clase> listaClases = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_clases);

        initViewModel();
        initViews();
        initToolbar();
        configurarRecyclerView();
        configurarObservadores();
    }

    /**
     * Inicializa el ViewModel asociado a la Activity.
     */
    private void initViewModel() {
        viewModel = new ViewModelProvider(this)
                .get(GestionClaseViewModel.class);
    }

    /**
     * Inicializa las vistas principales de la pantalla.
     */
    private void initViews() {

        FloatingActionButton fab = findViewById(R.id.fabAddClase);

        fab.setOnClickListener(v ->
                startActivity(
                        new Intent(this, ClaseFormActivity.class)
                )
        );
    }

    /**
     * Configura la barra superior de navegación.
     */
    private void initToolbar() {

        MaterialToolbar toolbar =
                findViewById(R.id.toolbarClases);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Configura el RecyclerView y su adaptador.
     */
    private void configurarRecyclerView() {

        RecyclerView rv =
                findViewById(R.id.recyclerClases);

        adapter = new ClaseAdapter(
                listaClases,
                listaProfesores,
                listaPistas,
                new ClaseAdapter.OnClaseClickListener() {

                    @Override
                    public void editar(Clase clase) {

                        Intent intent = new Intent(
                                GestionClasesActivity.this,
                                ClaseFormActivity.class
                        );

                        intent.putExtra(
                                "ID_CLASE",
                                clase.idClase
                        );

                        startActivity(intent);
                    }

                    @Override
                    public void eliminar(Clase clase) {

                        new androidx.appcompat.app.AlertDialog.Builder(
                                GestionClasesActivity.this
                        )
                                .setTitle("Eliminar clase")
                                .setMessage("¿Estás seguro?")
                                .setPositiveButton(
                                        "Eliminar",
                                        (dialog, which) ->
                                                viewModel.eliminar(clase)
                                )
                                .setNegativeButton(
                                        "Cancelar",
                                        null
                                )
                                .show();
                    }
                }
        );

        rv.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rv.setAdapter(adapter);
    }

    /**
     * Configura todos los observadores necesarios para
     * mantener sincronizada la interfaz.
     */
    private void configurarObservadores() {

        observarProfesores();
        observarPistas();
        observarClases();
    }

    /**
     * Observa los usuarios registrados y filtra
     * únicamente aquellos que son profesores.
     */
    private void observarProfesores() {

        viewModel.obtenerTodosLosUsuarios()
                .observe(this, usuarios -> {

                    listaProfesores.clear();

                    for (Usuario usuario : usuarios) {

                        if (Usuario.TIPO_PROFESOR.equals(usuario.tipo)) {
                            listaProfesores.add(usuario);
                        }
                    }

                    actualizarUIAdaptador();
                });
    }

    /**
     * Observa las pistas registradas.
     */
    private void observarPistas() {

        viewModel.obtenerTodasLasPistas()
                .observe(this, pistas -> {

                    listaPistas = pistas;

                    actualizarUIAdaptador();
                });
    }

    /**
     * Observa las clases registradas.
     */
    private void observarClases() {

        viewModel.obtenerTodasLasClases()
                .observe(this, clases -> {

                    listaClases = clases;

                    actualizarUIAdaptador();
                });
    }

    /**
     * Actualiza los datos del adaptador cuando
     * cambia alguna de las listas observadas.
     */
    private void actualizarUIAdaptador() {

        if (adapter != null) {

            adapter.actualizarTodo(
                    listaClases,
                    listaProfesores,
                    listaPistas
            );
        }
    }
}