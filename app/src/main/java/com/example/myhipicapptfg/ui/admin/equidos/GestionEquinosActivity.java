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

public class GestionEquinosActivity extends AppCompatActivity {

    private GestionEquinoViewModel viewModel;
    private EquinoAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_gestion_equinos);

        // 1. Inicializar Vistas
        RecyclerView rv = findViewById(R.id.recyclerEquinos);
        FloatingActionButton fab = findViewById(R.id.fabAddEquino);

        MaterialToolbar toolbar = findViewById(R.id.toolbarEquinos);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed(); // O finish();
            }
        });

        // 2. Inicializar ViewModel
        viewModel = new ViewModelProvider(this)
                .get(GestionEquinoViewModel.class);

        // 3. Configurar Adaptador con interfaces de click
        adapter = new EquinoAdapter(null, new EquinoAdapter.OnClick() {
            @Override
            public void editar(Equino e) {
                Intent i = new Intent(GestionEquinosActivity.this, EquinoFormActivity.class);
                i.putExtra("ID_EQUINO", e.idEquino);
                startActivity(i);
            }

            @Override
            public void eliminar(Equino e) {
                mostrarDialogoEliminar(e);
            }
        });

        // 4. Configurar RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // 5. Observar la lista de equinos
        viewModel.obtenerTodosEquinos().observe(this, equinos -> {
            if (equinos != null) {
                adapter.actualizar(equinos);
            }
        });

        // 6. Observar el estado de las operaciones (Eliminar/Actualizar)
        viewModel.getEstadoOperacion().observe(this, estado -> {
            if (estado == null) return;

            if (estado.equals("EXITO")) {
                Toast.makeText(this, "Operación realizada con éxito", Toast.LENGTH_SHORT).show();
            } else if (estado.equals("ERROR_BD")) {
                Toast.makeText(this, "Error al acceder a la base de datos", Toast.LENGTH_SHORT).show();
            }
        });

        // 7. Botón flotante para añadir nuevo
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, EquinoFormActivity.class));
        });
    }

    private void mostrarDialogoEliminar(Equino e) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar equino")
                .setMessage("¿Estás seguro de que quieres eliminar a " + e.nombre + "?")
                .setCancelable(false)
                .setPositiveButton("Eliminar", (dialog, which) -> {
                    viewModel.eliminar(e);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }
}