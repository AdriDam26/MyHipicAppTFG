package com.example.myhipicapptfg.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.admin.AdminMenuActivity;
import com.example.myhipicapptfg.ui.ar.ARMenuActivity;
import com.example.myhipicapptfg.ui.reservas.profesor.MisClasesProfesorActivity;
import com.example.myhipicapptfg.ui.reservas.alumnos.MisReservasActivity;
import com.example.myhipicapptfg.ui.reservas.alumnos.ReservaClaseActivity;
import com.example.myhipicapptfg.ui.competiciones.participantes.pruebas.MisPruebasActivity;
import com.example.myhipicapptfg.ui.competiciones.juez.pruebas.PruebasJuezActivity;
import com.example.myhipicapptfg.ui.gps.RutasMenuActivity;
import com.google.android.material.card.MaterialCardView;

public class MenuFuncionalidadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcionalidades);

        MaterialCardView cardAdmin       = findViewById(R.id.cardGestionAdmin);
        MaterialCardView cardAR          = findViewById(R.id.cardRealidadAumentada);
        MaterialCardView cardGPS         = findViewById(R.id.cardGPS);
        MaterialCardView cardReservar    = findViewById(R.id.cardReservarClase);
        MaterialCardView cardMisReservas = findViewById(R.id.cardMisReservas);
        MaterialCardView cardProfesor    = findViewById(R.id.cardVerClasesAsignadas);
        MaterialCardView cardJuez        = findViewById(R.id.cardMisPruebas); // ← nueva
        MaterialCardView cardCompeticiones = findViewById(R.id.cardMisCompeticiones);

        cardAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMenuActivity.class)));

        cardAR.setOnClickListener(v ->
                startActivity(new Intent(this, ARMenuActivity.class)));

        cardGPS.setOnClickListener(v ->
                startActivity(new Intent(this, RutasMenuActivity.class)));

        cardReservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservaClaseActivity.class);
            intent.putExtra("ID_ALUMNO", 2);
            startActivity(intent);
        });

        cardMisReservas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisReservasActivity.class);
            intent.putExtra("ID_ALUMNO", 2);
            startActivity(intent);
        });

        cardProfesor.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisClasesProfesorActivity.class);
            intent.putExtra(MisClasesProfesorActivity.EXTRA_ID_PROFESOR, 7);
            startActivity(intent);
        });

        // ── Juez ─────────────────────────────────────────────────────────────
        cardJuez.setOnClickListener(v -> {
            Intent intent = new Intent(this, PruebasJuezActivity.class);
            // Sustituye el valor hardcodeado por el ID real del juez logueado
            intent.putExtra(PruebasJuezActivity.EXTRA_ID_JUEZ, 5);
            startActivity(intent);
        });

        cardCompeticiones.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisPruebasActivity.class);

            // ID fijo = 2 como pediste
            intent.putExtra(MisPruebasActivity.EXTRA_ID_ALUMNO, 2);

            startActivity(intent);
        });
    }
}