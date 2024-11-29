package br.gohan.qualiar.helpers

import android.content.SharedPreferences
import android.content.pm.PackageManager
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

    fun invoke() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (hasLocationPermissions()) {
            getLastKnownLocation()
        } else {
            requestLocationPermissions(context)
        }
    }

    private fun getLastKnownLocation() {
        if (ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                val (city, country) = getCityName(latitude, longitude) ?: Pair(null, null)

                val savedCity = sharedPreferences.getString(LOCATION, null)

                val shouldUpdate = savedCity != city

                currentLocation.update {
                    Location(
                        latitude,
                        longitude,
                        city,
                        country,
                        shouldUpdate
                    )
                }
            } else {
                Toast.makeText(context, "Location not available", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(context, "Error: ${it.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun hasLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) ==
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermissions(mainActivity: ComponentActivity) {
        ActivityCompat.requestPermissions(
            mainActivity,
            arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    private fun getCityName(latitude: Double, longitude: Double): Pair<String, String>? {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                Pair(
                    addresses[0].subAdminArea,
                    addresses[0].countryName
                ) // City name - country name
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {
        const val LOCATION_REQUEST_CODE = 100
        val currentLocation = MutableStateFlow<Location?>(null)
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