package com.example.mapsandalarmsapp.core.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper

@Entity(tableName = DBNamesHelper.TABLE_MAPS)
data class EntityMaps(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = DBNamesHelper.COLUMN_MAPS_INDEX) val index: Long = 0L,
    @ColumnInfo(name = DBNamesHelper.COLUMN_MAPS_LATITUDE) val latitude: Double,
    @ColumnInfo(name = DBNamesHelper.COLUMN_MAPS_LONGITUDE) val longitude: Double,
    @ColumnInfo(name = DBNamesHelper.COLUMN_MAPS_ADDRESS) val address: String,
)