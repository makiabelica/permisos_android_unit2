package com.example.permisosparcial2angieespinoza.permission

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority

class LocationProvider(
    private val context: Context,
    private val onLocationReceived: (Location) -> Unit
) {
    private val fusedLocationProviderClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let {
                onLocationReceived(it)
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(3000)
                .setMaxUpdateDelayMillis(10000)
                .build()

            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null)
                .addOnFailureListener { e ->
                    Log.e("LocationProvider", "Error solicitando actualizaciones de ubicación", e)
                }
        } catch (e: SecurityException) {
            Log.e("LocationProvider", "Permisos de ubicación no concedidos", e)
        } catch (e: Exception) {
            Log.e("LocationProvider", "Error desconocido al solicitar ubicación", e)
        }
    }

    fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
