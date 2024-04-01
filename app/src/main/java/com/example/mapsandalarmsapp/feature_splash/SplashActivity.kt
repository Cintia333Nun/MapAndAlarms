package com.example.mapsandalarmsapp.feature_splash

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.mapsandalarmsapp.MainActivity
import com.example.mapsandalarmsapp.core.helpers.AlertDialogHelper
import com.example.mapsandalarmsapp.core.helpers.DBNamesHelper
import com.example.mapsandalarmsapp.core.helpers.PermissionHelper
import com.example.mapsandalarmsapp.core.sqlite.DBHelper
import com.example.mapsandalarmsapp.ui.theme.MapsAndAlarmsAppTheme

@SuppressLint("CustomSplashScreen")
class SplashActivity : ComponentActivity() {
    private val permissions by lazy { PermissionHelper(this) }
    companion object {
        private const val TIME_SPLASH: Long =  5 * 1000 // 5 Seconds
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MapsAndAlarmsAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    SplashScreen()
                }
            }
        }
        createDataBaseAlarms()
        checkPermissions()
    }

    private fun createDataBaseAlarms() {
        val sqliteHelper = DBHelper(
            this, DBNamesHelper.NAME_DB2, null, DBNamesHelper.VERSION_DB2
        )
        val db = sqliteHelper.writableDatabase
        db.close()
    }

    private fun checkPermissions() {
        if (permissions.checkPermissions()) {
            startMainActivityTime()
        } else {
            if (!permissions.checkPermissions()) {
                AlertDialogHelper(this).showAlertDialog(
                    "Permissions",
                    "The application requires a permission that you have not granted. " +
                            "You will be redirected to the settings to grant it manually." +
                            "If you already accept the required permissions, " +
                            "select \"Check again\" to start the application."
                    ,
                    "Check again",
                    "Go to settings",
                    { checkPermissions() },
                    {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        intent.setData(Uri.parse("package:$packageName"))
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }

    private fun startMainActivityTime() {
        Handler(Looper.getMainLooper()).postDelayed({
            goToMainActivity()
        }, TIME_SPLASH)
    }

    private fun goToMainActivity() {
        val options = android.app.ActivityOptions.makeCustomAnimation(
            this, android.R.anim.fade_in, android.R.anim.fade_out
        )
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent, options.toBundle())
    }
}