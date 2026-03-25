package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.google.android.material.button.MaterialButton; // IMPORTANTE: Cambiado de ImageButton a MaterialButton
import com.google.android.material.card.MaterialCardView;

public class SeleccionInstalacionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccion_instalacion);

        // 1. Vincular los componentes (Tipo corregido a MaterialButton)
        MaterialButton btnVolver = findViewById(R.id.btnVolverInstalaciones);
        MaterialCardView btnCuadras = findViewById(R.id.cardGestionCuadras);
        MaterialCardView btnPistas = findViewById(R.id.cardGestionPistas);

        // 2. Configurar el botón de volver atrás
        btnVolver.setOnClickListener(v -> {
            finish(); // Cierra esta actividad y vuelve a la anterior
        });

        btnCuadras.setOnClickListener(v -> {
            Intent intent = new Intent(this, AltaCuadraActivity.class);
            startActivity(intent);
        });

        btnPistas.setOnClickListener(v -> {
            Intent intent = new Intent(this, AltaPistaActivity.class);
            startActivity(intent);
        });
    }
}