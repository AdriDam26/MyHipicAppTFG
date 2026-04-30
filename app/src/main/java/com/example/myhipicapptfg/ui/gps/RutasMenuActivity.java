package com.example.myhipicapptfg.ui.gps;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.example.myhipicapptfg.R;

public class RutasMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rutas_menu);

        MaterialCardView btnGrabar = findViewById(R.id.btnGrabarRuta);
        MaterialCardView btnSeguir = findViewById(R.id.btnSeguirRuta);

        btnGrabar.setOnClickListener(v -> {
            Intent intent = new Intent(this, GrabarRutaActivity.class);
            intent.putExtra("ID_PROPIETARIO", getIntent().getIntExtra("ID_PROPIETARIO", -1));
            startActivity(intent);
        });

        btnSeguir.setOnClickListener(v -> {
            Intent intent = new Intent(this, ListaRutasActivity.class);
            startActivity(intent);
        });
    }
}