package com.example.myhipicapptfg.ui.admin;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.admin.clases.GestionClasesActivity;
import com.example.myhipicapptfg.ui.admin.competiciones.GestionCompeticionesActivity;
import com.example.myhipicapptfg.ui.admin.equinos.GestionEquinosActivity;
import com.example.myhipicapptfg.ui.admin.pistas.GestionPistaActivity;
import com.example.myhipicapptfg.ui.admin.usuarios.GestionUsuariosActivity;
import com.google.android.material.card.MaterialCardView;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        configurarNavegacion();
    }

    private void configurarNavegacion() {

        MaterialCardView cardUsuarios = findViewById(R.id.cardUsuarios);
        MaterialCardView cardEquinos = findViewById(R.id.cardEquinos);
        MaterialCardView cardClases = findViewById(R.id.cardClases);
        MaterialCardView cardCompeticiones = findViewById(R.id.cardCompeticiones);
        MaterialCardView cardPistas = findViewById(R.id.cardPistas);

        // USUARIOS
        cardUsuarios.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionUsuariosActivity.class));
        });

        // 🔹 PISTAS
        cardPistas.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionPistaActivity.class));
        });

        cardEquinos.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionEquinosActivity.class));
        });

        cardClases.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionClasesActivity.class));
        });

        cardCompeticiones.setOnClickListener(v -> {
            startActivity(new Intent(this, GestionCompeticionesActivity.class));
        });


    }
}