package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.google.android.material.card.MaterialCardView;
import com.example.myhipicapptfg.ui.GestionUsuariosActivity;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu);

        configurarNavegacion();
    }

    private void configurarNavegacion() {
        // 1. Vincular las tarjetas desde el XML
        MaterialCardView cardUsuarios = findViewById(R.id.cardUsuarios);
        MaterialCardView cardEquinos = findViewById(R.id.cardEquinos);
        MaterialCardView cardInstalaciones = findViewById(R.id.cardInstalaciones);
        MaterialCardView cardClases = findViewById(R.id.cardClases);
        MaterialCardView cardCompeticiones = findViewById(R.id.cardCompeticiones);

        // 2. Configurar los eventos de clic

        // USUARIOS
        cardUsuarios.setOnClickListener(v -> {
            Intent intent = new Intent(this, GestionUsuariosActivity.class);
            startActivity(intent);
        });


    }


}