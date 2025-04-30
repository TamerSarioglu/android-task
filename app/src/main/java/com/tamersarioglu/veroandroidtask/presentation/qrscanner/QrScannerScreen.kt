package com.tamersarioglu.veroandroidtask.presentation.qrscanner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip

@Composable
fun QrScannerScreen(
    onQrScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val lifecycleOwner = LocalContext.current as LifecycleOwner
    val executor = remember { Executors.newSingleThreadExecutor() }
    var permissionGranted by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            permissionGranted = granted
        }
    )

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        permissionGranted = granted
        if (!granted) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (permissionGranted) {
            AndroidView(factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProvider = cameraProviderFuture.get()
                val preview = androidx.camera.core.Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }
                val selector = CameraSelector.DEFAULT_BACK_CAMERA
                val analysis = ImageAnalysis.Builder().build().also { imageAnalysis ->
                    imageAnalysis.setAnalyzer(executor) { imageProxy ->
                        processImageProxy(imageProxy) { result ->
                            if (!scanned && result != null) {
                                scanned = true
                                onQrScanned(result)
                            }
                        }
                    }
                }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, selector, preview, analysis
                )
                previewView
            }, modifier = Modifier.fillMaxSize())
            Box(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.5f))
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(220.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.Transparent)
                        .border(3.dp, Color.Green, RoundedCornerShape(24.dp))
                )
                Text(
                    text = "Align QR code within the box",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center).offset(y = -140.dp)
                )
            }
        }
        Button(onClick = onCancel, modifier = Modifier.align(Alignment.BottomCenter)) {
            Text("Cancel")
        }
    }
}

@OptIn(ExperimentalGetImage::class)
private fun processImageProxy(imageProxy: ImageProxy, onResult: (String?) -> Unit) {
    val mediaImage = imageProxy.image
    if (mediaImage != null) {
        val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
        val scanner = BarcodeScanning.getClient()
        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                val qr = barcodes.firstOrNull { it.format == Barcode.FORMAT_QR_CODE }
                onResult(qr?.rawValue)
            }
            .addOnFailureListener {
                onResult(null)
            }
            .addOnCompleteListener {
                imageProxy.close()
            }
    } else {
        imageProxy.close()
        onResult(null)
    }
} 