package com.example.myhipicapptfg;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.Alumno;
import com.example.myhipicapptfg.entities.AlumnoDisciplina;
import com.example.myhipicapptfg.entities.Usuario;

public class DatabaseTestActivity extends AppCompatActivity {

    private TestDatabase db;
    private static final String TAG = "REPORTE_GENERAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Asegúrate de que este layout existe o usa el de tu actividad de test
        setContentView(R.layout.activity_test_db);

        db = TestDatabase.getInstance(this);

        Log.d(TAG, "--- Iniciando Modo Lectura de Base de Datos ---");

        // Configuramos los observadores que listarán los datos existentes
        configurarObservadores();
    }

    private void configurarObservadores() {


        // 3. LISTAR ALUMNO_DISCIPLINA (Relación N:M)
        db.alumnoDisciplinaDao().obtenerTodas().observe(this, listaAD -> {
            Log.d(TAG, ">> TABLA ALUMNO_DISCIPLINA [" + (listaAD != null ? listaAD.size() : 0) + " registros]");
            if (listaAD != null && !listaAD.isEmpty()) {
                for (AlumnoDisciplina ad : listaAD) {
                    Log.i(TAG, "   Alu_ID: " + ad.idAlumno + " | Disc_ID: " + ad.idDisciplina + " | Nivel: " + ad.nivel);
                }
            } else {
                Log.w(TAG, "   La tabla Alumno_Disciplina está vacía.");
            }
        });
    }
}