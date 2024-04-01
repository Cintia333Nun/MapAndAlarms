package com.example.mapsandalarmsapp.core.helpers

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import com.example.mapsandalarmsapp.R
import com.example.mapsandalarmsapp.feature_alarms.model.AlarmModel
import dagger.hilt.android.qualifiers.ApplicationContext
import java.lang.ref.WeakReference
import javax.inject.Inject

class AlarmManagerHelper @Inject constructor(@ApplicationContext context: Context) {
    companion object {
        const val NOTIFICATION_CHANNEL_ID = "alarm_channel"
        const val NOTIFICATION_ID = 1001
    }
    private val contextRef = WeakReference(context)
    private val alarmManager by lazy {
        contextRef.get()?.let { context ->
            context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        }
    }
    private val notificationManager by lazy {
        contextRef.get()?.let { context ->
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        }
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            "Alarm Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        contextRef.get()?.let { context ->
            val notificationSound = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_sound}")
            val attributes =  AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build()

            channel.setSound(notificationSound, attributes)
        }
        notificationManager?.createNotificationChannel(channel)
    }

    @SuppressLint("ScheduleExactAlarm")
    fun setAlarm(alarmModel: AlarmModel) {
        contextRef.get()?.let { context ->
            val intent = Intent(context, AlarmReceiver::class.java)
            intent.putExtra("title", alarmModel.title)
            intent.putExtra("message", alarmModel.message)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context, alarmModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, alarmModel.id, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            alarmManager?.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP, alarmModel.alarmTimeMillis, pendingIntent
            )
        }
    }

    fun deleteAlarm(alarmId: Int) {
        contextRef.get()?.let { context ->
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getBroadcast(
                    context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT  or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getBroadcast(
                    context, alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            pendingIntent?.let {
                alarmManager?.cancel(it)
            }
        }
    }

    fun updateAlarm(alarmModel: AlarmModel) {
        deleteAlarm(alarmModel.id)
        setAlarm(alarmModel)
    }
}