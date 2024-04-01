package com.example.mapsandalarmsapp.core.helpers

import android.content.Context
import android.view.View
import android.widget.EditText
import android.widget.Toast

fun Context.toastSimple(message: String) {
    Toast.makeText(
        this,
        message,
        Toast.LENGTH_SHORT
    ).show()
}

fun View.visible() { this.visibility = View.VISIBLE }
fun View.gone() { this.visibility = View.GONE }

fun EditText.validateText(): Boolean {
    val isOk = !(this.text.isNullOrBlank() || this.text.isEmpty())
    if(isOk) this.error = null
    else this.error = "Required field"
    return isOk
}