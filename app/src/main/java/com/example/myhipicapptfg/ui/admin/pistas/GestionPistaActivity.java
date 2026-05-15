package com.example.myhipicapptfg.ui.admin.pistas;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Pista;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GestionPistaActivity extends AppCompatActivity {

    private GestionPistaViewModel viewModel;
    private PistaAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_gestion_pista);

        RecyclerView rv = findViewById(R.id.recyclerPistas);
        FloatingActionButton fab = findViewById(R.id.fabAddPista);

        viewModel = new ViewModelProvider(this)
                .get(GestionPistaViewModel.class);

        adapter = new PistaAdapter(null, new PistaAdapter.OnClick() {
            @Override
            public void editar(Pista p) {
                Intent i = new Intent(GestionPistaActivity.this, PistaFormActivity.class);
                i.putExtra("ID_PISTA", p.idPista);
                startActivity(i);
            }

            @Override
            public void eliminar(Pista p) {

                new androidx.appcompat.app.AlertDialog.Builder(GestionPistaActivity.this)
                        .setTitle("Eliminar pista")
                        .setMessage("¿Estás seguro de que quieres eliminar esta pista?")
                        .setCancelable(false)

                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            viewModel.eliminar(p);
                        })

                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            dialog.dismiss();
                        })

                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        viewModel.obtenerTodas().observe(this, pistas -> {
            adapter.actualizar(pistas);
        });

        viewModel.getEstadoOperacion().observe(this, estado -> {

            if (estado == null) return;

            switch (estado) {

                case "EXITO":
                    Toast.makeText(this, "Operación realizada correctamente", Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_NOMBRE_DUPLICADO":
                    Toast.makeText(this, "Ya existe una pista con ese nombre", Toast.LENGTH_SHORT).show();
                    break;

                case "ERROR_BD":
                    Toast.makeText(this, "Error en base de datos", Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, PistaFormActivity.class));
        });
    }
}