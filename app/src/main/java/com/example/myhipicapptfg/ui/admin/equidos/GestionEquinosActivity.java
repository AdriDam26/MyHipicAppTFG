package com.example.myhipicapptfg.ui.admin.equidos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Equino;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity encargada de la gestión del catálogo de equinos.
 *
 * Funcionalidades principales:
 * - Visualizar la lista completa de equinos registrados.
 * - Añadir nuevos equinos mediante un formulario.
 * - Editar equinos existentes.
 * - Eliminar equinos con confirmación previa.
 */
public class GestionEquinosActivity extends AppCompatActivity {


    private GestionEquinoViewModel viewModel;
    private EquinoAdapter adapter;

    // =========================================================
    // CICLO DE VIDA
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_equinos);

        viewModel = new ViewModelProvider(this)
                .get(GestionEquinoViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupFab();
        observarEquinos();
        observarEstado();
    }

    // =========================================================
    // INIT
    // =========================================================

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarEquinos);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el RecyclerView y su adaptador con los listeners
     * de edición y eliminación de equinos.
     */
    private void setupRecyclerView() {

        RecyclerView rv = findViewById(R.id.recyclerEquinos);

        adapter = new EquinoAdapter(null, new EquinoAdapter.OnClick() {

            @Override
            public void editar(Equino e) {

                Intent intent = new Intent(
                        GestionEquinosActivity.this,
                        EquinoFormActivity.class
                );

                intent.putExtra("ID_EQUINO", e.idEquino);
                startActivity(intent);
            }

            @Override
            public void eliminar(Equino e) {
                mostrarDialogoEliminar(e);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    /**
     * Configura el botón flotante para abrir el formulario
     * de creación de un nuevo equino.
     */
    private void setupFab() {

        FloatingActionButton fab = findViewById(R.id.fabAddEquino);

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, EquinoFormActivity.class))
        );
    }


    /**
     * Observa la lista completa de equinos y actualiza
     * el adaptador cuando cambian los datos.
     */
    private void observarEquinos() {

        viewModel.obtenerTodosEquinos().observe(this, equinos -> {

            if (equinos != null) {
                adapter.actualizar(equinos);
            }
        });
    }

    /**
     * Observa el estado de las operaciones de modificación
     * y muestra un mensaje informativo al usuario según el resultado.
     */
    private void observarEstado() {

        viewModel.getEstadoOperacion().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this,
                            "Operación realizada con éxito",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error al acceder a la base de datos",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }



    /**
     * Muestra un diálogo de confirmación antes de eliminar un equino.
     *
     * Si el usuario confirma, delega la eliminación al ViewModel.
     *
     * @param e Equino que se desea eliminar.
     */
    private void mostrarDialogoEliminar(Equino e) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar equino")
                .setMessage("¿Estás seguro de que quieres eliminar a "
                        + e.nombre + "?")
                .setCancelable(false)
                .setPositiveButton("Eliminar",
                        (dialog, which) -> viewModel.eliminar(e))
                .setNegativeButton("Cancelar",
                        (dialog, which) -> dialog.dismiss())
                .show();
    }
}