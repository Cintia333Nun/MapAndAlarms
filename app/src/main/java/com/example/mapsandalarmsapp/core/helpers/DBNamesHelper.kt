package com.example.mapsandalarmsapp.core.helpers

class DBNamesHelper {
    companion object {
        const val NAME = "maps_and_alarms.db"
        const val NAME_DB2 = "alarms_only_sqlite.db"
        const val VERSION = 1
        const val VERSION_DB2 = 1

        //region TABLE_MAPS
        const val TABLE_MAPS = "TABLE_MAPS"
        const val COLUMN_MAPS_LATITUDE = "COLUMN_MAPS_LATITUDE"
        const val COLUMN_MAPS_LONGITUDE = "COLUMN_MAPS_LONGITUDE"
        const val COLUMN_MAPS_INDEX = "COLUMN_MAPS_INDEX"
        const val COLUMN_MAPS_ADDRESS = "COLUMN_MAPS_ADDRESS"
        //endregion

        //region TABLE_ALARMS
        const val TABLE_ALARMS = "TABLE_ALARMS"
        const val COLUMN_ALARMS_ID = "COLUMN_ALARMS_ID"
        const val COLUMN_ALARMS_TIME = "COLUMN_ALARMS_TIME"
        const val COLUMN_ALARMS_TITLE = "COLUMN_ALARMS_TITLE"
        const val COLUMN_ALARMS_MESSAGE = "COLUMN_ALARMS_MESSAGE"
        //endregion
    }
}