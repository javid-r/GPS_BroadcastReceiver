package com.practice.javid.gps_broadcastreceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;


public class GPS_BroadcastReceiver extends BroadcastReceiver {
    private Location location;
    private Converter converter;


    public GPS_BroadcastReceiver(Location location) {
        this.location = location;
        this.converter = new Converter();
    }

    public GPS_BroadcastReceiver() {
        this.location = new Location(LocationManager.GPS_PROVIDER);
        this.converter = new Converter();
    }

    public Location getLocation() {
        return this.location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }



    @Override
    public void onReceive(Context context, Intent intent) {

        try {
            String llt = intent.getExtras().getString("location");
            setLocation(converter.stringToLocation(llt));

        } catch (Exception e) {
            Log.e("My Code", e.getMessage());
        }
    }
}
