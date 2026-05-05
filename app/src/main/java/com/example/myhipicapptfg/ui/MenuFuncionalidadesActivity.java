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

        // Referencias a las tarjetas del XML
        MaterialCardView cardAdmin = findViewById(R.id.cardGestionAdmin);
        MaterialCardView cardAR = findViewById(R.id.cardRealidadAumentada);
        MaterialCardView cardGPS = findViewById(R.id.cardGPS);
        MaterialCardView cardReservar = findViewById(R.id.cardReservarClase);
        MaterialCardView cardMisReservas = findViewById(R.id.cardMisReservas); // Nueva tarjeta

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

        // Navegación a Reservar Clase
        cardReservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservaClaseActivity.class);
            intent.putExtra("ID_ALUMNO", 1); // ID de prueba
            startActivity(intent);
        });

        // Navegación a Mis Reservas
        cardMisReservas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisReservasActivity.class);
            intent.putExtra("ID_ALUMNO", 1); // ID de prueba
            startActivity(intent);
        });
    }
}