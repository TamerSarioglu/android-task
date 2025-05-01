package com.tamersarioglu.veroandroidtask.presentation.qrscanner

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tamersarioglu.veroandroidtask.presentation.components.CancelButton
import com.tamersarioglu.veroandroidtask.presentation.components.QrCameraPreview
import com.tamersarioglu.veroandroidtask.presentation.components.QrScannerOverlay
import com.tamersarioglu.veroandroidtask.utils.CameraPermissionHandler

@Composable
fun QrScannerScreen(
    onQrScanned: (String) -> Unit,
    onCancel: () -> Unit
) {
    var permissionGranted by remember { mutableStateOf(false) }
    var scanned by remember { mutableStateOf(false) }

    CameraPermissionHandler(Manifest.permission.CAMERA) {
        permissionGranted = it
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (permissionGranted) {
            QrCameraPreview(
                onQrScanned = onQrScanned,
                scanned = scanned,
                setScanned = { scanned = it }
            )
            QrScannerOverlay()
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp)
        ) {
            CancelButton(onCancel = onCancel)
        }
    }
}