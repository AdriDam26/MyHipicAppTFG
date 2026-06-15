package com.example.myhipicapptfg.ui.ar.scanner;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.example.myhipicapptfg.R;
import com.example.myhipicapptfg.ui.ar.ar.ARActivity;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Actividad encargada de la lectura de códigos QR mediante la cámara
 * del dispositivo.
 *
 * Utiliza CameraX para la captura y visualización de imágenes en tiempo
 * real y ML Kit para la detección y decodificación de códigos QR.
 *
 * El valor obtenido del QR corresponde al número de microchip de un
 * caballo registrado en la aplicación.
 *
 * Flujo:
 *
 * Cámara → Detección QR → Microchip → ARActivity
 */
public class QRScannerActivity extends AppCompatActivity {

    /**
     * Componente visual donde se muestra la imagen capturada
     * por la cámara en tiempo real.
     */
    private PreviewView previewView;

    /**
     * Hilo secundario utilizado para procesar los fotogramas
     * de la cámara sin bloquear la interfaz de usuario.
     */
    private ExecutorService executor;

    /**
     * Detector de códigos QR proporcionado por ML Kit.
     */
    private BarcodeScanner scanner;

    /**
     * Indica si ya se ha detectado un código QR válido.
     *
     * Evita procesar múltiples veces el mismo QR mientras
     * la cámara continúa capturando imágenes.
     */
    private boolean yaDetectado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        // Obtiene una referencia al componente gráfico.
        previewView = findViewById(R.id.previewView);
        // Inicializa ML Kit
        scanner     = BarcodeScanning.getClient();
        // Crea un hilo de trabajo. Las imagenes se procesan aquí
        executor    = Executors.newSingleThreadExecutor();

        // Comprobamos si Android permite usar la cámara.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Si tiene permisos iniciamos CameraX
            iniciarCamara();
        } else {
            // Si no tiene permisos se cierra la actividad
            Toast.makeText(this,
                    "Sin permiso de cámara", Toast.LENGTH_SHORT).show();
            finish();
        }

    }

    // Obtiene el acceso a CameraX
    private void iniciarCamara() {
        // Instanciamos el gestor principal de CameraX
        // Abrir la cámara tarda un tiempo, CamaraX devuelve un Future
        ListenableFuture<ProcessCameraProvider> futuro =
                ProcessCameraProvider.getInstance(this);

        // Cuando la cámara termine de inicializarse
        futuro.addListener(() -> {
            try {
                // Obtiene el proveedor
                ProcessCameraProvider proveedor = futuro.get();
                // Se configura el sistema de cámara
                configurarCamara(proveedor);
            } catch (Exception e) {
                Log.e("QRScanner", "Error al iniciar cámara", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void configurarCamara(@NonNull ProcessCameraProvider proveedor) {
        // Aqui se muestra la salida visual de la cámara. (Lo que la cámara ve)
        Preview preview = new Preview.Builder().build();
        // Es el encargado de proporcionar una superficie donde dibujar las imágenes
        // Sin esta línea el usuario veria una pantalla en negro, ya que CamaraX necesita saber donde dibujar los fotogramas.
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Este componente recibe los mismos fotogramas y los analiza.
        ImageAnalysis analisis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        analisis.setAnalyzer(executor, this::procesarFrame);

        // Determina que cámara utilizar
        // Cogemos la cámara posterior.
        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        proveedor.unbindAll();
        // Aqui es donde CamaraX donde empieza a funcionar
        proveedor.bindToLifecycle(this, selector, preview, analisis);
    }

    // Recibe cada fotograma capturado
    @SuppressLint("UnsafeOptInUsageError") // Ocultar una advertencia de Android Studio
    private void procesarFrame(@NonNull ImageProxy frame) {

        // Control de lectura única
        if (yaDetectado) {
            // Si ya se leyó un QR, no se procesa nada más.
            frame.close();
            return;
        }

        // La imagen capturada se transforma en un InputImage para que lo entienda ML Kit
        InputImage imagen = InputImage.fromMediaImage(
                frame.getImage(),
                frame.getImageInfo().getRotationDegrees()
        );

        // ML Kit analiza la imagen
        scanner.process(imagen)
                .addOnSuccessListener(codigos -> { // Si ecuentras códigos QR, los recorre
                    for (Barcode codigo : codigos) {
                        // Obtiene el contenido almacenado dentro del QR.
                        String microchip = codigo.getRawValue();
                        if (microchip != null && !yaDetectado) {
                            yaDetectado = true;
                            navegarAR(microchip); // Va a la Realidad Aumentada
                        }
                    }
                })
                .addOnCompleteListener(t -> frame.close());
    }

    private void navegarAR(String microchip) {
        // Se utiliza runOnUiThread para volver al hilo principal antes de abrir la actividad de realidad aumentada
        runOnUiThread(() -> {
            Intent intent = new Intent(this, ARActivity.class);
            intent.putExtra("MICROCHIP", microchip);
            startActivity(intent);
            finish();
        });
    }


    /**
     * Libera los recursos utilizados por el hilo de procesamiento y por el detector QR cuando la actividad se cierra.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        scanner.close();
    }
}