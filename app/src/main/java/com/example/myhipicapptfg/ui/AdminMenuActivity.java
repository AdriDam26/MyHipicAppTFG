package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.google.android.material.card.MaterialCardView;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        // 1. Vincular las tarjetas
        MaterialCardView cardUsuarios = findViewById(R.id.cardUsuarios);
        MaterialCardView cardEquinos = findViewById(R.id.cardEquinos);
        MaterialCardView cardInstalaciones = findViewById(R.id.cardInstalaciones);
        MaterialCardView cardClases = findViewById(R.id.cardClases);

        // 2. Configurar Clics

        // USUARIOS
        cardUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(AdminMenuActivity.this, GestionAlumnosActivity.class);
            startActivity(intent);
        });

        // EQUINOS
        cardEquinos.setOnClickListener(v -> {
            Intent intent = new Intent(this, AltaEquinoActivity.class);
            startActivity(intent);
        });

        // INSTALACIONES
        cardInstalaciones.setOnClickListener(v -> {
            Intent intent = new Intent(this, SeleccionInstalacionActivity.class);
            startActivity(intent);
        });

        // CLASES
        cardClases.setOnClickListener(v -> {
            Intent intent = new Intent(this, AltaClaseActivity.class);
            startActivity(intent);
            showComingSoon("Calendario de Clases");
        });
    }

    private void showComingSoon(String seccion) {
        Toast.makeText(this, seccion + " próximamente", Toast.LENGTH_SHORT).show();
    }
}