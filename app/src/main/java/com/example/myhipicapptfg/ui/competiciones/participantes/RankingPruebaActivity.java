package com.example.myhipicapptfg.ui.competiciones.participantes;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.google.android.material.button.MaterialButton;

public class RankingPruebaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PRUEBA        = "extra_id_prueba";
    public static final String EXTRA_ID_PARTICIPACION = "extra_id_participacion";
    public static final String EXTRA_NOMBRE_PRUEBA    = "extra_nombre_prueba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking_prueba);

        int    idPrueba        = getIntent().getIntExtra(EXTRA_ID_PRUEBA, -1);
        int    idParticipacion = getIntent().getIntExtra(EXTRA_ID_PARTICIPACION, -1);
        String nombrePrueba    = getIntent().getStringExtra(EXTRA_NOMBRE_PRUEBA);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(nombrePrueba);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv = findViewById(R.id.rvRanking);
        rv.setLayoutManager(new LinearLayoutManager(this));
        RankingAdapter adapter = new RankingAdapter();
        rv.setAdapter(adapter);

        AlumnoResultadosViewModel vm = new ViewModelProvider(this)
                .get(AlumnoResultadosViewModel.class);

        vm.getRanking(idPrueba).observe(this, adapter::submitList);

        MaterialButton btnHoja = findViewById(R.id.btnVerHoja);
        btnHoja.setOnClickListener(v -> {
            Intent i = new Intent(this, HojaCalificacionesActivity.class);
            i.putExtra(HojaCalificacionesActivity.EXTRA_ID_PARTICIPACION, idParticipacion);
            i.putExtra(HojaCalificacionesActivity.EXTRA_NOMBRE_PRUEBA,    nombrePrueba);
            startActivity(i);
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}