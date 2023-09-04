package com.aarogyaforworkers.aarogya.Location

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Handler
import android.provider.Settings
import android.widget.Toast
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import com.aarogyaforworkers.aarogya.CsvGenerator.CsvRepository
import com.aarogyaforworkers.aarogya.MainActivity
import java.io.IOException
import java.util.Locale


class LocationRepository {

    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var lastUserLocation : UserLocation? = UserLocation("","","","", "", "")
    private var isUserLocation : MutableState<UserLocation?> = mutableStateOf(lastUserLocation)
    var userLocation : State<UserLocation?> = isUserLocation

    /**
     * Requests permission to access the user's location.
     */
    private fun requestLocationPermission(context: Context){
        // Check if the app has permission to access the user's location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it from the user
            // Request both FINE and COARSE location permissions
            MainActivity.shared.requestPermissions(arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION), 1)
        }
    }

    /**
     * Updates the user's location with the given address.
     *
     * @param address The address to update the user's location with.
     */
    private fun updateLocation(address: Address, lat : String, lon : String){
        // Extract the locality, country name, postal code, city, and sub-administrative area from the address
        val locality = address.locality
        val country = address.countryName
        val zipCode = address.postalCode
        val city = address.adminArea
        val subArea = address.subAdminArea
        // Combine the locality, sub-administrative area, and city into a single user-readable address string
        val userAddress = "$locality, $subArea, $city"
        // Create a new UserLocation object with the extracted location information
        lastUserLocation = UserLocation(userAddress, city, zipCode, country, lat, lon)
        // Set the value of isUserLocation LiveData to the new UserLocation object
        MainActivity.shared.run { isUserLocation.value = lastUserLocation }
    }

    /**
     * Gets the user's current location using the device's GPS and updates the user's location with the obtained address.
     *
     * @param context The context used to obtain the user's location.
     */
    fun getLocation(context: Context) {
        // Check if the app has permission to access the user's location
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // If permission is not granted, request it from the user and return
            requestLocationPermission(context)
            return
        }
        var isError = false
        // Get the LocationManager system service
        locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Create a LocationListener to listen for location updates
        locationListener = LocationListener { location ->
            // When the location changes, log the accuracy and get the address for the new location
            val geocoder = Geocoder(context, Locale.getDefault())

            try {
                val addresses: List<Address> =
                    geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
                if (addresses.isNotEmpty()) {
                    // Update the user's location
                    updateLocation(addresses[0], location.longitude.toString(), location.longitude.toString())
                } else {
                    // No addresses found
                    showErrorMessage("Unable to retrieve location data. Please try again later.", context)
                }
            } catch (e: IOException) {
                // Handle the IOException
                if(!isError) {
                    isError = false
                    showErrorMessage("Unable to retrieve location data due to a network error. Please try again later.", context)
                }
            }
            // Remove the location updates to conserve battery
            locationManager.removeUpdates(locationListener)
        }

        when{

            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, locationListener)
            }

            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) -> {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
            }

            locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER) -> {
                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0f, locationListener)
            }

            locationManager.isProviderEnabled(LocationManager.FUSED_PROVIDER) -> {
                locationManager.requestLocationUpdates(LocationManager.FUSED_PROVIDER, 0, 0f, locationListener)
            }

            !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || !locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) -> {
                Toast.makeText(context, "Please enable location service", Toast.LENGTH_LONG).show()
                val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                context.startActivity(settingsIntent)
            }
        }
    }

    companion object {
        // Singleton instantiation you already know and love
        @Volatile private var instance: LocationRepository? = null

        fun getInstance() =
            instance ?: synchronized(this) {
                instance ?: LocationRepository().also { instance = it }
            }
    }
}
private fun showErrorMessage(message: String, context: Context) {
    // Display an error message to the user
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


