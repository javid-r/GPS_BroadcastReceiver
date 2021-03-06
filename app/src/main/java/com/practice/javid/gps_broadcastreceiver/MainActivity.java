package com.practice.javid.gps_broadcastreceiver;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private final int permissionRequestCode = 1;

    private ToneGenerator toneG;

    private AppCompatActivity context;
    private TextView distance;
    private TextView originLatitude;
    private TextView originLongitude;
    private TextView originAltitude;
    private TextView destinationLatitude;
    private TextView destinationLongitude;
    private TextView destinationAltitude;

    private Location originLocation;
    private Location destinationLocation;
    private GPS_BroadcastReceiver onLocationChangedReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;
        distance = (TextView) findViewById(R.id.txt_distance);
        originLatitude = (TextView) findViewById(R.id.txt_origin_lat);
        originLongitude = (TextView) findViewById(R.id.txt_origin_lon);
        originAltitude = (TextView) findViewById(R.id.txt_origin_alt);
        destinationLatitude = (TextView) findViewById(R.id.txt_destination_lat);
        destinationLongitude = (TextView) findViewById(R.id.txt_destination_lon);
        destinationAltitude = (TextView) findViewById(R.id.txt_destination_alt);
        toneG = new ToneGenerator(AudioManager.STREAM_DTMF, 200);

        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                startService(new Intent(context, GPS_Service.class));
            } else {
                ActivityCompat.requestPermissions(context, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, permissionRequestCode);
            }
        } else {
            startService(new Intent(context, GPS_Service.class));
        }


        onLocationChangedReceiver = new GPS_BroadcastReceiver();

        IntentFilter LocationChangedIntentFilter = new IntentFilter();
        LocationChangedIntentFilter.addAction("Location_Changed");
        registerReceiver(onLocationChangedReceiver, LocationChangedIntentFilter);

        Button getOriginButton = (Button) findViewById(R.id.btn_origin);
        getOriginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                originLocation = onLocationChangedReceiver.getLocation();
                setOrigin(originLocation);
                beep();

            }
        });

        Button getDestinationButton = (Button) findViewById(R.id.btn_destination);
        getDestinationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (originLocation != null) {
                    destinationLocation = onLocationChangedReceiver.getLocation();//location;
                    if (destinationLocation != null) {
                        setDestination(destinationLocation);
                        setDistance(originLocation, destinationLocation);
                        beep();
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
        unregisterReceiver(onLocationChangedReceiver);
        stopService(new Intent(context, GPS_Service.class));
        super.onDestroy();
    }

    private void setOrigin(Location origin) {
        double latitude = origin.getLatitude();
        double longitude = origin.getLongitude();
        double altitude = origin.getAltitude();

        originLatitude.setText(String.valueOf(latitude));
        originLongitude.setText(String.valueOf(longitude));
        originAltitude.setText(String.valueOf(altitude));

    }

    private void setDestination(Location destination) {
        double latitude = destination.getLatitude();
        double longitude = destination.getLongitude();
        double altitude = destination.getAltitude();

        destinationLatitude.setText(String.valueOf(latitude));
        destinationLongitude.setText(String.valueOf(longitude));
        destinationAltitude.setText(String.valueOf(altitude));
    }

    private void setDistance(Location origin, Location destination) {
        double destinationDouble = destination.distanceTo(origin);
        String distanceString = String.format(
                getResources().getString(R.string.distance_value),
                destinationDouble);
        distance.setText(distanceString);
    }

    private void beep() {
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 280);
    }
}
