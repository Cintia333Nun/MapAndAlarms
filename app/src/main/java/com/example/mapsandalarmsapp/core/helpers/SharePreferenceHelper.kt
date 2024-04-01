package com.example.mapsandalarmsapp.core.helpers

import android.content.Context
import android.content.SharedPreferences

class SharePreferenceHelper private constructor(context: Context) {
    companion object {
        private const val SHARE_PREFERENCE = "SHARE_PREFERENCE"
        const val KEY_IS_CHECKED = "KEY_IS_CHECkED"
        private var preference: SharePreferenceHelper? = null

        fun getInstance(context: Context): SharePreferenceHelper? {
            if (preference == null) preference = SharePreferenceHelper(context)
            return preference
        }
    }

    private val preferences: SharedPreferences?
    init {
        preferences = context.getSharedPreferences(SHARE_PREFERENCE, Context.MODE_PRIVATE)
    }

    fun saveData(key: String?, value: String?) {
        preferences?.edit()?.let { editSP ->
            editSP.putString(key, value)
            editSP.apply()
        }
    }

    fun saveData(key: String?, value: Boolean?) {
        preferences?.edit()?.let { editSP ->
            editSP.putBoolean(key, value!!)
            editSP.apply()
        }
    }

    fun saveData(key: String?, value: Int) {
        preferences?.edit()?.let { editSP ->
            editSP.putInt(key, value)
            editSP.apply()
        }
    }

    fun saveData(key: String?, value: Float) {
        preferences?.edit()?.let { editSP ->
            editSP.putFloat(key, value)
            editSP.apply()
        }
    }

    fun getStrData(key: String): String? {
        return if (preferences != null) preferences.getString(key, "") else ""
    }

    fun getBooData(key: String?): Boolean {
        return preferences != null && preferences.getBoolean(key, false)
    }

    fun getIntData(key: String?): Int {
        return preferences?.getInt(key, 0) ?: 0
    }

    fun getFloatData(key: String?): Double {
        return preferences?.getFloat(key, 0f)?.toDouble() ?: 0.0
    }

    fun getFloatDataNull(key: String?, coordenada: Float): Float {
        return preferences?.getFloat(key, coordenada) ?: 0f
    }

    fun clearSharedPreferencesForPictures() {
        val editor = preferences!!.edit()
        editor.clear()
        editor.apply()
    }
}
