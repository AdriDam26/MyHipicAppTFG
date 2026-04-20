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
import com.example.myhipicapptfg.adapters.AlumnoAdapter;
import com.example.myhipicapptfg.entities.Usuario;
import com.example.myhipicapptfg.viewmodel.GestionUsuariosViewModel;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class GestionAlumnosActivity extends AppCompatActivity {

    private GestionUsuariosViewModel viewModel;
    private AlumnoAdapter adapter;
    private TextView tvEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_alumnos);

        tvEmpty = findViewById(R.id.tvEmpty);
        viewModel = new ViewModelProvider(this).get(GestionUsuariosViewModel.class);

        setupRecyclerView();

        // Botón añadir: Forzamos que el formulario se abra como "Alumno"
        FloatingActionButton fab = findViewById(R.id.fabAddAlumno);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AltaUsuarioActivity.class);
            intent.putExtra("TIPO_USUARIO", "Alumno");
            startActivity(intent);
        });

        viewModel.obtenerUsuariosPorTipo("Alumno").observe(this, lista -> {
            adapter.setAlumnos(lista);
            tvEmpty.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }

    private void setupRecyclerView() {
        RecyclerView rv = findViewById(R.id.rvAlumnos);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AlumnoAdapter(new AlumnoAdapter.OnAlumnoClickListener() {
            @Override
            public void onEditar(Usuario usuario) {
                Intent intent = new Intent(GestionAlumnosActivity.this, AltaUsuarioActivity.class);
                intent.putExtra("USUARIO_ID", usuario.idUsuario);
                intent.putExtra("TIPO_USUARIO", "Alumno");
                startActivity(intent);
            }

            @Override
            public void onEliminar(Usuario usuario) {
                confirmarEliminar(usuario);
            }
        });
        rv.setAdapter(adapter);
    }

    private void confirmarEliminar(Usuario u) {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Eliminar Alumno")
                .setMessage("¿Estás seguro de eliminar a " + u.nombre + "?")
                .setPositiveButton("Eliminar", (d, w) -> viewModel.eliminarUsuario(u))
                .setNegativeButton("Cancelar", null)
                .show();
    }
}