package com.example.myhipicapptfg.ui.gps.seguir;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.RutaPersonal;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;

public class ListaRutasActivity extends AppCompatActivity {

    private ListaRutasViewModel viewModel;
    private ListView listView;
    private TextView tvSinRutas;
    private List<RutaPersonal> rutas = new ArrayList<>();
    private RutasAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_rutas);

        // Toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> finish());

        listView   = findViewById(R.id.listViewRutas);
        tvSinRutas = findViewById(R.id.tvSinRutas);

        adapter = new RutasAdapter();
        listView.setAdapter(adapter);

        // ViewModel
        int idPropietario = getIntent().getIntExtra("ID_PROPIETARIO", 1);
        viewModel = new ViewModelProvider(this).get(ListaRutasViewModel.class);
        viewModel.obtenerRutas(idPropietario).observe(this, lista -> {
            rutas.clear();
            if (lista == null || lista.isEmpty()) {
                tvSinRutas.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            } else {
                tvSinRutas.setVisibility(View.GONE);
                listView.setVisibility(View.VISIBLE);
                rutas.addAll(lista);
                adapter.notifyDataSetChanged();
            }
        });

        // Click en una ruta
        listView.setOnItemClickListener((parent, view, position, id) ->
                mostrarOpciones(rutas.get(position)));
    }

    private void mostrarOpciones(RutaPersonal ruta) {
        new MaterialAlertDialogBuilder(this)
                .setTitle(ruta.nombre)
                .setMessage("Fecha: " + ruta.fecha + "\nDistancia: " +
                        String.format("%.2f km", ruta.distanciaRecorrida) +
                        "\nDuración: " + ruta.duracion)
                .setPositiveButton("Seguir ruta", (d, w) -> {
                    Intent intent = new Intent(this, SeguirRutaActivity.class);
                    intent.putExtra("RUTA_ID", ruta.idRutaPersonal);
                    startActivity(intent);
                })
                .setNegativeButton("Eliminar", (d, w) ->
                        confirmarEliminar(ruta))
                .setNeutralButton("Cancelar", null)
                .show();
    }

    private void confirmarEliminar(RutaPersonal ruta) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar ruta")
                .setMessage("¿Seguro que quieres eliminar \"" + ruta.nombre + "\"?")
                .setPositiveButton("Eliminar", (d, w) -> {
                    viewModel.eliminar(ruta);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Adapter personalizado para el ListView
    private class RutasAdapter extends ArrayAdapter<RutaPersonal> {

        public RutasAdapter() {
            super(ListaRutasActivity.this, 0, rutas);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.item_ruta, parent, false);
            }

            RutaPersonal ruta = rutas.get(position);

            TextView tvNombre    = convertView.findViewById(R.id.tvNombreRuta);
            TextView tvFecha     = convertView.findViewById(R.id.tvFechaRuta);
            TextView tvDistancia = convertView.findViewById(R.id.tvDistanciaRuta);
            TextView tvDuracion  = convertView.findViewById(R.id.tvDuracionRuta);

            tvNombre.setText(ruta.nombre);
            tvFecha.setText(ruta.fecha);
            tvDistancia.setText(String.format("%.2f km", ruta.distanciaRecorrida));
            tvDuracion.setText(ruta.duracion);

            return convertView;
        }
    }
}