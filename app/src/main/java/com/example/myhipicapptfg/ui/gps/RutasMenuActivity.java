package com.example.myhipicapptfg.ui.gps;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.ui.gps.grabar.GrabarRutaActivity;
import com.example.myhipicapptfg.ui.gps.seguir.ListaRutasActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.card.MaterialCardView;
import com.example.myhipicapptfg.R;


/**
 * Pantalla principal del módulo de rutas GPS.
 *
 * Esta Activity funciona como un menú donde el usuario puede:
 * - Grabar una nueva ruta GPS.
 * - Ver / seguir rutas ya guardadas.
 *
 * También recibe el ID del usuario propietario para asociar
 * las rutas a su cuenta.
 */
public class RutasMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_menu);

        // Botón para grabar ruta
        MaterialCardView btnGrabar = findViewById(R.id.btnGrabarRuta);

        // Botón para ver/seguir rutas guardadas
        MaterialCardView btnSeguir = findViewById(R.id.btnSeguirRuta);

        // ID del usuario que ha iniciado sesión o está activo
        // (si no viene en el Intent, se usa 12 como valor por defecto)
        int idUsuario = getIntent().getIntExtra("ID_PROPIETARIO", 12);

        // Toolbar superior del menú
        MaterialToolbar toolbar = findViewById(R.id.toolbarRutas);

        // Acción del icono de navegación (flecha atrás)
        toolbar.setNavigationOnClickListener(v ->
                getOnBackPressedDispatcher().onBackPressed()
        );

        /**
         * Ir a pantalla de grabar ruta GPS
         */
        btnGrabar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GrabarRutaActivity.class);
            // Se pasa el ID del usuario para asociar la ruta grabada
            intent.putExtra("ID_PROPIETARIO", idUsuario);
            startActivity(intent);
        });

        /**
         * Ir a pantalla donde se listan las rutas del usuario
         */
        btnSeguir.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaRutasActivity.class);
            //  se pasa el ID para filtrar rutas del usuario
            intent.putExtra("ID_PROPIETARIO", idUsuario);
            startActivity(intent);
        });
    }
}