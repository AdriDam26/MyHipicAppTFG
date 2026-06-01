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

public class GestionClasesActivity extends AppCompatActivity {

    private GestionClaseViewModel viewModel;
    private ClaseAdapter adapter;

    // Listas locales para mantener la referencia de nombres
    private List<Usuario> listaProfesores = new ArrayList<>();
    private List<Pista> listaPistas = new ArrayList<>();
    private List<Clase> listaClases = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_clases);

        RecyclerView rv = findViewById(R.id.recyclerClases);
        FloatingActionButton fab = findViewById(R.id.fabAddClase);

        MaterialToolbar toolbarClases = findViewById(R.id.toolbarClases);
        toolbarClases.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        viewModel = new ViewModelProvider(this).get(GestionClaseViewModel.class);

        // CORRECCIÓN: El constructor ahora pide (Clases, Profesores, Pistas, Listener)
        adapter = new ClaseAdapter(listaClases, listaProfesores, listaPistas, new ClaseAdapter.OnClaseClickListener() {
            @Override
            public void editar(Clase clase) {
                Intent i = new Intent(GestionClasesActivity.this, ClaseFormActivity.class);
                i.putExtra("ID_CLASE", clase.idClase);
                startActivity(i);
            }

            @Override
            public void eliminar(Clase clase) {
                new androidx.appcompat.app.AlertDialog.Builder(GestionClasesActivity.this)
                        .setTitle("Eliminar clase")
                        .setMessage("¿Estás seguro?")
                        .setPositiveButton("Eliminar", (dialog, which) -> viewModel.eliminar(clase))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // --- OBSERVADORES ---

        // 1. Observar Profesores
        viewModel.obtenerTodosLosUsuarios().observe(this, usuarios -> {
            listaProfesores.clear();
            for (Usuario u : usuarios) {
                if (Usuario.TIPO_PROFESOR.equals(u.tipo)) listaProfesores.add(u);
            }
            actualizarUIAdaptador();
        });

        // 2. Observar Pistas
        viewModel.obtenerTodasLasPistas().observe(this, pistas -> {
            listaPistas = pistas;
            actualizarUIAdaptador();
        });

        // 3. Observar Clases
        viewModel.obtenerTodasLasClases().observe(this, clases -> {
            listaClases = clases;
            actualizarUIAdaptador();
        });

        fab.setOnClickListener(v -> startActivity(new Intent(this, ClaseFormActivity.class)));
    }

    /**
     * Método auxiliar para refrescar el adaptador con todas las listas actualizadas
     */
    private void actualizarUIAdaptador() {
        if (adapter != null) {
            // Usamos el método 'actualizarTodo' que creamos en el ClaseAdapter
            adapter.actualizarTodo(listaClases, listaProfesores, listaPistas);
        }
    }
}