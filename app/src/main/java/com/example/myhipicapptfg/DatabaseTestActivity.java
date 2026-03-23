package com.example.myhipicapptfg; // Pon tu paquete aquí

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.Usuario;

public class DatabaseTestActivity extends AppCompatActivity {

    private TestDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        db = TestDatabase.getInstance(this);

        findViewById(R.id.btnTestUser).setOnClickListener(v -> {
            new Thread(() -> {
                try {
                    // 1. Crear Usuario
                    Usuario u = new Usuario();
                    u.nombre = "Prueba";
                    u.dni = "11122233A";
                    u.tipo = Usuario.TIPO_ALUMNO;

                    long id = db.usuarioDao().insertarUsuario(u);
                    Log.d("HIPICA", "ID Usuario creado: " + id);

                    // 2. Crear Alumno
                    Alumno a = new Alumno();
                    a.idAlumno = (int) id;
                    db.alumnoDao().insertarAlumno(a);
                    Log.d("HIPICA", "Alumno vinculado al ID: " + id);

                } catch (Exception e) {
                    Log.e("HIPICA", "Error: " + e.getMessage());
                }
            }).start();
        });
    }
}