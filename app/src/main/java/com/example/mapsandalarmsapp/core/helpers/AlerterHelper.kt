package com.example.mapsandalarmsapp.core.helpers

import android.app.Activity
import com.tapadoo.alerter.Alerter
import java.lang.ref.WeakReference

class AlerterHelper(activity: Activity) {
    companion object {
        const val LONG_TIME: Long = 4000
        const val SHORT_TIME: Long = 2000
    }
    private val activityRef = WeakReference(activity)

    fun showAlert(color: Int, title: String, text: String, iconResId: Int, durationMillis: Long) {
        activityRef.get()?.let { activity ->
            val alerter = Alerter.create(activity)
                .setTitle(title)
                .setText(text)
                .setIcon(iconResId)
                .setBackgroundColorRes(color)
                .setDuration(durationMillis)

            alerter.show()
        }
    }
}