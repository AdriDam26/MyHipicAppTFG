package com.example.myhipicapptfg.ui.gps.seguir;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ListaRutasActivity extends AppCompatActivity implements RutasAdapter.OnRutaClickListener {

    private ListaRutasViewModel viewModel;
    private RecyclerView recyclerView;
    private LinearLayout layoutSinRutas;
    private RutasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutas);

        // Toolbar global hípica
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        // Inicializar vistas de Material 3
        recyclerView = findViewById(R.id.recyclerViewRutas);
        layoutSinRutas = findViewById(R.id.layoutSinRutas);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RutasAdapter(this);
        recyclerView.setAdapter(adapter);

        // ViewModel y Observador
        int idPropietario = getIntent().getIntExtra("ID_PROPIETARIO", 12);
        viewModel = new ViewModelProvider(this).get(ListaRutasViewModel.class);

        viewModel.obtenerRutas(idPropietario).observe(this, lista -> {
            if (lista == null || lista.isEmpty()) {
                layoutSinRutas.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                layoutSinRutas.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setRutas(lista);
            }
        });
    }

    // Intercepción del click desde el Adapter
    @Override
    public void onRutaClick(RutaPersonal ruta) {
        mostrarOpciones(ruta);
    }

    private void mostrarOpciones(RutaPersonal ruta) {
        // Le pasamos el estilo personalizado de rutas al constructor
        new MaterialAlertDialogBuilder(this, R.style.Theme_MyHipicApp_Dialog_Rutas)
                .setTitle(ruta.nombre)
                .setMessage("Fecha: " + ruta.fecha + "\nDistancia: " +
                        String.format("%.2f km", ruta.distanciaRecorrida) +
                        "\nDuración: " + ruta.duracion)
                .setPositiveButton("Seguir ruta", (d, w) -> {
                    Intent intent = new Intent(this, SeguirRutaActivity.class);
                    intent.putExtra("RUTA_ID", ruta.idRutaPersonal);
                    startActivity(intent);
                })
                .setNegativeButton("Eliminar", (d, w) -> confirmarEliminar(ruta))
                .setNeutralButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(RutaPersonal ruta) {
        // Para la confirmación de borrar, usamos el estilo con acento de advertencia/burdeos
        new MaterialAlertDialogBuilder(this, R.style.Theme_MyHipicApp_Dialog_Rutas_Eliminar)
                .setTitle("Eliminar ruta")
                .setMessage("¿Seguro que quieres eliminar \"" + ruta.nombre + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminar(ruta))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}