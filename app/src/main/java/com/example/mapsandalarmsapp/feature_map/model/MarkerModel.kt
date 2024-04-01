package com.example.mapsandalarmsapp.feature_map.model

data class MarkerModel(
    var index: Long,
    val locationModel: LocationModel,
    val address: String,
)