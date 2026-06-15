package com.example.myhipicapptfg.ui.ar;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.ar.scanner.QRScannerActivity;
import com.google.android.material.appbar.MaterialToolbar;


/**
 * Actividad que actúa como menú de entrada al módulo de
 * Realidad Aumentada de la aplicación.
 *
 * Su principal responsabilidad es:
 * - Mostrar la interfaz de acceso al escáner QR.
 * - Gestionar los permisos de cámara.
 * - Redirigir al usuario a la pantalla de escaneo.
 *
 * Esta actividad constituye el punto de inicio del flujo:
 *
 * Menú AR → Escáner QR → Identificación del caballo →
 */
 public class ARMenuActivity extends AppCompatActivity {

    /**
     * Código identificador utilizado para gestionar la
     * solicitud del permiso de cámara.
     */
    private static final int PERMISO_CAMARA = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ar_menu);

        // Obtiene la referencia al botón encargado de iniciar el proceso de escaneo.
        Button btnEscanear = findViewById(R.id.btnEscanear);


        // Obtiene la barra superior de navegación.
        MaterialToolbar toolbar = findViewById(R.id.toolbarEscanner);

        // Obtiene la barra superior de navegación
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ejecuta la acción de ir hacia atrás
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        // Cuando el usuario pulsa el botón se verifica los permisos
        btnEscanear.setOnClickListener(v -> verificarPermiso());
    }

    /**
     * Comprueba si la aplicación dispone del permiso
     * necesario para utilizar la cámara del dispositivo.
     *
     * Si el permiso ya fue concedido, se abre directamente
     * el escáner QR.
     *
     * En caso contrario, se solicita al usuario.
     */
    private void verificarPermiso() {
        // Comprobamos si Android ya le concedió acceso a la cámara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Si existe permisos va al escaner
            irAlEscaner();
        } else {
            // En caso contrario le pide permisos
            // Cuando el usario pulsa uno de los botones del dialogo, Android llama al método onRequestPermissionsResult
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    PERMISO_CAMARA
            );
        }
    }

    /**
     * Método ejecutado cuando el usuario responde
     * a la solicitud de permisos.
     *
     * @param requestCode Código identificador del permiso.
     * @param permissions Lista de permisos solicitados.
     * @param grantResults Resultado de la solicitud.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISO_CAMARA &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            irAlEscaner();

        } else {
            Toast.makeText(this,
                    "Necesitas dar permiso de cámara",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Inicia la actividad encargada de leer
     * códigos QR mediante la cámara.
     */
    private void irAlEscaner() {
        startActivity(new Intent(this, QRScannerActivity.class));
    }
}