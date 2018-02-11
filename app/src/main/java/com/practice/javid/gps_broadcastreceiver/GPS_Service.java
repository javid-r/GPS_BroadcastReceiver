package com.practice.javid.gps_broadcastreceiver;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

public class GPS_Service extends Service implements android.location.LocationListener {

    private Context context;
    private LocationListener locationListener;
    private LocationManager locationManager;

    @Override
    public void onCreate() {
        context = GPS_Service.this;
        locationListener = GPS_Service.this;
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            if (Build.VERSION.SDK_INT >= 23) {

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
                }

            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
            }
        } catch (Exception e) {
            Log.e("My Code: ", "class -> GPS_Service in method -> onStartCommand -> " + e.toString());
        }

        return START_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {

        Context context = this;
        try {
            if (location == null) {
                return;
            }
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("Location_Changed");
            broadcastIntent.putExtra("location", location.getLatitude() + "," + location.getLongitude() + "," + location.getAltitude());
            context.sendBroadcast(broadcastIntent);
        } catch (Exception e) {
            Log.e("My code", e.getMessage());
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        try {
            locationManager.removeUpdates(locationListener);
        } catch (Exception e) {
            Log.e("My Code: ", "class -> GPS_Service in method -> onDestroy -> " + e.toString());
        }
        super.onDestroy();
    }
}
