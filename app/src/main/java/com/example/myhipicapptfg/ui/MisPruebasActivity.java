package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.adapter.PruebaAlumnoAdapter;
import com.example.myhipicapptfg.viewmodel.AlumnoResultadosViewModel;

public class MisPruebasActivity extends AppCompatActivity {

    public static final String EXTRA_ID_ALUMNO = "extra_id_alumno";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_pruebas);

        int idAlumno = getIntent().getIntExtra(EXTRA_ID_ALUMNO, -1);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Mis pruebas");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        RecyclerView rv      = findViewById(R.id.rvMisPruebas);
        TextView     tvEmpty = findViewById(R.id.tvEmpty);

        rv.setLayoutManager(new LinearLayoutManager(this));
        PruebaAlumnoAdapter adapter = new PruebaAlumnoAdapter();
        rv.setAdapter(adapter);

        AlumnoResultadosViewModel vm = new ViewModelProvider(this)
                .get(AlumnoResultadosViewModel.class);

        vm.getPruebas(idAlumno).observe(this, lista -> {
            if (lista == null || lista.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                adapter.submitList(lista);
            }
        });

        adapter.setOnClick(item -> {
            Intent i = new Intent(this, RankingPruebaActivity.class);
            i.putExtra(RankingPruebaActivity.EXTRA_ID_PRUEBA,        item.idPrueba);
            i.putExtra(RankingPruebaActivity.EXTRA_ID_PARTICIPACION, item.idParticipacion);
            i.putExtra(RankingPruebaActivity.EXTRA_NOMBRE_PRUEBA,    item.nombrePrueba);
            startActivity(i);
        });
    }

    @Override public boolean onSupportNavigateUp() { finish(); return true; }
}