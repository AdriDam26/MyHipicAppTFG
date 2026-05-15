package com.example.myhipicapptfg.ui.admin.usuarios;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GestionUsuariosActivity extends AppCompatActivity {

    private GestionUsuariosViewModel viewModel;
    private UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_gestion_usuarios);

        RecyclerView rv = findViewById(R.id.recyclerUsuarios);
        FloatingActionButton fab = findViewById(R.id.fabAddUsuario);

        viewModel = new ViewModelProvider(this)
                .get(GestionUsuariosViewModel.class);

        adapter = new UsuarioAdapter(null, new UsuarioAdapter.OnClick() {
            @Override
            public void editar(Usuario u) {
                Intent i = new Intent(GestionUsuariosActivity.this, UsuarioFormActivity.class);
                i.putExtra("ID_USUARIO", u.idUsuario);
                startActivity(i);
            }

            @Override
            public void eliminar(Usuario u) {

                new androidx.appcompat.app.AlertDialog.Builder(GestionUsuariosActivity.this)
                        .setTitle("Eliminar usuario")
                        .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                        .setCancelable(false)

                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            viewModel.eliminarUsuario(u);
                        })

                        .setNegativeButton("Cancelar", (dialog, which) -> {
                            dialog.dismiss();
                        })

                        .show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

        viewModel.getUsuarios().observe(this, usuarios -> {
            adapter.actualizar(usuarios);
        });

        fab.setOnClickListener(v -> {
            startActivity(new Intent(this, UsuarioFormActivity.class));
        });
    }
}