package br.gohan.qualiar.helpers

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

class LocationHelper(
    private val context: ComponentActivity,
) : KoinComponent {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val sharedPreferences: SharedPreferences by inject()

    @SuppressLint("MissingPermission")
    fun getLastKnownLocation(completed: (Location) -> Unit) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                getCityName(latitude, longitude) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val (city, country) = Pair(
                            addresses[0].subAdminArea,
                            addresses[0].countryName
                        ) // City name - country name

                        val savedCity = sharedPreferences.getString(LOCATION, null)

                        val shouldUpdate = savedCity != city
                        completed.invoke( Location(
                            latitude,
                            longitude,
                            city,
                            country,
                            shouldUpdate
                        ))
                    }
                }
            } else {
                Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }


    private fun getCityName(latitude: Double, longitude: Double, result: (List<Address>) -> Unit) {
        val geocoder = Geocoder(context, Locale.getDefault())
        geocoder.getFromLocation(latitude, longitude, 1) {
            result.invoke(it)
        }
    }

    companion object {
        const val LOCATION = "location"
    }
}

data class Location(
    val latitude: Double,
    val longitude: Double,
    val city: String?,
    val country: String?,
    val shouldUpdate: Boolean
)