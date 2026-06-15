package com.example.myhipicapptfg.ui.reservas.profesor;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.google.android.material.appbar.MaterialToolbar;

/**
 * Activity que muestra los alumnos inscritos en una clase concreta.
 */
public class AlumnosDeClaseActivity extends AppCompatActivity {

    // Claves del Intent

    public static final String EXTRA_ID_CLASE   = "extra_id_clase";
    public static final String EXTRA_DISCIPLINA = "extra_disciplina";
    public static final String EXTRA_NIVEL      = "extra_nivel";

    // Componentes de la UI

    private RecyclerView recyclerView;
    private View tvVacio;
    private TextView tvTitulo;
    private TextView tvContador;


    private ProfesorClasesViewModel viewModel;
    private AlumnoClaseAdapter adapter;
    private int idClase;

    //Ciclo de vida

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alumnos_de_clase);

        idClase = getIntent().getIntExtra(EXTRA_ID_CLASE, -1);
        String disciplina = getIntent().getStringExtra(EXTRA_DISCIPLINA);
        String nivel      = getIntent().getStringExtra(EXTRA_NIVEL);

        initViews();
        initToolbar(disciplina, nivel);
        initViewModel();
        observarAlumnos();
    }

    //Inicialización

    private void initViews() {
        recyclerView = findViewById(R.id.rv_alumnos_clase);
        tvVacio      = findViewById(R.id.tv_sin_alumnos);
        tvTitulo     = findViewById(R.id.tv_titulo_alumnos);
        tvContador   = findViewById(R.id.tv_contador_alumnos);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnoClaseAdapter(this);
        recyclerView.setAdapter(adapter);
    }

    private void initToolbar(String disciplina, String nivel) {
        MaterialToolbar toolbar = findViewById(R.id.toolbarAlumnos);
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        if (disciplina != null && nivel != null) {
            tvTitulo.setText(disciplina + " · " + nivel);
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(ProfesorClasesViewModel.class);
    }

    // Observadores

    private void observarAlumnos() {
        viewModel.getAlumnosDeClase(idClase).observe(this, alumnos -> {
            boolean vacio = alumnos == null || alumnos.isEmpty();
            recyclerView.setVisibility(vacio ? View.GONE  : View.VISIBLE);
            tvVacio.setVisibility     (vacio ? View.VISIBLE : View.GONE);

            if (vacio) {
                tvContador.setText("0 alumnos inscritos");
            } else {
                adapter.setAlumnos(alumnos);
                int n = alumnos.size();
                tvContador.setText(n + " alumno" + (n == 1 ? "" : "s") +
                        " inscrito" + (n == 1 ? "" : "s"));
            }
        });
    }
}