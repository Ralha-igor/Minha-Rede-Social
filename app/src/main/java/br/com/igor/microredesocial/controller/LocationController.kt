package br.com.igor.microredesocial.controller

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationController(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    data class LocationData(val city: String, val lat: Double, val lng: Double)

    @SuppressLint("MissingPermission")
    fun obterLocalizacao(onResult: (LocationData?) -> Unit) {
        fusedClient.lastLocation
            .addOnSuccessListener { location ->
                if (location == null) {
                    onResult(null)
                    return@addOnSuccessListener
                }

                val lat = location.latitude
                val lng = location.longitude
                val city = obterCidade(lat, lng)

                onResult(LocationData(city, lat, lng))
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    private fun obterCidade(lat: Double, lng: Double): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val enderecos = geocoder.getFromLocation(lat, lng, 1)
            enderecos?.firstOrNull()?.locality ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}