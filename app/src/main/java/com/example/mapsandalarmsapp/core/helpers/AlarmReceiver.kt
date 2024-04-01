package com.example.mapsandalarmsapp.core.helpers

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.mapsandalarmsapp.MainActivity
import com.example.mapsandalarmsapp.R

class AlarmReceiver : BroadcastReceiver() {
    @SuppressLint("UnsafeProtectedBroadcastReceiver")
    override fun onReceive(context: Context?, intent: Intent?) {
        val title = intent?.getStringExtra("title") ?: "Alarm Notification"
        val message = intent?.getStringExtra("message") ?: "Your alarm is active"
        showNotification(context, title, message)
    }

    private fun showNotification(context: Context?, title: String, message: String) {
        context?.let {
            val notificationIntent = Intent(context, MainActivity::class.java)
            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(
                    context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                )
            } else {
                PendingIntent.getActivity(
                    context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
            }
            val notificationSound = Uri.parse("android.resource://${context.packageName}/${R.raw.alarm_sound}")

            val notification = NotificationCompat.Builder(
                context, AlarmManagerHelper.NOTIFICATION_CHANNEL_ID
            ).setContentTitle(title)
                .setContentText(message)
                .setSmallIcon(R.drawable.icon_notifications_active)
                .setContentIntent(pendingIntent)
                .setSound(notificationSound)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .build()

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(AlarmManagerHelper.NOTIFICATION_ID, notification)
        }
    }
}