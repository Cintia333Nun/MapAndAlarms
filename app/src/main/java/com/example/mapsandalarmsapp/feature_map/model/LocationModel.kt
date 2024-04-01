package com.example.mapsandalarmsapp.feature_map.model

import com.google.android.gms.maps.model.LatLng


data class LocationModel(
    var location: LatLng? = null,
    var provider: String? = null
)
