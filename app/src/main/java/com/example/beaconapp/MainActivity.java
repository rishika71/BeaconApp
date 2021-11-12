package com.example.beaconapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bluecats.sdk.BCBeacon;
import com.bluecats.sdk.BCBeaconManager;
import com.bluecats.sdk.BCBeaconManagerCallback;
import com.bluecats.sdk.BCSite;
import com.bluecats.sdk.BlueCatsSDK;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TOKEN = "06e8c088-fae4-419c-aeb6-c56e8def1c42";
    BCBeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BlueCatsSDK.startPurringWithAppToken(getApplicationContext(), TOKEN);

        beaconManager = new BCBeaconManager();
        beaconManager.registerCallback( mBeaconManagerCallback );

    }


    @Override
    protected void onResume()
    {
        super.onResume();

        BlueCatsSDK.didEnterForeground();
        beaconManager.registerCallback( mBeaconManagerCallback );
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        BlueCatsSDK.didEnterBackground();
        beaconManager.unregisterCallback( mBeaconManagerCallback );
    }

    private BCBeaconManagerCallback mBeaconManagerCallback = new BCBeaconManagerCallback()
    {
        @Override
        public void didRangeBlueCatsBeacons( final List<BCBeacon> beacons ) {}

    };

}