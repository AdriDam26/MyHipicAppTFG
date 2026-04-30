package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.ar.ARMenuActivity;
import com.google.android.material.card.MaterialCardView;

public class MenuFuncionalidadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcionalidades);

        MaterialCardView cardAdmin = findViewById(R.id.cardGestionAdmin);
        MaterialCardView cardAR = findViewById(R.id.cardRealidadAumentada);

        // Navegación al Menú de Administración
        cardAdmin.setOnClickListener(v -> {
            startActivity(new Intent(this, AdminMenuActivity.class));
        });

        // Navegación al Menú de Realidad Aumentada (AR)
        cardAR.setOnClickListener(v -> {
            startActivity(new Intent(this, ARMenuActivity.class));
        });
    }
}