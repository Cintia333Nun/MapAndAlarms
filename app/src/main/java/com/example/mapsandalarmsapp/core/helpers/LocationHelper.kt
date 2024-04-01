package com.example.mapsandalarmsapp.core.helpers

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.HandlerThread
import androidx.core.app.ActivityCompat
import com.example.mapsandalarmsapp.feature_map.model.LocationModel
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import java.util.Locale
import javax.inject.Inject

class LocationHelper @Inject constructor(@ApplicationContext context: Context) {
    private val contextRef = WeakReference(context)
    private val locationManager by lazy {
        contextRef.get()?.let { context ->
            context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        }
    }
    private val sharePreferenceHelper by lazy { SharePreferenceHelper.getInstance(context) }
    private val locationListener: LocationListener by lazy {
        object : LocationListener {
            override fun onLocationChanged(location: Location) {
                sharePreferenceHelper?.saveData("LastLatitud", location.latitude.toFloat())
                sharePreferenceHelper?.saveData("LastLongitud", location.longitude.toFloat())
                sharePreferenceHelper?.saveData("LastSpeed", location.speed)
                sharePreferenceHelper?.saveData("lastProvider", location.provider)
                locationManager?.removeUpdates(this)
            }
            override fun onProviderEnabled(s: String) {}
            override fun onProviderDisabled(s: String) {}
        }
    }

    init {
        startLocationServiceUpdates()
    }

    private fun startLocationServiceUpdates() {
        contextRef.get()?.let { context ->
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return
            }

            val criteria = Criteria().apply {
                setAccuracy(Criteria.ACCURACY_FINE)
                isAltitudeRequired = false
                isBearingRequired = false
                isCostAllowed = true
                powerRequirement = Criteria.POWER_LOW
            }
            locationManager?.getBestProvider(criteria, true)?.let { provider ->
                val handlerThread = HandlerThread("HandlerThread")
                handlerThread.start()
                val looper = handlerThread.getLooper()
                locationManager?.requestLocationUpdates(provider, 0, 0f, locationListener, looper)
                sharePreferenceHelper?.saveData("last_provider", provider)
            }
        }
    }

    fun getLocation(): LocationModel? {
        contextRef.get()?.let { context ->
            val locationModel = LocationModel()
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                locationModel.location = LatLng(0.0, 0.0)
                locationModel.provider = "Not permission"
                return locationModel
            }
            var location = sharePreferenceHelper?.getStrData("lastProvider")?.let {
                locationManager?.getLastKnownLocation(it)
            }
            if (location != null) {
                return if (location.latitude != 0.0 && location.longitude != 0.0) {
                    locationModel.location = LatLng(location.latitude, location.longitude)
                    locationModel.provider = sharePreferenceHelper?.getStrData("lastProvider")
                    locationModel
                } else {
                    locationModel.location = LatLng(
                        sharePreferenceHelper?.getFloatData("LastLatitud") ?: 0.0,
                        sharePreferenceHelper?.getFloatData("LastLongitud") ?: 0.0
                    )
                    locationModel.provider = "preferences"
                    locationModel
                }
            }
            location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null) {
                return if (location.latitude != 0.0 && location.longitude != 0.0) {
                    locationModel.location = LatLng(location.latitude, location.longitude)
                    locationModel.provider = LocationManager.GPS_PROVIDER
                    locationModel
                } else {
                    locationModel.location = LatLng(
                        sharePreferenceHelper?.getFloatData("LastLatitud") ?: 0.0,
                        sharePreferenceHelper?.getFloatData("LastLongitud") ?: 0.0
                    )
                    locationModel.provider = "Preferences"
                    locationModel
                }
            }
            location = locationManager?.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (location != null) {
                return if (location.latitude != 0.0 && location.longitude != 0.0) {
                    locationModel.location = (LatLng(location.latitude, location.longitude))
                    locationModel.provider = (LocationManager.NETWORK_PROVIDER)
                    locationModel
                } else {
                    locationModel.location = LatLng(
                        sharePreferenceHelper?.getFloatData("LastLatitud") ?: 0.0,
                        sharePreferenceHelper?.getFloatData("LastLongitud") ?: 0.0
                    )
                    locationModel.provider = ("Preferences")
                    locationModel
                }
            }
            locationModel.location = LatLng(
                sharePreferenceHelper?.getFloatData("LastLatitud") ?: 0.0,
                sharePreferenceHelper?.getFloatData("LastLongitud") ?: 0.0
            )
            locationModel.provider = "Preferences"
            return locationModel
        }
        return null
    }

    fun getAddressFromLocation(latitude: Double, longitude: Double, onCallback: (String) -> Unit) {
        contextRef.get()?.let { context ->
            val geocoder = Geocoder(context, Locale.getDefault())
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val geocodeListener = Geocoder.GeocodeListener { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val addressText = "${address.countryName}, ${address.adminArea}, " +
                                    "${address.postalCode}, ${address.subLocality}, ${address.thoroughfare}, " +
                                    "${address.adminArea}, ${address.subThoroughfare}"
                            if (addressText.isEmpty()) {
                                onCallback("Address not enable")
                            } else onCallback(addressText)
                        } else {
                            onCallback("An error has occurred address is empty")
                        }
                    }
                    geocoder.getFromLocation(latitude, longitude, 1, geocodeListener)
                } else {
                    val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        val address: Address = addresses[0]
                        val addressText = "${address.countryName}, ${address.adminArea}, " +
                                "${address.postalCode}, ${address.subLocality}, ${address.thoroughfare}, " +
                                "${address.adminArea}, ${address.subThoroughfare}"
                        if (addressText.isEmpty()) {
                            onCallback("Address not enable")
                        } else onCallback(addressText)
                    }
                }
            } catch (e: Exception) {
                onCallback("An error has occurred ${e.printStackTrace()}")
            }
        }
    }
}