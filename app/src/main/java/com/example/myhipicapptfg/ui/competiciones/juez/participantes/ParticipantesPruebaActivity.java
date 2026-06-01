package com.example.myhipicapptfg.ui.competiciones.juez.participantes;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.competiciones.juez.calificacion.PuntuacionActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

public class ParticipantesPruebaActivity extends AppCompatActivity {

    public static final String EXTRA_ID_PRUEBA     = "extra_id_prueba";
    public static final String EXTRA_NOMBRE_PRUEBA = "extra_nombre_prueba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participantes_prueba);

        int    idPrueba     = getIntent().getIntExtra(EXTRA_ID_PRUEBA, -1);
        String nombrePrueba = getIntent().getStringExtra(EXTRA_NOMBRE_PRUEBA);

        // Configuración de la nueva MaterialToolbar y acción de volver atrás
        MaterialToolbar toolbar = findViewById(R.id.toolbarParticipantes);
        if (nombrePrueba != null) {
            toolbar.setTitle(nombrePrueba);
        }
        toolbar.setNavigationOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        TextView     tvEmpty   = findViewById(R.id.tvEmptyParticipantes);
        RecyclerView rv        = findViewById(R.id.rvParticipantes);
        MaterialButton btnPublicar    = findViewById(R.id.btnPublicarResultados);

        rv.setLayoutManager(new LinearLayoutManager(this));

        ParticipanteAdapter adapter = new ParticipanteAdapter();
        rv.setAdapter(adapter);

        ParticipantesViewModel vm = new ViewModelProvider(this)
                .get(ParticipantesViewModel.class);

        vm.getParticipantes(idPrueba).observe(this, lista -> {
            if (lista == null || lista.isEmpty()) {
                tvEmpty.setVisibility(View.VISIBLE);
                rv.setVisibility(View.GONE);
            } else {
                tvEmpty.setVisibility(View.GONE);
                rv.setVisibility(View.VISIBLE);
                adapter.submitList(lista);
            }
        });

        vm.isPublicado().observe(this, publicado -> {
            if (publicado != null && publicado) {
                btnPublicar.setText("🔒 Ocultar resultados");
                btnPublicar.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#B71C1C")));
            } else {
                btnPublicar.setText("📢 Publicar resultados");
                btnPublicar.setBackgroundTintList(
                        ColorStateList.valueOf(Color.parseColor("#8B1A2E"))); // ← burdeos
            }
        });

        // Click publicar/ocultar con confirmación
        btnPublicar.setOnClickListener(v -> {
            Boolean actual = vm.isPublicado().getValue();
            boolean nuevoEstado = (actual == null) ? true : !actual;
            String mensaje = nuevoEstado
                    ? "¿Publicar los resultados? Los alumnos podrán verlos."
                    : "¿Ocultar los resultados? Los alumnos dejarán de verlos.";

            new AlertDialog.Builder(this)
                    .setTitle("Resultados")
                    .setMessage(mensaje)
                    .setPositiveButton("Confirmar", (d, w) ->
                            vm.actualizarPublicado(nuevoEstado))
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        adapter.setOnParticipanteClickListener(item -> {
            Intent intent = new Intent(this, PuntuacionActivity.class);
            intent.putExtra(PuntuacionActivity.EXTRA_ID_PARTICIPACION, item.idParticipacion);
            intent.putExtra(PuntuacionActivity.EXTRA_ID_PRUEBA, idPrueba);
            intent.putExtra(PuntuacionActivity.EXTRA_NOMBRE_JINETE,    item.nombreJinete);
            intent.putExtra(PuntuacionActivity.EXTRA_NOMBRE_CABALLO,   item.nombreCaballo);
            intent.putExtra(PuntuacionActivity.EXTRA_CORRECCION_PREVIA, item.correccion);
            startActivity(intent);
        });
    }
}