package com.alpha.silentme;

import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.IBinder;
import android.os.IBinder;
import android.os.Binder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

public class GeofenceService extends Service {

    private static final int NOTIFICATION_ID = 123;
    private static final String NOTIFICATION_CHANNEL_ID = "GeofenceChannel";
    private static final String GEOFENCE_STATUS_ENTERED = "Inside the geofence area";
    private static final String GEOFENCE_STATUS_EXITED = "Outside the geofence area";

    private final IBinder binder = new GeofenceServiceBinder();

    private FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Start the service as a foreground service
        startForeground(NOTIFICATION_ID, createNotification(GEOFENCE_STATUS_EXITED));
        return START_STICKY;
    }


    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,
                locationCallback,
                Looper.getMainLooper());
    }

    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    public void onCreate() {
        super.onCreate();


        // Initialize FusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Configure location request using builder pattern
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 100)
                .setWaitForAccurateLocation(false)
                .setMinUpdateIntervalMillis(2000)
                .setMaxUpdateDelayMillis(100)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    // Update UI with location data
                    Location lastLocation = locationResult.getLastLocation();
                    String lat = MySharedPreferences.getString(getApplicationContext(), "Lat");
                    String lng = MySharedPreferences.getString(getApplicationContext(), "Lng");


                    Log.e("SettLocation:",lat+"---"+lng);
                    float v = Float.parseFloat(lat);
                    float v1 = Float.parseFloat(lng);

                    float[] distance = new float[1];
                    Location.distanceBetween(lastLocation.getLatitude(), lastLocation.getLongitude(),
                            v, v1, distance);

                    Log.e("NewLogic",distance[0]+"---");
                    if (distance[0] > 100) {
                        // New location is outside the geofence radius, change to normal mode
                        setRingerMode(RINGER_MODE_NORMAL);
                    } else {
                        // New location is inside the geofence radius, change to vibrate mode
                        setRingerMode(RINGER_MODE_VIBRATE);
                    }

                    // ...
                }
            }
        };


        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private void setRingerMode(int ringerMode) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(ringerMode);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class GeofenceServiceBinder extends Binder {
        GeofenceService getService() {
            return GeofenceService.this;
        }
    }

    private Notification createNotification(String geofenceStatus) {
        // Create a notification channel for Android Oreo and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Geofence Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        // Create the notification
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Geofence Service")
                .setContentText(geofenceStatus)
                .setSmallIcon(R.drawable.user)
                .setColor(Color.BLUE) // Set the notification color to blue (optional)
                .setAutoCancel(false);

        // Set the notification as ongoing, so it cannot be swiped away by the user
        notificationBuilder.setOngoing(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        int flags;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            // Set the mutability flag for Android S and above
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, flags);
        notificationBuilder.setContentIntent(pendingIntent);

        return notificationBuilder.build();
    }

    // Call this method to update the geofence status on the notification
    public void updateGeofenceStatus(boolean insideGeofence) {
        String geofenceStatus = insideGeofence ? GEOFENCE_STATUS_ENTERED : GEOFENCE_STATUS_EXITED;
        Notification notification = createNotification(geofenceStatus);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.notify(NOTIFICATION_ID, notification);
    }
}
