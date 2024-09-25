package com.example.permisosparcial2angieespinoza.permission

import android.Manifest
import android.content.pm.PackageManager
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
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage

@Composable
fun PermissionScreen() {
    val context = LocalContext.current

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
                // Verifica si el permiso ya está concedido
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    cameraLauncher.launch(null)
                } else {
                    requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Tomar Foto con Cámara")
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
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        galleryLauncher.launch("image/*")
                    } else {
                        requestStoragePermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Seleccionar Imagen de Galería")
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
                // Verifica si el permiso ya está concedido
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    val locationProvider = LocationProvider(context) { location ->
                        locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
                    }
                    locationProvider.requestLocationUpdates()
                } else {
                    requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(text = "Obtener Ubicación Actual")
        }

        // Mostrar la ubicación actual
        Text(
            text = locationText,
            modifier = Modifier.padding(8.dp)
        )
    }
}
