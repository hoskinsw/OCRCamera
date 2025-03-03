package com.example.ocrproject

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraPreview(
    previewView: PreviewView,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner
) {
    //Do this first
}

private fun bindPreview(
    cameraProvider: ProcessCameraProvider,
    previewView: PreviewView,
    imageCapture: ImageCapture,
    lifecycleOwner: LifecycleOwner
) {
    //Do second
}