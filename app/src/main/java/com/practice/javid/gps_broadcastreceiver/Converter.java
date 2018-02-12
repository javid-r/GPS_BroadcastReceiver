package com.practice.javid.gps_broadcastreceiver;

import android.location.Location;
import android.location.LocationManager;


public class Converter {

    public Location stringToLocation(String lla) {
        String[] params = lla.split(",");
        Location location = null;

        if (params.length <= 3) {
            double latitude = Double.parseDouble(params[0]);
            double longitude = Double.parseDouble(params[1]);
            double altitude = Double.parseDouble(params[2]);

            location = new Location(LocationManager.GPS_PROVIDER);
            location.setLatitude(latitude);
            location.setLongitude(longitude);
            location.setAltitude(altitude);
        }

        return location;
    }

    public Location doubleToLocation(double latitude, double longitude, double altitude) {

        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);


        return location;
    }
}
