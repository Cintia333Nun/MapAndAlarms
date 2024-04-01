package com.example.mapsandalarmsapp.core.helpers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


public class PermissionHelper {
    private final String[] globalPermissions;
    private final Activity activity;

    public PermissionHelper(Activity activity) {
        this.activity = activity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            globalPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.POST_NOTIFICATIONS,
            };
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            globalPermissions = new String[]{
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.SCHEDULE_EXACT_ALARM,
            };
        } else {
            globalPermissions = new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
            };
        }
    }

    public boolean checkPermissions() {
        int numberOfPermissions = 0;

        for (String globalPermission : globalPermissions) {
            if (ContextCompat.checkSelfPermission(activity, globalPermission) != PackageManager.PERMISSION_GRANTED) {
                numberOfPermissions++;
            }
        }

        String[] permissions = new String[numberOfPermissions];
        numberOfPermissions = 0;

        for (String globalPermission : globalPermissions) {
            if (ContextCompat.checkSelfPermission(activity, globalPermission) != PackageManager.PERMISSION_GRANTED) {
                permissions[numberOfPermissions++] = globalPermission;
            }
        }

        if (numberOfPermissions > 0) {
            ActivityCompat.requestPermissions(activity, permissions, 1);
            return false;
        }
        return true;
    }
}
