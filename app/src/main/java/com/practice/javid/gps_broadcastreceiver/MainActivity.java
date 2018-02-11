package com.practice.javid.gps_broadcastreceiver;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 1;

    private AppCompatActivity context;
    private TextView distance;
    private TextView originLlatitude;
    private TextView originLlongitude;
    private TextView originLaltitude;
    private TextView destinationlatitude;
    private TextView destinationlongitude;
    private TextView destinationaltitude;



    private Location location;
    private Location originLocation;
    private Location destinationLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;

        distance = (TextView) findViewById(R.id.txt_distance);
        originLlatitude = (TextView) findViewById(R.id.txt_origin_lat);
        originLlongitude = (TextView) findViewById(R.id.txt_origin_lon);
        originLaltitude = (TextView) findViewById(R.id.txt_origin_alt);
        destinationlatitude = (TextView) findViewById(R.id.txt_destination_lat);
        destinationlongitude = (TextView) findViewById(R.id.txt_destination_lon);
        destinationaltitude = (TextView) findViewById(R.id.txt_destination_alt);


        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(context, GPS_Service.class));
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            }
        } else {
            startService(new Intent(context, GPS_Service.class));
        }


        BroadcastReceiver onLocationChangedReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    location = convertToLocation(intent.getExtras().getString("location"));
                } catch (Exception e) {
                    Log.e("My Code", e.getMessage());
                }
            }
        };


        IntentFilter LocationChangedIntentFilter = new IntentFilter();
        LocationChangedIntentFilter.addAction("Location_Changed");
        registerReceiver(onLocationChangedReceiver, LocationChangedIntentFilter);

        Button getOriginButton = (Button) findViewById(R.id.btn_origin);
        getOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originLocation = location;
                setOrigin(originLocation);
            }
        });

        Button getDestinationButton = (Button) findViewById(R.id.btn_destination);
        getDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation != null) {
                    destinationLocation = location;
                    if (destinationLocation != null) {
                        setDestination(destinationLocation);
                        setDistance(originLocation, destinationLocation);
                    }
                } else {
                    Toast.makeText(context, "Please set origin first", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == permissionRequestCode) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    startService(new Intent(context, GPS_Service.class));
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        stopService(new Intent(context, GPS_Service.class));
        super.onDestroy();
    }

    private Location convertToLocation(String lla) {
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

    private void setOrigin(Location origin) {
        double latitude = origin.getLatitude();
        double longitude = origin.getLongitude();
        double altitude = origin.getAltitude();

        originLlatitude.setText(String.valueOf(latitude));
        originLlongitude.setText(String.valueOf(longitude));
        originLaltitude.setText(String.valueOf(altitude));

    }

    private void setDestination(Location destination){
        double latitude = destination.getLatitude();
        double longitude = destination.getLongitude();
        double altitude = destination.getAltitude();

        destinationlatitude.setText(String.valueOf(latitude));
        destinationlongitude.setText(String.valueOf(longitude));
        destinationaltitude.setText(String.valueOf(altitude));
    }

    private void setDistance(Location origin, Location destination){
        double destinationDouble = destination.distanceTo(origin);
        String distanceString = String.format(getResources().getString(R.string.distance_value), destinationDouble);
        distance.setText(distanceString);
    }
}
