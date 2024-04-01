package com.example.mapsandalarmsapp.core.room

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper

@Database(
    entities = [EntityMaps::class],
    version = DBNamesHelper.VERSION,
    exportSchema = false,
)
abstract class RoomDB: RoomDatabase() {
    abstract fun daoMaps(): DaoMaps
}