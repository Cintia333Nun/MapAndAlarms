package com.example.mapsandalarmsapp.core.helpers

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*

class TimeAndDateHelper(context: Context) {
    private val contextRef: WeakReference<Context> = WeakReference(context)
    private var minDate: Long? = null
    private var maxDate: Long? = null
    private var onDateTimeSelected: ((calendar: Calendar) -> Unit)? = null

    fun setMinDate(minDate: Long) {
        this.minDate = minDate
    }

    fun setMaxDate(maxDate: Long) {
        this.maxDate = maxDate
    }

    fun setOnDateTimeSelectedListener(listener: (calendar: Calendar) -> Unit) {
        this.onDateTimeSelected = listener
    }

    fun showDateTimePicker() {
        contextRef.get()?.let {  context ->
            try {
                val calendar = Calendar.getInstance()

                val datePickerDialog = DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth ->
                        calendar.set(Calendar.YEAR, year)
                        calendar.set(Calendar.MONTH, month)
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                        showTimePicker(calendar)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                minDate?.let { datePickerDialog.datePicker.minDate = it }
                maxDate?.let { datePickerDialog.datePicker.maxDate = it }

                datePickerDialog.show()
            } catch (e: Exception) {
                context.toastSimple("Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    private fun showTimePicker(calendar: Calendar) {
        contextRef.get()?.let { context ->
            try {
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        onDateTimeSelected?.invoke(calendar)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                )

                timePickerDialog.show()
            } catch (e: Exception) {
                context.toastSimple("Error: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    fun getDateTimeFormat(calendar: Calendar): String {
        return try {
            val simpleDateFormat = SimpleDateFormat("dd/MM/yy HH:mm:ss", Locale.getDefault())
            simpleDateFormat.format(calendar.time)
        } catch (e: Exception) {
            e.printStackTrace()
            "Error: ${e.message}"
        }
    }
}