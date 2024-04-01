package com.example.mapsandalarmsapp.core.helpers

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.mapsandalarmsapp.R
import com.google.android.material.button.MaterialButton
import java.lang.ref.WeakReference

class AlertDialogHelper(activity: Activity) {
    private val weakContext = WeakReference(activity)

    @SuppressLint("MissingInflatedId")
    fun showAlertDialog(
        title: String,
        message: String,
        positiveButtonText: String,
        negativeButtonText: String,
        onPositiveClick: () -> Unit,
        onNegativeClick: () -> Unit
    ) {
        weakContext.get()?.let { context ->
            val builder = AlertDialog.Builder(context)
            val view = LayoutInflater.from(context).inflate(R.layout.view_dialog_alert, null)
            builder.setView(view)

            val titleTextView: TextView = view.findViewById(R.id.alert_title)
            val messageTextView: TextView = view.findViewById(R.id.alert_message)
            val positiveButton: MaterialButton = view.findViewById(R.id.button_positive)
            val negativeButton: MaterialButton = view.findViewById(R.id.button_negative)

            titleTextView.text = title
            messageTextView.text = message
            positiveButton.text = positiveButtonText
            negativeButton.text = negativeButtonText

            val dialog = builder.create()
            dialog.setOnShowListener {
                positiveButton.setOnClickListener {
                    onPositiveClick.invoke()
                    dialog.dismiss()
                }
                negativeButton.setOnClickListener {
                    onNegativeClick.invoke()
                    dialog.dismiss()
                }
            }

            dialog.show()
        }
    }
}