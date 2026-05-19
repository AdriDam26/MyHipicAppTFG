package com.example.myhipicapptfg.ui.reservas.profesor;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;

public class MisClasesProfesorActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PROFESOR = "extra_id_profesor";

    private ProfesorClasesViewModel viewModel;
    private ClaseProfesorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_clase_profesor);

        // ── Extras ────────────────────────────────────────────────────────────
        int idProfesor = getIntent().getIntExtra(EXTRA_ID_PROFESOR, -1);
        android.util.Log.d("Prueba", "ID Profesor recibido: " + idProfesor);
        if (idProfesor == -1) {
            finish();
            return;
        }

        // El título ahora es estático desde el XML, no necesitamos tvTitulo.setText() aquí.

        // ── RecyclerView ──────────────────────────────────────────────────────
        RecyclerView recyclerView = findViewById(R.id.rv_mis_clases);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ClaseProfesorAdapter(this, clase -> {
            // Al pulsar una clase → abrir pantalla de alumnos
            Intent intent = new Intent(this, AlumnosDeClaseActivity.class);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_ID_CLASE, clase.idClase);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_DISCIPLINA, clase.disciplina);
            intent.putExtra(AlumnosDeClaseActivity.EXTRA_NIVEL, clase.nivel);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);

        // ── ViewModel ─────────────────────────────────────────────────────────
        viewModel = new ViewModelProvider(this).get(ProfesorClasesViewModel.class);

        viewModel.getClasesDelProfesor(idProfesor).observe(this, clases -> {
            View tvVacio = findViewById(R.id.tv_sin_clases);
            if (clases == null || clases.isEmpty()) {
                recyclerView.setVisibility(View.GONE);
                tvVacio.setVisibility(View.VISIBLE);
            } else {
                recyclerView.setVisibility(View.VISIBLE);
                tvVacio.setVisibility(View.GONE);
                adapter.setClases(clases);
            }
        });

        // ── Botón volver ──────────────────────────────────────────────────────
        findViewById(R.id.btn_volver_mis_clases).setOnClickListener(v -> finish());
    }
}