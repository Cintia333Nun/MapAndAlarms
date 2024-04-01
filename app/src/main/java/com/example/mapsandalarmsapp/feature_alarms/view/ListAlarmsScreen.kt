package com.example.mapsandalarmsapp.feature_alarms.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mapsandalarmsapp.R
import com.example.mapsandalarmsapp.core.helpers.TimeAndDateHelper
import com.example.mapsandalarmsapp.core.helpers.toastSimple
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel
import com.example.mapsandalarmsapp.feature_alarms.viewmodel.AlarmViewModel

@Composable
fun ListAlarmsScreen(
    viewModel: AlarmViewModel,
    goToNewAlarm: () -> Unit
) {
    val context = LocalContext.current
    val alarmsState = viewModel.alarms

    Column {
        Button(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            onClick = { goToNewAlarm() }
        ) {
            Icon(
                painter =  painterResource(id = R.drawable.icon_notification_add),
                contentDescription = "",
                tint = Color.White
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            itemsIndexed(alarmsState) { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(
                            colorResource(R.color.background_light),
                            shape = RoundedCornerShape(8.dp)
                        )
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = item.title, fontSize = 18.sp, color = colorResource(id = R.color.black))
                        Text(text = item.message, fontSize = 16.sp, color = colorResource(id = R.color.black))
                        Text(text = item.getTimeStringFormat(), fontSize = 14.sp, color = colorResource(id = R.color.black))
                    }
                    Button(
                        modifier = Modifier.padding(2.dp),
                        onClick = {
                            viewModel.deleteAlarm(index, item.id)
                        }
                    ) {
                        Text(text = "Delete")
                    }
                    Button(
                        modifier = Modifier.padding(2.dp),
                        onClick = {
                            val timeAndDateHelper = TimeAndDateHelper(context)
                            val minDate = System.currentTimeMillis()
                            val maxDate = minDate + (10 * 24 * 60 * 60 * 1000)

                            timeAndDateHelper.setMinDate(minDate)
                            timeAndDateHelper.setMaxDate(maxDate)
                            timeAndDateHelper.setOnDateTimeSelectedListener { calendar ->
                                val timeAlarmMil = calendar.time.time
                                val currentTimeMillis = System.currentTimeMillis()
                                val delayMillis = timeAlarmMil - currentTimeMillis
                                if (delayMillis > 0) {
                                    viewModel.updateAlarm(
                                        index,
                                        AlarmModel(item.id, timeAlarmMil, item.title, item.message)
                                    )
                                } else {
                                    context.toastSimple("The alarm time has already passed")
                                }

                            }
                            timeAndDateHelper.showDateTimePicker()
                        }
                    ) {
                        Text(text = "Update")
                    }
                }
            }
        }
    }
}