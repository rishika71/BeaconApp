/*
 * Copyright (c) 2017 BlueCats. All rights reserved.
 * http://www.bluecats.com
 */

package com.example.beaconapp;

import android.Manifest.permission;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bluecats.sdk.BlueCatsSDK;

public class ApplicationPermissions {
    public static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1001;
    public static final int REQUEST_CODE_LOCATION_PERMISSIONS = 1002;

    private Activity mActivity;

    public ApplicationPermissions(Activity activity) {
        mActivity = activity;
    }

    public void verifyPermissions() {
        if (!BlueCatsSDK.isBluetoothEnabled()) {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(intent, REQUEST_CODE_ENABLE_BLUETOOTH);
        } else if (!locationPermissionsEnabled()) {
            ActivityCompat.requestPermissions(mActivity, new String[] { permission.ACCESS_FINE_LOCATION, permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_LOCATION_PERMISSIONS);
        } else if (!BlueCatsSDK.isLocationAuthorized(mActivity)) {
            showLocationServicesAlert();
        } else if( !BlueCatsSDK.isNetworkReachable(mActivity) ) {
            Toast.makeText(mActivity, "Enable network please!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean locationPermissionsEnabled() {
        return ContextCompat.checkSelfPermission(mActivity, permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(mActivity, permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void showLocationServicesAlert() {
        new AlertDialog.Builder(mActivity, android.R.style.Theme_Material_Light_Dialog_Alert)
                .setMessage("This app requires Location Services to run. Would you like to enable Location Services now?")
                .setPositiveButton("Yes", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        mActivity.startActivity(intent);
                    }
                })
                .setNegativeButton("No", cancelClickListener)
                .create()
                .show();
    }

    private OnClickListener cancelClickListener = (dialog, which) -> dialog.cancel();

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults){
        if (requestCode == REQUEST_CODE_LOCATION_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                verifyPermissions();
            }
        }
    }
}