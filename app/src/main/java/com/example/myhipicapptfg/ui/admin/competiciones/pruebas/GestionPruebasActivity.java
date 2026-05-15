package com.example.myhipicapptfg.ui.admin.competiciones.pruebas;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Prueba;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.example.myhipicapptfg.ui.admin.competiciones.pruebas.participaciones.GestionParticipacionesActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class GestionPruebasActivity extends AppCompatActivity {

    private GestionPruebasViewModel viewModel;
    private PruebaAdapter adapter;
    private int idCompeticion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_pruebas);

        idCompeticion = getIntent().getIntExtra("ID_COMPETICION", -1);
        String nombreCompeticion = getIntent().getStringExtra("NOMBRE_COMPETICION");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nombreCompeticion);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv = findViewById(R.id.recyclerPruebas);
        FloatingActionButton fab = findViewById(R.id.fabAddPrueba);

        // ✅ 1. Inicializar viewModel PRIMERO
        viewModel = new ViewModelProvider(this).get(GestionPruebasViewModel.class);

        // ✅ 2. Inicializar adapter ANTES de usarlo
        adapter = new PruebaAdapter(new PruebaAdapter.OnClick() {

            @Override
            public void abrir(Prueba p) {
                Intent i = new Intent(GestionPruebasActivity.this,
                        GestionParticipacionesActivity.class);
                i.putExtra("ID_PRUEBA", p.idPrueba);
                i.putExtra("NOMBRE_PRUEBA", p.nombre);
                startActivity(i);
            }

            @Override
            public void editar(Prueba p) {
                Intent i = new Intent(GestionPruebasActivity.this,
                        PruebaFormActivity.class);
                i.putExtra("ID_PRUEBA", p.idPrueba);
                i.putExtra("ID_COMPETICION", idCompeticion);
                startActivity(i);
            }

            @Override
            public void eliminar(Prueba p) {
                new AlertDialog.Builder(GestionPruebasActivity.this)
                        .setTitle("Eliminar prueba")
                        .setMessage("¿Seguro que quieres eliminar \""
                                + p.nombre + "\"?\nSe eliminarán todos sus movimientos.")
                        .setCancelable(false)
                        .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminarPrueba(p))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        // ✅ 3. Observar datos DESPUÉS de tener viewModel y adapter listos
        viewModel.getPruebasPorCompeticion(idCompeticion)
                .observe(this, lista -> adapter.actualizar(lista));

        viewModel.getJuecesActivos().observe(this, jueces -> {
            if (jueces == null) return;
            Map<Integer, String> mapa = new HashMap<>();
            for (Usuario u : jueces) {
                mapa.put(u.idUsuario, u.nombre + " " + u.apellido1);
            }
            adapter.actualizarJueces(mapa);
        });

        fab.setOnClickListener(v -> {
            Intent i = new Intent(this, PruebaFormActivity.class);
            i.putExtra("ID_COMPETICION", idCompeticion);
            startActivity(i);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}