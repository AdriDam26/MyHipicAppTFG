package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.ar.ARMenuActivity;
import com.example.myhipicapptfg.ui.gps.RutasMenuActivity;
import com.google.android.material.card.MaterialCardView;

public class MenuFuncionalidadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcionalidades);

        MaterialCardView cardAdmin = findViewById(R.id.cardGestionAdmin);
        MaterialCardView cardAR = findViewById(R.id.cardRealidadAumentada);
        MaterialCardView cardGPS = findViewById(R.id.cardGPS);

        // Navegación al menú Admin
        cardAdmin.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMenuActivity.class));
        });

        // Navegación al menú AR
        cardAR.setOnClickListener(v -> {
            startActivity(new Intent(this, ARMenuActivity.class));
        });

        // Navegación al menú GPS
        cardGPS.setOnClickListener(v -> {
            startActivity(new Intent(this, RutasMenuActivity.class));
        });
    }
}