package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.R;
import com.google.android.material.card.MaterialCardView;

public class AdminMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_menu); // Asegúrate de que este nombre coincide con tu XML

        // 1. Vincular las tarjetas del XML
        MaterialCardView cardUsuarios = findViewById(R.id.cardUsuarios);
        MaterialCardView cardCaballos = findViewById(R.id.cardCaballos);


        cardUsuarios.setOnClickListener(v -> {
            // Abrimos la pantalla de registro de usuarios que ya tenemos hecha
            Intent intent = new Intent(AdminMenuActivity.this, AltaUsuarioActivity.class);
            startActivity(intent);
        });

        // 3. Configurar el clic para Caballos
        cardCaballos.setOnClickListener(v -> {
            // Por ahora, como no hemos hecho la de caballos, podemos mostrar un mensaje
            // o dejarlo listo para cuando crees "AltaCaballoActivity"
            /*
            Intent intent = new Intent(AdminMenuActivity.this, AltaCaballoActivity.class);
            startActivity(intent);
            */
            android.widget.Toast.makeText(this, "Gestión de caballos próximamente", android.widget.Toast.LENGTH_SHORT).show();
        });
    }
}