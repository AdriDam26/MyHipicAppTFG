package com.example.myhipicapptfg.ui.competiciones.juez.pruebas;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.juez.participantes.ParticipantesPruebaActivity;
import com.google.android.material.appbar.MaterialToolbar;

public class PruebasJuezActivity extends AppCompatActivity {

    public static final String EXTRA_ID_JUEZ = "extra_id_juez";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pruebas_juez);

        // Recupera el ID del juez logueado (pasado por Intent)
        int idJuez = getIntent().getIntExtra(EXTRA_ID_JUEZ, -1);

        RecyclerView rv   = findViewById(R.id.rvPruebas);
        TextView     tvEmpty = findViewById(R.id.tvEmptyPruebas);

        // Vincula el nuevo MaterialToolbar usando su ID
        MaterialToolbar toolbar = findViewById(R.id.toolbarPruebas);

// Configura la acción para ir hacia atrás al presionar la flecha
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));

        PruebaJuezAdapter adapter = new PruebaJuezAdapter(prueba -> {
            // Al pulsar una prueba, abre el listado de participantes
            Intent intent = new Intent(this, ParticipantesPruebaActivity.class);
            intent.putExtra(ParticipantesPruebaActivity.EXTRA_ID_PRUEBA,   prueba.idPrueba);
            intent.putExtra(ParticipantesPruebaActivity.EXTRA_NOMBRE_PRUEBA, prueba.nombrePrueba);
            startActivity(intent);
        });

        rv.setAdapter(adapter);

        PruebasJuezViewModel vm = new ViewModelProvider(this)
                .get(PruebasJuezViewModel.class);

        vm.getPruebas(idJuez).observe(this, lista -> {
            if (lista == null || lista.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                adapter.submitList(lista);
            }
        });
    }
}