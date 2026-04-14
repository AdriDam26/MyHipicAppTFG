package com.example.myhipicapptfg;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myhipicapptfg.database.AppDatabase;
import com.example.myhipicapptfg.entities.Propietario;
import com.example.myhipicapptfg.entities.Usuario;

public class DatabaseTestActivity extends AppCompatActivity {

    private AppDatabase db;
    private static final String TAG = "HIPICA_TEST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        db = AppDatabase.getInstance(this);

        findViewById(R.id.btnTestUser).setOnClickListener(v -> {

            Log.i(TAG, "Creando nuevo propietario de prueba...");

            new Thread(() -> {

                try {
                    // ==========================
                    // 1️⃣ CREAR USUARIO
                    // ==========================

                    Usuario usuario = new Usuario();
                    usuario.nombre = "Carlos";
                    usuario.apellido1 = "García";
                    usuario.apellido2 = "López";

                    // Para evitar duplicados
                    long timestamp = System.currentTimeMillis();

                    usuario.dni = "DNI" + timestamp;
                    usuario.email = "carlos" + timestamp + "@test.com";
                    usuario.telefono = "600123456";
                    usuario.fechaNacimiento = "1990-01-01";
                    usuario.sexo = Usuario.SEXO_MASCULINO;
                    usuario.tipo = Usuario.TIPO_PROPIETARIO;

                    long idGenerado = db.usuarioDao().insertarUsuario(usuario);

                    Log.i(TAG, "Usuario creado con ID: " + idGenerado);

                    // ==========================
                    // 2️⃣ CREAR PROPIETARIO
                    // ==========================

                    Propietario propietario = new Propietario();
                    propietario.idPropietario = (int) idGenerado;

                    db.propietarioDao().insertarPropietario(propietario);

                    Log.i(TAG, "Propietario creado correctamente.");

                } catch (Exception e) {
                    Log.e(TAG, "Error creando propietario: " + e.getMessage());
                }

            }).start();

            // ==========================
            // 3️⃣ OBSERVAR PROPIETARIOS
            // ==========================

            db.propietarioDao().obtenerTodosPropietarios().observe(this, propietarios -> {

                if (propietarios == null || propietarios.isEmpty()) {
                    Log.w(TAG, "No hay propietarios registrados.");
                    return;
                }

                Log.i(TAG, "==== PROPIETARIOS REGISTRADOS: " + propietarios.size() + " ====");

                for (Propietario p : propietarios) {
                    Log.i(TAG, "Propietario ID: " + p.idPropietario);
                }
            });
        });
    }
}