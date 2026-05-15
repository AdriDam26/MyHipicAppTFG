package com.example.myhipicapptfg.ui.admin.competiciones;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Competicion;
import com.example.myhipicapptfg.ui.admin.competiciones.pruebas.GestionPruebasActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GestionCompeticionesActivity extends AppCompatActivity {

    private GestionCompeticionesViewModel viewModel;
    private CompeticionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_competiciones);

        initRecyclerView();
        initViewModel();

        FloatingActionButton fab = findViewById(R.id.fabAddCompeticion);
        fab.setOnClickListener(v -> {
            // Abrir formulario para nueva competición
            Intent intent = new Intent(this, CompeticionFormActivity.class);
            startActivity(intent);
        });
    }

    private void initRecyclerView() {
        RecyclerView rv = findViewById(R.id.recyclerCompeticiones);
        rv.setLayoutManager(new LinearLayoutManager(this));

        adapter = new CompeticionAdapter(new CompeticionAdapter.OnClick() {
            @Override
            public void abrir(Competicion c) {
                // Ir a la gestión de pruebas de esta competición
                Intent i = new Intent(GestionCompeticionesActivity.this, GestionPruebasActivity.class);
                i.putExtra("ID_COMPETICION", c.idCompeticion);
                i.putExtra("NOMBRE_COMPETICION", c.nombre);
                startActivity(i);
            }

            @Override
            public void editar(Competicion c) {
                // Abrir formulario en modo edición pasando el ID
                Intent i = new Intent(GestionCompeticionesActivity.this, CompeticionFormActivity.class);
                i.putExtra("ID_COMPETICION", c.idCompeticion);
                startActivity(i);
            }

            @Override
            public void eliminar(Competicion c) {
                mostrarDialogoConfirmacionEliminar(c);
            }
        });

        rv.setAdapter(adapter);
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(GestionCompeticionesViewModel.class);

        // Observar la lista de competiciones
        viewModel.getCompeticiones().observe(this, lista -> {
            if (lista != null) {
                adapter.actualizar(lista);
            }
        });

        // Observar errores o estados (solo para eliminación, ya que el guardado se gestiona en FormActivity)
        viewModel.getEstado().observe(this, estado -> {
            if (estado == null) return;
            if ("EXITO_ELIMINAR".equals(estado)) {
                Toast.makeText(this, "Competición eliminada", Toast.LENGTH_SHORT).show();
            } else if ("ERROR_BD".equals(estado)) {
                Toast.makeText(this, "Error en la base de datos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialogoConfirmacionEliminar(Competicion c) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar competición")
                .setMessage("¿Seguro que quieres eliminar \"" + c.nombre + "\"?\nSe eliminarán todas sus pruebas y movimientos asociados.")
                .setCancelable(false)
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminar(c))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}