package com.example.cleankrasnoyark.GPS

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class LocationProvider(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    fun getLastKnownLocation(callback: (Location?) -> Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    callback(location)
                }
                .addOnFailureListener { exception ->
                    // Обработка ошибки
                    callback(null)
                }
        } else {
            // Разрешения не предоставлены
            callback(null)
        }
    }
}