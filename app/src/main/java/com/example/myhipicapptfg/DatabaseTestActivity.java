package com.example.myhipicapptfg; // Pon tu paquete aquí

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Cuadra;
import com.example.myhipicapptfg.entities.Equino;
import com.example.myhipicapptfg.entities.Propietario;
import com.example.myhipicapptfg.entities.Usuario;

public class DatabaseTestActivity extends AppCompatActivity {

    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnTestUser).setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    // 1. CREAR USUARIO (Base para el Propietario)
                    Usuario u = new Usuario();
                    u.nombre = "Juan";
                    u.apellido1 = "García";
                    u.apellido2 = "Pérez";
                    u.dni = "12345678Z";
                    u.email = "juan.propietario@email.com";
                    u.telefono = "600112233";
                    u.fechaNacimiento = "1985-05-20";
                    u.sexo = Usuario.SEXO_MASCULINO;
                    u.tipo = Usuario.TIPO_PROPIETARIO;

                    long idUsuario = db.usuarioDao().insertarUsuario(u);
                    Log.d("HIPICA", "1. Usuario Propietario creado con ID: " + idUsuario);

                    // 2. VINCULAR COMO PROPIETARIO
                    Propietario p = new Propietario();
                    p.idPropietario = (int) idUsuario;
                    db.propietarioDao().insertarPropietario(p);
                    Log.d("HIPICA", "2. Rol de Propietario asignado al ID: " + idUsuario);

                    // 3. CREAR CUADRA
                    Cuadra c = new Cuadra();
                    c.numeroCuadra = 105; // Número único según tu índice
                    long idCuadra = db.cuadraDao().insertarCuadra(c);
                    Log.d("HIPICA", "3. Cuadra creada con ID: " + idCuadra);

                    // 4. CREAR EQUINO CON TODOS LOS DATOS
                    Equino e = new Equino();
                    e.nombre = "Relámpago";
                    e.raza = "Pura Raza Española";
                    e.fechaNacimiento = "2018-03-15";
                    e.sexo = Equino.SEXO_MACHO;
                    e.altura = 1.65;
                    e.peso = 520.0;
                    e.temperamento = Equino.MANEJABLE;
                    e.estadoSalud = Equino.BUENO;
                    e.numeroMicrochip = "900123456789012"; // Único

                    // Foreign Keys
                    e.idPropietario = (int) idUsuario;
                    e.idCuadra = (int) idCuadra;

                    long idEquino = db.equinoDao().insertar(e);
                    Log.d("HIPICA", "4. Equino '" + e.nombre + "' creado con ID: " + idEquino);
                    Log.d("HIPICA", "--- PRUEBA COMPLETADA CON ÉXITO ---");

                } catch (Exception e) {
                    Log.e("HIPICA", "ERROR EN LA PRUEBA: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();
        });
    }
}