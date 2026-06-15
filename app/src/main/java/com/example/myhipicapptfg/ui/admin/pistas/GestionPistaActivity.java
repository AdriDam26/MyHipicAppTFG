package com.example.myhipicapptfg.ui.admin.pistas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity encargada de la gestión del catálogo de pistas.
 *
 * Funcionalidades principales:
 * - Visualizar la lista completa de pistas registradas.
 * - Añadir nuevas pistas mediante un formulario.
 * - Editar pistas existentes.
 * - Eliminar pistas con confirmación previa.
 */
public class GestionPistaActivity extends AppCompatActivity {



    private GestionPistaViewModel viewModel;
    private PistaAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pista);

        viewModel = new ViewModelProvider(this)
                .get(GestionPistaViewModel.class);

        setupToolbar();
        setupRecyclerView();
        setupFab();
        observarPistas();
        observarEstado();
    }


    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarPistas);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el RecyclerView y su adaptador con los listeners
     * de edición y eliminación de pistas.
     */
    private void setupRecyclerView() {

        RecyclerView rv = findViewById(R.id.recyclerPistas);

        adapter = new PistaAdapter(null, new PistaAdapter.OnClick() {

            @Override
            public void editar(Pista p) {

                Intent intent = new Intent(
                        GestionPistaActivity.this,
                        PistaFormActivity.class
                );

                intent.putExtra("ID_PISTA", p.idPista);
                startActivity(intent);
            }

            @Override
            public void eliminar(Pista p) {
                confirmarEliminar(p);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    /**
     * Configura el botón flotante para abrir el formulario
     * de creación de una nueva pista.
     */
    private void setupFab() {

        FloatingActionButton fab = findViewById(R.id.fabAddPista);

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, PistaFormActivity.class))
        );
    }


    /**
     * Observa la lista completa de pistas y actualiza
     * el adaptador cuando cambian los datos.
     */
    private void observarPistas() {

        viewModel.obtenerTodas().observe(this, pistas ->
                adapter.actualizar(pistas)
        );
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
                            "Operación realizada correctamente",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_NOMBRE_DUPLICADO":
                    Toast.makeText(this,
                            "Ya existe una pista con ese nombre",
                            Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this,
                            "Error en base de datos",
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        });
    }



    /**
     * Muestra un diálogo de confirmación antes de eliminar una pista.
     *
     * Si el usuario confirma, delega la eliminación al ViewModel.
     *
     * @param p Pista que se desea eliminar.
     */
    private void confirmarEliminar(Pista p) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar pista")
                .setMessage("¿Estás seguro de que quieres eliminar esta pista?")
                .setCancelable(false)
                .setPositiveButton("Eliminar",
                        (dialog, which) -> viewModel.eliminar(p))
                .setNegativeButton("Cancelar",
                        (dialog, which) -> dialog.dismiss())
                .show();
    }
}