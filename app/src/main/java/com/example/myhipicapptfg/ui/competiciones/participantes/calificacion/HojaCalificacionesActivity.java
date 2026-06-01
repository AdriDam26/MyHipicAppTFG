package com.example.myhipicapptfg.ui.competiciones.participantes.calificacion;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.participantes.AlumnoResultadosViewModel;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class HojaCalificacionesActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PARTICIPACION = "extra_id_participacion";
    public static final String EXTRA_NOMBRE_PRUEBA    = "extra_nombre_prueba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hoja_calificaciones);

        int    idParticipacion = getIntent().getIntExtra(EXTRA_ID_PARTICIPACION, -1);



        MaterialToolbar toolbar = findViewById(R.id.toolbar);


        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        TextView     tvResumen = findViewById(R.id.tvResumen);
        RecyclerView rv        = findViewById(R.id.rvHoja);

        TextView tvCorreccion = findViewById(R.id.tvCorreccion);

        rv.setLayoutManager(new LinearLayoutManager(this));
        HojaCalifAdapter adapter = new HojaCalifAdapter();
        rv.setAdapter(adapter);

        AlumnoResultadosViewModel vm = new ViewModelProvider(this)
                .get(AlumnoResultadosViewModel.class);

        // RecyclerView — solo carga la lista
        vm.getHoja(idParticipacion).observe(this, lista -> {
            if (lista == null || lista.isEmpty()) return;
            adapter.submitList(lista);
        });

        // Resumen — lee notaFinal y porcentaje ya calculados en Participacion
        vm.getParticipacion(idParticipacion).observe(this, p -> {
            if (p == null) return;
            if (p.eliminado) {
                tvResumen.setText("ELIMINADO");
                tvResumen.setBackgroundColor(getColor(android.R.color.holo_red_dark));
            } else {
                tvResumen.setText(String.format(Locale.getDefault(),
                        "Nota: %.2f  |  Porcentaje: %.3f%%",
                        p.notaFinal, p.porcentaje));
            }

            String etiqueta;
            if      (p.correccion == 0.0) etiqueta = "Sin corrección";
            else if (p.correccion == 2.0) etiqueta = "1 corrección";
            else if (p.correccion == 4.0) etiqueta = "2 correcciones";
            else                          etiqueta = "3 correcciones";   // -1 → tu caso especial

            tvCorreccion.setText(String.format(Locale.getDefault(),
                    "Correcciones: %s", etiqueta));
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}