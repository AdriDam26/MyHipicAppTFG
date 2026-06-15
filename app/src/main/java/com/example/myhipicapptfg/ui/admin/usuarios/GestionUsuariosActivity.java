package com.example.myhipicapptfg.ui.admin.usuarios;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.datos.local.entidades.Usuario;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity encargada de la gestión del catálogo de usuarios.
 *
 * Funcionalidades principales:
 * - Visualizar la lista completa de usuarios registrados.
 * - Filtrar usuarios por texto libre y por tipo
 *   (Alumno, Profesor, Juez, Propietario).
 * - Añadir nuevos usuarios mediante un formulario.
 * - Editar usuarios existentes.
 * - Eliminar usuarios con confirmación previa.
 */
public class GestionUsuariosActivity extends AppCompatActivity {


    private GestionUsuariosViewModel viewModel;
    private UsuarioAdapter adapter;


    private EditText etBuscar;
    private CheckBox cbAlumno;
    private CheckBox cbProfesor;
    private CheckBox cbJuez;
    private CheckBox cbPropietario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_usuarios);

        viewModel = new ViewModelProvider(this)
                .get(GestionUsuariosViewModel.class);

        initViews();
        setupToolbar();
        setupRecyclerView();
        setupFiltros();
        setupFab();
        observarUsuarios();
    }

    /**
     * Inicializa las referencias a las vistas de filtrado.
     */
    private void initViews() {
        etBuscar      = findViewById(R.id.etBuscarUsuario);
        cbAlumno      = findViewById(R.id.cbAlumno);
        cbProfesor    = findViewById(R.id.cbProfesor);
        cbJuez        = findViewById(R.id.cbJuez);
        cbPropietario = findViewById(R.id.cbPropietario);
    }

    /**
     * Configura el MaterialToolbar y su acción de navegación hacia atrás.
     */
    private void setupToolbar() {

        MaterialToolbar toolbar = findViewById(R.id.toolbarUsuarios);

        toolbar.setNavigationOnClickListener(
                v -> getOnBackPressedDispatcher().onBackPressed()
        );
    }

    /**
     * Inicializa el RecyclerView y su adaptador con los listeners
     * de edición, eliminación y pulsación sobre un usuario.
     */
    private void setupRecyclerView() {

        RecyclerView rv = findViewById(R.id.recyclerUsuarios);

        adapter = new UsuarioAdapter(null, new UsuarioAdapter.OnClick() {

            @Override
            public void editar(Usuario u) {

                Intent intent = new Intent(
                        GestionUsuariosActivity.this,
                        UsuarioFormActivity.class
                );

                intent.putExtra("ID_USUARIO", u.idUsuario);
                startActivity(intent);
            }

            @Override
            public void eliminar(Usuario u) {
                confirmarEliminar(u);
            }

            @Override
            public void clickItem(Usuario u) {
                Toast.makeText(
                        GestionUsuariosActivity.this,
                        "ID Usuario: " + u.idUsuario,
                        Toast.LENGTH_SHORT
                ).show();
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    /**
     * Configura el botón flotante para abrir el formulario
     * de creación de un nuevo usuario.
     */
    private void setupFab() {

        FloatingActionButton fab = findViewById(R.id.fabAddUsuario);

        fab.setOnClickListener(v ->
                startActivity(new Intent(this, UsuarioFormActivity.class))
        );
    }



    /**
     * Configura los listeners del campo de búsqueda y los checkboxes
     * de tipo de usuario para aplicar el filtro ante cualquier cambio.
     */
    private void setupFiltros() {

        etBuscar.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void onTextChanged(CharSequence s, int a, int b, int c) {}

            @Override
            public void afterTextChanged(Editable s) {
                aplicarFiltro();
            }
        });

        CompoundButton.OnCheckedChangeListener listenerCheckbox =
                (buttonView, isChecked) -> aplicarFiltro();

        cbAlumno.setOnCheckedChangeListener(listenerCheckbox);
        cbProfesor.setOnCheckedChangeListener(listenerCheckbox);
        cbJuez.setOnCheckedChangeListener(listenerCheckbox);
        cbPropietario.setOnCheckedChangeListener(listenerCheckbox);
    }

    /**
     * Aplica el filtro combinado de texto libre y tipos seleccionados,
     * y actualiza el adaptador con los resultados obtenidos.
     *
     * Se ejecuta cada vez que cambia el texto de búsqueda
     * o el estado de algún checkbox de tipo.
     */
    private void aplicarFiltro() {

        viewModel.buscarUsuariosFiltrado(
                etBuscar.getText().toString().trim(),
                cbAlumno.isChecked(),
                cbProfesor.isChecked(),
                cbJuez.isChecked(),
                cbPropietario.isChecked()
        ).observe(this, usuarios -> adapter.actualizar(usuarios));
    }

    /**
     * Observa la lista completa de usuarios y actualiza
     * el adaptador cuando cambian los datos.
     */
    private void observarUsuarios() {

        viewModel.getUsuarios().observe(this, usuarios ->
                adapter.actualizar(usuarios)
        );
    }


    /**
     * Muestra un diálogo de confirmación antes de eliminar un usuario.
     *
     * Si el usuario confirma, delega la eliminación al ViewModel.
     *
     * @param u Usuario que se desea eliminar.
     */
    private void confirmarEliminar(Usuario u) {

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Eliminar usuario")
                .setMessage("¿Estás seguro de que quieres eliminar este usuario?")
                .setCancelable(false)
                .setPositiveButton("Eliminar",
                        (dialog, which) -> viewModel.eliminarUsuario(u))
                .setNegativeButton("Cancelar",
                        (dialog, which) -> dialog.dismiss())
                .show();
    }
}