package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.R;
import com.google.android.material.card.MaterialCardView;

public class MenuFuncionalidadesActivity extends AppCompatActivity {

    private MaterialCardView cardQR, cardRutas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcionalidades);

        cardQR = findViewById(R.id.cardQR);
        cardRutas = findViewById(R.id.cardRutas);

        cardQR.setOnClickListener(v ->
                startActivity(new Intent(this, ARMenuActivity.class))
        );

        cardRutas.setOnClickListener(v ->
                startActivity(new Intent(this, RutasMenuActivity.class))
        );
    }
}