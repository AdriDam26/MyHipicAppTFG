package com.example.myhipicapptfg.ui;


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
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.barcode.BarcodeScanner;
import com.google.mlkit.vision.barcode.BarcodeScanning;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.common.InputImage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QRScannerActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ExecutorService executor;
    private BarcodeScanner scanner;
    private boolean yaDetectado = false; // Evita procesar el mismo QR dos veces

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_scanner);

        previewView = findViewById(R.id.previewView);
        scanner     = BarcodeScanning.getClient();
        executor    = Executors.newSingleThreadExecutor();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            iniciarCamara();
        } else {
            // No debería llegar aquí, pero por seguridad
            Toast.makeText(this,
                    "Sin permiso de cámara", Toast.LENGTH_SHORT).show();
            finish();
        }

        iniciarCamara();
    }

    private void iniciarCamara() {
        ListenableFuture<ProcessCameraProvider> futuro =
                ProcessCameraProvider.getInstance(this);

        futuro.addListener(() -> {
            try {
                ProcessCameraProvider proveedor = futuro.get();
                configurarCamara(proveedor);
            } catch (Exception e) {
                Log.e("QRScanner", "Error al iniciar cámara", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void configurarCamara(@NonNull ProcessCameraProvider proveedor) {
        // Vista previa
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        // Análisis de imágenes para detectar QR
        ImageAnalysis analisis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();
        analisis.setAnalyzer(executor, this::procesarFrame);

        // Cámara trasera
        CameraSelector selector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        proveedor.unbindAll();
        proveedor.bindToLifecycle(this, selector, preview, analisis);
    }

    @SuppressLint("UnsafeOptInUsageError")
    private void procesarFrame(@NonNull ImageProxy frame) {
        if (yaDetectado) {
            frame.close();
            return;
        }

        InputImage imagen = InputImage.fromMediaImage(
                frame.getImage(),
                frame.getImageInfo().getRotationDegrees()
        );

        scanner.process(imagen)
                .addOnSuccessListener(codigos -> {
                    for (Barcode codigo : codigos) {
                        // ✅ DESPUÉS — nombre claro
                        String microchip = codigo.getRawValue();
                        if (microchip != null && !yaDetectado) {
                            yaDetectado = true;
                            navegarAR(microchip); // microchip = Numero_Microchip del equino
                        }
                    }
                })
                .addOnCompleteListener(t -> frame.close());
    }

    private void navegarAR(String microchip) {
        runOnUiThread(() -> {
            Intent intent = new Intent(this, ARActivity.class);
            intent.putExtra("MICROCHIP", microchip); // ← correcto
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executor.shutdown();
        scanner.close();
    }
}