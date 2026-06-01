package com.example.myhipicapptfg.ui.admin.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
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
        EditText etBuscar = findViewById(R.id.etBuscarUsuario);
        CheckBox cbAlumno = findViewById(R.id.cbAlumno);
        CheckBox cbProfesor = findViewById(R.id.cbProfesor);
        CheckBox cbJuez = findViewById(R.id.cbJuez);
        CheckBox cbPropietario = findViewById(R.id.cbPropietario);

        MaterialToolbar toolbarUsuarios = findViewById(R.id.toolbarUsuarios);

        toolbarUsuarios.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed(); // Vuelve a la pantalla anterior
            }
        });

        viewModel = new ViewModelProvider(this)
                .get(GestionUsuariosViewModel.class);

        Runnable aplicarFiltro = new Runnable() {
            @Override
            public void run() {

                String texto = etBuscar.getText().toString().trim();

                boolean alumno = cbAlumno.isChecked();
                boolean profesor = cbProfesor.isChecked();
                boolean juez = cbJuez.isChecked();
                boolean propietario = cbPropietario.isChecked();

                viewModel.buscarUsuariosFiltrado(
                        texto,
                        alumno,
                        profesor,
                        juez,
                        propietario
                ).observe(GestionUsuariosActivity.this, usuarios -> {
                    adapter.actualizar(usuarios);
                });
            }
        };

        etBuscar.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void afterTextChanged(android.text.Editable s) {
                aplicarFiltro.run();
            }
        });

        cbAlumno.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            android.widget.CompoundButton buttonView,
                            boolean isChecked
                    ) {
                        aplicarFiltro.run();
                    }
                });

        cbProfesor.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            android.widget.CompoundButton buttonView,
                            boolean isChecked
                    ) {
                        aplicarFiltro.run();
                    }
                });

        cbJuez.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            android.widget.CompoundButton buttonView,
                            boolean isChecked
                    ) {
                        aplicarFiltro.run();
                    }
                });

        cbPropietario.setOnCheckedChangeListener(
                new android.widget.CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(
                            android.widget.CompoundButton buttonView,
                            boolean isChecked
                    ) {
                        aplicarFiltro.run();
                    }
                });

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

            @Override
            public void clickItem(Usuario u) {
                android.widget.Toast.makeText(
                        GestionUsuariosActivity.this,
                        "ID Usuario: " + u.idUsuario,
                        android.widget.Toast.LENGTH_SHORT
                ).show();
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