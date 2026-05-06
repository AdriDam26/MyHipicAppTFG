package com.example.myhipicapptfg.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.adapter.AlumnoClaseAdapter;
import com.example.myhipicapptfg.viewmodel.ProfesorClasesViewModel;

public class AlumnosDeClaseActivity extends AppCompatActivity {

    public static final String EXTRA_ID_CLASE    = "extra_id_clase";
    public static final String EXTRA_DISCIPLINA  = "extra_disciplina";
    public static final String EXTRA_NIVEL       = "extra_nivel";

    private ProfesorClasesViewModel viewModel;
    private AlumnoClaseAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_de_clase);

        // ── Extras ────────────────────────────────────────────────────────────
        int idClase      = getIntent().getIntExtra(EXTRA_ID_CLASE, -1);
        String disciplina = getIntent().getStringExtra(EXTRA_DISCIPLINA);
        String nivel      = getIntent().getStringExtra(EXTRA_NIVEL);

        if (idClase == -1) {
            finish();
            return;
        }

        // ── Cabecera ──────────────────────────────────────────────────────────
        TextView tvTitulo = findViewById(R.id.tv_titulo_alumnos);
        if (disciplina != null && nivel != null) {
            tvTitulo.setText(disciplina + " · " + nivel);
        }

        // ── RecyclerView ──────────────────────────────────────────────────────
        RecyclerView recyclerView = findViewById(R.id.rv_alumnos_clase);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new AlumnoClaseAdapter(this);
        recyclerView.setAdapter(adapter);

        // ── ViewModel ─────────────────────────────────────────────────────────
        viewModel = new ViewModelProvider(this).get(ProfesorClasesViewModel.class);

        viewModel.getAlumnosDeClase(idClase).observe(this, alumnos -> {
            View tvVacio = findViewById(R.id.tv_sin_alumnos);
            TextView tvContador = findViewById(R.id.tv_contador_alumnos);

            if (alumnos == null || alumnos.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
                tvContador.setText("0 alumnos inscritos");
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvVacio.setVisibility(View.GONE);
                tvContador.setText(alumnos.size() + " alumno" + (alumnos.size() == 1 ? "" : "s") + " inscrito" + (alumnos.size() == 1 ? "" : "s"));
                adapter.setAlumnos(alumnos);
            }
        });

        // ── Botón volver ──────────────────────────────────────────────────────
        findViewById(R.id.btn_volver_alumnos).setOnClickListener(v -> finish());
    }
}