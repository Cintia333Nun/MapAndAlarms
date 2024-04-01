package com.example.mapsandalarmsapp.feature_map.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsandalarmsapp.core.helpers.LocationHelper
import com.example.mapsandalarmsapp.core.repositories.MapAndAlarmsRepository
import com.example.mapsandalarmsapp.core.room.EntityMaps
import com.example.mapsandalarmsapp.feature_map.model.LocationModel
import com.example.mapsandalarmsapp.feature_map.model.MarkerModel
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    repository: MapAndAlarmsRepository, private val location: LocationHelper
) : ViewModel() {
    private val daoMaps = repository.getRoomLocalMapsDataSource()
    private val _listMarkers = MutableLiveData<List<MarkerModel>>(mutableListOf())
    val listMarkers: LiveData<List<MarkerModel>> = _listMarkers
    private val _isClear = MutableLiveData(false)
    val isClear: LiveData<Boolean> = _isClear

    fun setIsClear(status: Boolean) = _isClear.postValue(status)

    fun getMarkersSize() = listMarkers.value?.size ?: 0

    fun clearAllMarkers() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                daoMaps.deleteAllMarkers()
                _listMarkers.postValue(mutableListOf())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNewMarker(marker: MarkerModel) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newIndex = daoMaps.addMarker(EntityMaps(
                    latitude = marker.locationModel.location?.latitude ?: 0.0,
                    longitude = marker.locationModel.location?.longitude ?: 0.0,
                    address = marker.address
                ))
                marker.index = newIndex
                val currentList = _listMarkers.value?.toMutableList() ?: mutableListOf()
                currentList.add(marker)
                _listMarkers.postValue(currentList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteMarker(index: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                daoMaps.deleteMarker(index)
                val currentList = _listMarkers.value?.toMutableList() ?: mutableListOf()
                currentList.removeIf { marker -> marker.index == index }
                _listMarkers.postValue(currentList)
                if (currentList.isEmpty()) _isClear.postValue(true)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun getAllMarkersFromDataBase() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentList = daoMaps.getAllMarkers().map { entity ->
                    with(entity) {
                        MarkerModel(
                            index, LocationModel(LatLng(latitude, longitude), "DB"), address
                        )
                    }
                }
                _listMarkers.postValue(currentList)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun goToUserLocation(callBack: (Double, Double) -> Unit) {
        location.getLocation()?.let { locationModel ->
            callBack.invoke(
                locationModel.location?.longitude ?: 0.0,
                locationModel.location?.latitude ?: 0.0
            )
        }
    }

    fun getAddressLocation(marker: Marker, callback: (Long, String) -> Unit) {
        val idMarker = marker.title?.toLong() ?: 0
        viewModelScope.launch(Dispatchers.IO) {
            val address = daoMaps.getAddress(idMarker)
            viewModelScope.launch(Dispatchers.Main) {
                if (address.trim().isEmpty()) {
                    location.getAddressFromLocation(
                        marker.position.latitude,
                        marker.position.longitude
                    ) { message ->
                        viewModelScope.launch(Dispatchers.IO) {
                            daoMaps.updateAddressById(idMarker, message)
                        }
                        callback.invoke(idMarker, message)
                    }
                } else callback.invoke(idMarker, address)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}