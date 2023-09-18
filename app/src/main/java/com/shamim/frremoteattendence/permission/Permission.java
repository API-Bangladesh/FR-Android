package com.shamim.frremoteattendence.permission;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Permission
{

    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final int REQUEST_STORAGE_PERMISSION = 2;
    private static  final int Location_Request_code=101;
    static LocationRequest mLocationRequest;
    public  static  boolean gpsLocation=false;


    public static void checkCameraPermission_API_LEVEL_30(Activity activity)
    {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {

            checkStoragePermission_API_LEVEL_30(activity);
        }
        else {
            // Request camera permission if not already granted
            requestCameraPermission(activity);
        }

    }

    public static void checkCameraPermission_API_LEVEL_32(Activity activity) {
        // Check if the camera permission is already granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, do camera-related tasks here
            // For example, you can open the camera intent or start using the camera.
//
            checkStoragePermission_API_LEVEL_32(activity);
        } else {
            // Request camera permission if not already granted
            requestCameraPermission(activity);
        }
    }

    private static void requestCameraPermission(Activity activity) {
        // Should we show an explanation for the need for camera permission?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA)) {
            // Show an explanation to the user asynchronously, then request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        } else {
            // No explanation needed, request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CAMERA_PERMISSION);
        }
    }

    private static void checkStoragePermission_API_LEVEL_30(Activity activity)
    {
        // Check if the camera permission is already granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
        {

        } else {
            // Request camera permission if not already granted
            requestStoragePermission_API_LEVEL_30(activity);
        }
    }


    private static void  requestStoragePermission_API_LEVEL_30(Activity activity)
    {
        // Should we show an explanation for the need for camera permission?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Show an explanation to the user asynchronously, then request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // No explanation needed, request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }




    private static   void checkStoragePermission_API_LEVEL_32(Activity activity)
    {
        // Check if the camera permission is already granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_MEDIA_IMAGES)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission already granted, do camera-related tasks here
            // For example, you can open the camera intent or start using the camera.
//            Intent i = new Intent(this, LivePreviewActivity.class);
//            startActivity(i);
//            finish();
        } else {
            // Request camera permission if not already granted
            requestStoragePermission_API_LEVEL_32(activity);
        }
    }





    private static void  requestStoragePermission_API_LEVEL_32(Activity activity)
    {
        // Should we show an explanation for the need for camera permission?
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.READ_MEDIA_IMAGES)) {
            // Show an explanation to the user asynchronously, then request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // No explanation needed, request the permission
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    public static  void gpsPermission(Activity activity)
    {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Location_Request_code);
        }
        else
        {
            CheckGps(activity);
        }
    }

    public static void CheckGps(Activity activity) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(10);
        mLocationRequest.setFastestInterval(1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);

        Task<LocationSettingsResponse> locationSettingsResponseTask = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build());

        locationSettingsResponseTask.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    gpsLocation = true;
                } catch (ApiException e) {
                    if (e.getStatusCode() == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                        ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                        try {
                            resolvableApiException.startResolutionForResult(activity, 101);
                        } catch (IntentSender.SendIntentException sendIntentException) {
                            sendIntentException.printStackTrace();
                        }
                    } else if (e.getStatusCode() == LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE) {
                        Toast.makeText(activity, "Setting not available", Toast.LENGTH_SHORT).show();
                        // Close the activity when GPS settings are unavailable
                        gpsLocation = false;
                        activity.finish();

                    }
                }
            }
        });
    }


    public void handlePermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, handle it here
            } else {
                // Camera permission denied, handle this as needed (e.g., show a message, disable camera features).
            }
        }
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Storage permission granted, handle it here
            } else {
                // Storage permission denied, handle this as needed (e.g., show a message, disable storage features).
            }

        }
        if (requestCode == Location_Request_code)
        {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, handle it here
            } else {
                // Camera permission denied, handle this as needed (e.g., show a message, disable camera features).
            }

        }
    }



}
