package com.example.myhipicapptfg;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myhipicapptfg.database.TestDatabase;
import com.example.myhipicapptfg.entities.*;

import java.util.List;

public class DatabaseTestActivity extends AppCompatActivity {

    private TestDatabase db;
    // Etiqueta para filtrar en el Logcat (Escribe PRUEBA_FINAL_DB en el buscador de Logcat)
    private static final String TAG = "PRUEBA_FINAL_DB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_db);

        db = TestDatabase.getInstance(this);

        Log.d(TAG, "************************************************");
        Log.d(TAG, "   INICIANDO REPORTE COMPLETO DE BASE DE DATOS");
        Log.d(TAG, "************************************************");

        configurarObservadores();
    }

    private void configurarObservadores() {

        // 1. TABLA USUARIOS
        db.usuarioDao().obtenerTodosUsuarios().observe(this, lista -> {
            Log.d(TAG, "--- TABLA USUARIOS [" + (lista != null ? lista.size() : 0) + "] ---");
            if (lista != null) {
                for (Usuario u : lista) {
                    Log.i(TAG, "   [User] ID: " + u.idUsuario + " | Nombre: " + u.nombre + " | Tipo: " + u.tipo);
                }
            }
        });

        // 2. TABLA ALUMNOS
        db.alumnoDao().obtenerTodosAlumnos().observe(this, lista -> {
            Log.d(TAG, "--- TABLA ALUMNOS [" + (lista != null ? lista.size() : 0) + "] ---");
            if (lista != null) {
                for (Alumno a : lista) {
                    Log.i(TAG, "   [Alumno] FK_User: " + a.idAlumno);
                }
            }
        });

        // 3. TABLA PROFESORES
        db.profesorDao().obtenerTodosProfesores().observe(this, lista -> {
            Log.d(TAG, "--- TABLA PROFESORES [" + (lista != null ? lista.size() : 0) + "] ---");
            if (lista != null) {
                for (Profesor p : lista) {
                    Log.i(TAG, "   [Profesor] FK_User: " + p.idProfesor);
                }
            }
        });

        // 4. TABLA PROPIETARIOS
        db.propietarioDao().obtenerTodosPropietarios().observe(this, lista -> {
            Log.d(TAG, "--- TABLA PROPIETARIOS [" + (lista != null ? lista.size() : 0) + "] ---");
            if (lista != null) {
                for (Propietario pr : lista) {
                    Log.i(TAG, "   [Propietario] FK_User: " + pr.idPropietario);
                }
            }
        });

        // 5. TABLA ALUMNO_DISCIPLINA (Relación Nivel/Disciplina)
        db.alumnoDisciplinaDao().obtenerTodas().observe(this, listaAD -> {
            Log.d(TAG, "--- TABLA ALUMNO_DISCIPLINA [" + (listaAD != null ? listaAD.size() : 0) + "] ---");
            if (listaAD != null && !listaAD.isEmpty()) {
                for (AlumnoDisciplina ad : listaAD) {
                    Log.i(TAG, "   [Rel_Alu] ID_Alu: " + ad.idAlumno + " | Disc: " + ad.idDisciplina + " | Nivel: " + ad.nivel);
                }
            }
        });

        // 6. TABLA PROFESOR_DISCIPLINA
        db.profesorDisciplinaDao().obtenerTodas().observe(this, listaPD -> {
            Log.d(TAG, "--- TABLA PROFESOR_DISCIPLINA [" + (listaPD != null ? listaPD.size() : 0) + "] ---");
            if (listaPD != null && !listaPD.isEmpty()) {
                for (ProfesorDisciplina pd : listaPD) {
                    Log.i(TAG, "   [Rel_Prof] ID_Prof: " + pd.idProfesor + " | Disc: " + pd.idDisciplina);
                }
            }
        });

        // 7. TABLA CUADRAS (Nueva Gestión)
        db.cuadraDao().obtenerTodas().observe(this, listaCuadras -> {
            Log.d(TAG, "--- TABLA CUADRAS [" + (listaCuadras != null ? listaCuadras.size() : 0) + "] ---");
            if (listaCuadras != null && !listaCuadras.isEmpty()) {
                for (Cuadra c : listaCuadras) {
                    Log.i(TAG, "   [Cuadra] ID: " + c.idCuadra + " | Número: " + c.numeroCuadra);
                }
            } else {
                Log.w(TAG, "   [Cuadra] Sin registros.");
            }
        });

        // 8. TABLA PISTAS (Nueva Gestión)
        db.pistaDao().obtenerTodasPistas().observe(this, listaPistas -> {
            Log.d(TAG, "--- TABLA PISTAS [" + (listaPistas != null ? listaPistas.size() : 0) + "] ---");
            if (listaPistas != null && !listaPistas.isEmpty()) {
                for (Pista p : listaPistas) {
                    Log.i(TAG, "   [Pista] ID: " + p.idPista +
                            " | Nombre: " + p.nombre +
                            " | Medidas: " + p.ancho + "x" + p.largo + "m" +
                            " | Estado: " + p.estado);
                }
            } else {
                Log.w(TAG, "   [Pista] Sin registros.");
            }
        });
    }
}