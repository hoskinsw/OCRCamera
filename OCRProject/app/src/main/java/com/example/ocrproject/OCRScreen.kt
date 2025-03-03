package com.example.ocrproject

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OCRScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val imageCapture = remember { ImageCapture.Builder().build() }
    val recognizer = remember { TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS) }
    val recognizedText = remember { mutableStateOf("") }

    // Camera permission state
    val cameraPermissionState = rememberPermissionState(
        android.Manifest.permission.CAMERA
    )

    if (cameraPermissionState.status.isGranted) {
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .weight(.8f)
            ) {
                CameraPreview(
                    previewView = previewView,
                    imageCapture = imageCapture,
                    lifecycleOwner = lifecycleOwner
                )
            }
            Box(
                modifier = Modifier
                    .weight(.2f)
                    .clip(RoundedCornerShape(150.dp))
                    .background(color = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Button(onClick = {
                        takePhotoAndAnalyze(
                            imageCapture = imageCapture,
                            recognizer = recognizer,
                        ) { text ->
                            recognizedText.value = text
                        }
                    }) {
                        Text("Capture and Analyze")
                    }
                    LazyColumn(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        item {
                            Text("Recognized Text: ${recognizedText.value}")
                        }
                    }
                }
            }
        }
    } else {
        // Request camera permission
        LaunchedEffect(Unit) {
            cameraPermissionState.launchPermissionRequest()
        }
    }
}

private fun takePhotoAndAnalyze(
    imageCapture: ImageCapture,
    recognizer: com.google.mlkit.vision.text.TextRecognizer,
    onTextRecognized: (String) -> Unit
) {
    val executor = Executors.newSingleThreadExecutor()

    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(imageProxy: ImageProxy) {
                val bitmap = imageProxy.toBitmap()
                val rotation = imageProxy.imageInfo.rotationDegrees.toFloat()
                val rotatedBitmap = bitmap.rotate(rotation)

                val image = InputImage.fromBitmap(rotatedBitmap, 0)

                recognizer.process(image)
                    .addOnSuccessListener { visionText ->
                        val text = visionText.text
                        onTextRecognized(text)
                    }
                    .addOnFailureListener { e ->
                        onTextRecognized("Error: ${e.message}")
                    }
                    .addOnCompleteListener {
                        imageProxy.close()
                    }
            }
        }
    )
}

private fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}