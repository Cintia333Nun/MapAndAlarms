package com.example.mapsandalarmsapp.core.repositories

import com.example.mapsandalarmsapp.core.room.DaoMaps
import com.example.mapsandalarmsapp.core.room.RoomDB
import javax.inject.Inject

class MapAndAlarmsRepository @Inject constructor(private val dataBase: RoomDB) {
    fun getRoomLocalMapsDataSource(): DaoMaps = dataBase.daoMaps()
}