package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.adapter.ClaseAdapter;
import com.example.myhipicapptfg.entities.Clase;
import com.example.myhipicapptfg.viewmodel.GestionClaseViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GestionClasesActivity extends AppCompatActivity {

    private GestionClaseViewModel viewModel;
    private ClaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_clases);

        // Referencias a la UI (Asegúrate de que los IDs coincidan en tu XML)
        RecyclerView rv = findViewById(R.id.recyclerClases);
        FloatingActionButton fab = findViewById(R.id.fabAddClase);

        // Inicializar ViewModel
        viewModel = new ViewModelProvider(this).get(GestionClaseViewModel.class);

        // Configurar el Adapter
        adapter = new ClaseAdapter(null, new ClaseAdapter.OnClaseClickListener() {
            @Override
            public void editar(Clase clase) {
                // Navegar al formulario en modo edición
                Intent i = new Intent(GestionClasesActivity.this, ClaseFormActivity.class);
                i.putExtra("ID_CLASE", clase.idClase);
                startActivity(i);
            }

            @Override
            public void eliminar(Clase clase) {
                // Diálogo de confirmación para eliminar
                new androidx.appcompat.app.AlertDialog.Builder(GestionClasesActivity.this)
                        .setTitle("Eliminar clase")
                        .setMessage("¿Estás seguro de que quieres eliminar esta clase programada?")
                        .setCancelable(false)
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            viewModel.eliminar(clase);
                        })
                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            dialog.dismiss();
                        })
                        .show();
            }
        });

        // Configurar RecyclerView
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // Observar cambios en la lista de clases
        viewModel.obtenerTodasLasClases().observe(this, clases -> {
            if (clases != null) {
                adapter.actualizar(clases);
            }
        });

        // Acción del FAB para añadir nueva clase
        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, ClaseFormActivity.class));
        });
    }
}