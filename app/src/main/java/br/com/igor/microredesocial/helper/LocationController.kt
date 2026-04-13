package br.com.igor.microredesocial.helper

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class LocationController(private val context: Context) {

    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    data class LocationData(val city: String, val lat: Double, val lng: Double)

    @SuppressLint("MissingPermission")
    fun obterLocalizacao(onResult: (LocationData?) -> Unit) {
        val request = CurrentLocationRequest.Builder()
            .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            .setMaxUpdateAgeMillis(0)
            .build()

        fusedClient.getCurrentLocation(request, null)
            .addOnSuccessListener { location ->
                if (location == null) {
                    onResult(null)
                    return@addOnSuccessListener
                }

                val lat = location.latitude
                val lng = location.longitude

                obterCidade(lat, lng) { city ->
                    onResult(LocationData(city, lat, lng))
                }
            }
            .addOnFailureListener {
                onResult(null)
            }
    }

    private fun obterCidade(lat: Double, lng: Double, onResult: (String) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())

        geocoder.getFromLocation(lat, lng, 1, object : Geocoder.GeocodeListener {
            override fun onGeocode(addresses: MutableList<Address>) {
                val address = addresses.firstOrNull()
                // locality pode ser nulo em algumas regiões, fallback para subAdminArea
                val city = address?.locality
                    ?: address?.subAdminArea
                    ?: address?.adminArea
                    ?: ""
                onResult(city)
            }

            override fun onError(errorMessage: String?) {
                onResult("")
            }
        })
    }
}