package com.shamim.frremoteattendence.LocationService;

import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.IBinder;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class LocationService extends Service {
    public static final String TAG = "LocationService";
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    public static float distanceInMeters;
    public static Location location;
    private static Location specifiedLocation;
    public static double Lat,Long;
    @Override
    public void onCreate() {
        super.onCreate();
        specifiedLocation = new Location("");
        specifiedLocation.setLatitude(23.7963977);
        specifiedLocation.setLongitude(90.4024995);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
                .setIntervalMillis(500).build();
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationAvailability(@NonNull LocationAvailability p0) {
                super.onLocationAvailability(p0);
            }
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                onNewLocation(locationResult);
            }
        };
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        createLocationRequest();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createLocationRequest() {
        try {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Please Give Location Permission", Toast.LENGTH_SHORT).show();
                return;
            }
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeLocationUpdates() {
        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }
        stopForeground(true);
        stopSelf();
    }
    private void onNewLocation(LocationResult locationResult) {
        location = locationResult.getLastLocation();
        EventBus.getDefault().post(new LocationEvent(location.getLatitude(), location.getLongitude()));
        if (location != null) {
            Lat=location.getLatitude();
            Long=location.getLatitude();
            getAreaName(Lat, Long);
        } else {
            Toast.makeText(this, "Please wait change for update Location", Toast.LENGTH_SHORT).show();

        }
    }
    public static boolean checkLocationArea(double latitude, double longitude)
    {
        float[] results = new float[1];
        Location.distanceBetween(
                specifiedLocation.getLatitude(), specifiedLocation.getLongitude(),
                latitude, // Replace with actual latitude
                longitude, // Replace with actual longitude
                results);
        distanceInMeters = results[0];

        return distanceInMeters <= 5;
    }
    private void getAreaName(double latitude, double longitude) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String areaName = address.getSubLocality(); // Get the locality (e.g., city)
                String addressName = address.getAddressLine(0); // Get the full address
                // Use the areaName or addressName as needed
                // For example, display it in a TextView
                Toast.makeText(this, "Area: " + areaName + "\nAddress: " + addressName, Toast.LENGTH_SHORT).show();
                //tvResult.setText("Area: " + areaName + "\nAddress: " + addressName);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle geocoding errors
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeLocationUpdates();
    }
}
