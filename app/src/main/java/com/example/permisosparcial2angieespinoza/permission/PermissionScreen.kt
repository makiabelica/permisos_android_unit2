package com.example.permisosparcial2angieespinoza.permission

import android.Manifest
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch

@Composable
fun PermissionScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    // Estados para manejar imágenes y ubicación
    var capturedImageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var locationText by remember { mutableStateOf("Ubicación no disponible") }

    // Launchers para la cámara y la galería
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        capturedImageBitmap = bitmap
    }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        selectedImageUri = uri
    }

    // Launchers para solicitar permisos
    val requestCameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            cameraLauncher.launch(null)
        } else {
            locationText = "Permiso de cámara denegado."
        }
    }

    val requestLocationPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val locationProvider = LocationProvider(context) { location ->
                locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            }
            locationProvider.requestLocationUpdates()
        } else {
            locationText = "Permiso de ubicación denegado."
        }
    }

    val requestStoragePermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            galleryLauncher.launch("image/*")
        } else {
            locationText = "Permiso de almacenamiento denegado."
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Botón para solicitar permiso de cámara y tomar foto
        Button(
            onClick = {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Solicitar Permiso de Cámara")
        }

        // Mostrar imagen capturada
        capturedImageBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )
        }

        // Botón para solicitar permiso de almacenamiento y seleccionar imagen de la galería
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    galleryLauncher.launch("image/*")
                } else {
                    requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Solicitar Permiso de Almacenamiento")
        }

        // Mostrar imagen seleccionada de la galería
        selectedImageUri?.let {
            AsyncImage(
                model = it,
                contentDescription = null,
                modifier = Modifier
                    .size(200.dp)
                    .padding(8.dp)
            )
        }

        // Botón para solicitar permiso de ubicación y obtener ubicación actual
        Button(
            onClick = {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Solicitar Permiso de Ubicación")
        }

        // Mostrar la ubicación actual
        Text(
            text = locationText,
            modifier = Modifier.padding(8.dp)
        )
    }
}
