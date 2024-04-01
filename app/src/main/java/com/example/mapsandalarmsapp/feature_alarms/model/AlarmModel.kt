package com.example.mapsandalarmsapp.feature_alarms.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AlarmModel(
    val id: Int,
    var alarmTimeMillis: Long,
    val title: String,
    val message: String,
) {
    fun getTimeStringFormat(): String {
        val sdf = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
        val date = Date(alarmTimeMillis)
        return sdf.format(date)
    }
}
