package com.example.mapsandalarmsapp.core.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper

@Dao
interface DaoMaps {
    @Query("SELECT * FROM ${DBNamesHelper.TABLE_MAPS} ORDER BY ${DBNamesHelper.COLUMN_MAPS_INDEX} DESC")
    suspend fun getAllMarkers(): List<EntityMaps>

    @Query("SELECT ${DBNamesHelper.COLUMN_MAPS_ADDRESS} FROM ${DBNamesHelper.TABLE_MAPS} WHERE ${DBNamesHelper.COLUMN_MAPS_INDEX} = :id")
    suspend fun getAddress(id: Long): String

    @Query("UPDATE ${DBNamesHelper.TABLE_MAPS} SET ${DBNamesHelper.COLUMN_MAPS_ADDRESS} = :address WHERE ${DBNamesHelper.COLUMN_MAPS_INDEX} = :id")
    suspend fun updateAddressById(id: Long, address: String)

    @JvmSuppressWildcards
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMarker(entity: EntityMaps): Long

    @Query("DELETE FROM ${DBNamesHelper.TABLE_MAPS} WHERE ${DBNamesHelper.COLUMN_MAPS_INDEX} = :id")
    suspend fun deleteMarker(id: Long)

    @Query("DELETE FROM ${DBNamesHelper.TABLE_MAPS}")
    suspend fun deleteAllMarkers()
}