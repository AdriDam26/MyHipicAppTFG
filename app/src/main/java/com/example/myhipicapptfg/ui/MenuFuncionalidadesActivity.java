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

/**
 * MenuFuncionalidadesActivity
 *
 * Pantalla principal de menú donde el usuario accede a las distintas funcionalidades
 * de la aplicación según su rol (admin, alumno, profesor, juez, etc.).
 *
 * Cada tarjeta (MaterialCardView) actúa como acceso directo a una sección concreta.
 */
public class MenuFuncionalidadesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_funcionalidades);

        /**
         * Referencias a las tarjetas del menú
         * Cada una representa una funcionalidad distinta
         */
        MaterialCardView cardAdmin       = findViewById(R.id.cardGestionAdmin);
        MaterialCardView cardAR          = findViewById(R.id.cardRealidadAumentada);
        MaterialCardView cardGPS         = findViewById(R.id.cardGPS);
        MaterialCardView cardReservar    = findViewById(R.id.cardReservarClase);
        MaterialCardView cardMisReservas = findViewById(R.id.cardMisReservas);
        MaterialCardView cardProfesor    = findViewById(R.id.cardVerClasesAsignadas);
        MaterialCardView cardJuez        = findViewById(R.id.cardMisPruebas); // ← nueva
        MaterialCardView cardCompeticiones = findViewById(R.id.cardMisCompeticiones);

        // Acceso al módulo administración
        cardAdmin.setOnClickListener(v ->
                startActivity(new Intent(this, AdminMenuActivity.class)));

        // Realidad Aumentada
        cardAR.setOnClickListener(v ->
                startActivity(new Intent(this, ARMenuActivity.class)));

        // GPS / Rutas
        cardGPS.setOnClickListener(v -> {
            Intent intent = new Intent(this, RutasMenuActivity.class);
            intent.putExtra("ID_PROPIETARIO", 12);
            startActivity(intent);
        });

        // Reserva de clases
        cardReservar.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReservaClaseActivity.class);
            intent.putExtra("ID_ALUMNO", 10);
            startActivity(intent);
        });

        // Mis Reservas
        cardMisReservas.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisReservasActivity.class);
            intent.putExtra("ID_ALUMNO", 10);
            startActivity(intent);
        });

        // Clases asignadas
        cardProfesor.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisClasesProfesorActivity.class);
            intent.putExtra(MisClasesProfesorActivity.EXTRA_ID_PROFESOR, 9);
            startActivity(intent);
        });

        // Pruebas asignadas
        cardJuez.setOnClickListener(v -> {
            Intent intent = new Intent(this, PruebasJuezActivity.class);
            intent.putExtra(PruebasJuezActivity.EXTRA_ID_JUEZ, 11);
            startActivity(intent);
        });

        // Competiciones
        cardCompeticiones.setOnClickListener(v -> {
            Intent intent = new Intent(this, MisPruebasActivity.class);
            intent.putExtra(MisPruebasActivity.EXTRA_ID_ALUMNO, 10);
            startActivity(intent);
        });
    }
}