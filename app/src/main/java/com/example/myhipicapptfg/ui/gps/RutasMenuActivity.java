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

public class RutasMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_menu);

        MaterialCardView btnGrabar = findViewById(R.id.btnGrabarRuta);
        MaterialCardView btnSeguir = findViewById(R.id.btnSeguirRuta);

        int idUsuario = getIntent().getIntExtra("ID_PROPIETARIO", 12);

        // Vincula el Toolbar de Rutas
        MaterialToolbar toolbar = findViewById(R.id.toolbarRutas);

        // Configura la acción para ir hacia atrás
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        btnGrabar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GrabarRutaActivity.class);
            intent.putExtra("ID_PROPIETARIO", idUsuario); // Envía el ID activo (12)
            startActivity(intent);
        });

        btnSeguir.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaRutasActivity.class);
            intent.putExtra("ID_PROPIETARIO", idUsuario); // ← ¡Faltaba añadir esto para listar sus rutas!
            startActivity(intent);
        });
    }
}