package com.example.mapsandalarmsapp.feature_alarms.viewmodel

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsandalarmsapp.core.helpers.AlarmManagerHelper
import com.example.mapsandalarmsapp.core.sqlite.SqlDataBaseAlarms
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(
    private val location: AlarmManagerHelper,
    @ApplicationContext context: Context
): ViewModel() {
    private val dataBaseBasic by lazy { SqlDataBaseAlarms(context) }

    private val _alarms = mutableStateListOf<AlarmModel>()
    val alarms = _alarms

    fun getAllAlarms() {
        _alarms.clear()
        _alarms.addAll(dataBaseBasic.allAlarms)
    }

    fun addAlarm(alarmModel: AlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            if (dataBaseBasic.insertAlarm(alarmModel)) location.setAlarm(alarmModel)
            else println("Error adding new alarm")
        }
    }

    fun updateAlarm(index: Int, alarmModel: AlarmModel) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBaseBasic.updateAlarm(alarmModel.id, alarmModel.alarmTimeMillis)
            location.updateAlarm(alarmModel)
            _alarms[index] = alarmModel
        }
    }

    fun deleteAlarm(index: Int, id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            dataBaseBasic.deleteAlarm(id)
            location.deleteAlarm(id)
            _alarms.removeAt(index)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
}