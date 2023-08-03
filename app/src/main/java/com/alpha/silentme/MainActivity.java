package com.alpha.silentme;

import android.Manifest;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.alpha.silentme.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private MapView mapView;
    private GoogleMap googleMap;
    private GeofencingClient geofencingClient;
    private Marker marker;
    private Circle circle;
    private FusedLocationProviderClient fusedLocationClient;
    private LatLng centerLatLng; // Declare centerLatLng as an instance variable
    private float radius;

    private static final int RINGER_MODE_VIBRATE = AudioManager.RINGER_MODE_VIBRATE;
    private static final int RINGER_MODE_NORMAL = AudioManager.RINGER_MODE_NORMAL;
    private static final float GEOFENCE_RADIUS = 100;
    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 123;
    private static final int PERMISSIONS_REQUEST_BACKGROUND_LOCATION = 456;

    private GeofenceService geofenceService;
    private boolean isBound = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

//        setVolumeToLowest();

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        // Initialize the geofencing client
        geofencingClient = LocationServices.getGeofencingClient(this);

        // Find the ImageButton and set its onClickListener
        ImageButton locationButton = findViewById(R.id.locationButton);
        locationButton.setOnClickListener(v -> onImageButtonClick());

        // Check for background location permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request background location permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                    PERMISSIONS_REQUEST_BACKGROUND_LOCATION);
        } else {
            // Permission is already granted, proceed with setting up the geofence.
            setupGeofence();
        }
    }

    private void onImageButtonClick() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // If the location permission is not granted, request it.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            // If the location permission is already granted, get the current location.
            getCurrentLocation();
        }
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions.
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                        updateMarkerPosition(currentLatLng);
                        moveCameraToLocation(currentLatLng);
                        updateCirclePosition(currentLatLng);
                    } else {
                        Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateMarkerPosition(LatLng newLocation) {

        MySharedPreferences.saveString(this,"Lat",String.valueOf(newLocation.latitude));
        MySharedPreferences.saveString(this,"Lng",String.valueOf(newLocation.longitude));
        if (marker != null) {
            marker.setPosition(newLocation);
        } else {
            // If the marker doesn't exist yet, create it.
            MarkerOptions markerOptions = new MarkerOptions().position(newLocation).title("Marker Title");
            marker = googleMap.addMarker(markerOptions);
            marker.setDraggable(true); // Enable marker dragging
            googleMap.setOnMarkerDragListener(this);
        }
    }

    private void moveCameraToLocation(LatLng location) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location, 17));
    }

    private void setVolumeToLowest() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int lowestVolume = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                lowestVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_RING);
            }
            audioManager.setStreamVolume(AudioManager.STREAM_RING, lowestVolume, 0);

            // Set the device to vibrate mode
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_BACKGROUND_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Background location permission granted, proceed with setting up the geofence.
                setupGeofence();
            } else {
                // Background location permission denied, handle this situation (e.g., display a message or close the app).
            }
        }
    }

    private void setupGeofence() {
        // Add the geofence registration logic here
        // ...
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Check for location permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request location permissions
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_FINE_LOCATION);
        } else {
            // Permissions already granted, show the user's current location
            showUserCurrentLocation();
        }

        String lat = MySharedPreferences.getString(this, "Lat");
        String lng = MySharedPreferences.getString(this, "Lng");

        if (!lat.isEmpty())
        {
            // Add a marker to the desired initial location
            LatLng initialLocation = new LatLng(Float.parseFloat(lat), Float.parseFloat(lng));
            centerLatLng = initialLocation;
            radius = 100;
            MarkerOptions markerOptions = new MarkerOptions().position(initialLocation).title("My Marker");
            marker = googleMap.addMarker(markerOptions);
            marker.setDraggable(true); // Enable marker dragging
            googleMap.setOnMarkerDragListener(this);

            // Move the camera to the initial marker location
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 17));

            // Add a circle radius around the initial marker location
            CircleOptions circleOptions = new CircleOptions()
                    .center(initialLocation)
                    .radius(100) // Radius in meters, change this value as per your requirement
                    .strokeWidth(5f)
                    .strokeColor(Color.RED)
                    .fillColor(Color.parseColor("#30FF0000")); // Transparent red fill

            circle = googleMap.addCircle(circleOptions);

            // Register the geofence with the selected location
            registerGeofence(initialLocation, 100);

            checkGeofenceStatus();
        }

    }

    private void showUserCurrentLocation() {
        // Enable the "My Location" button on the map
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        googleMap.setMyLocationEnabled(true);

//        // Get the user's current location
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(this, location -> {
//                    if (location != null) {
//                        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//                        updateMarkerPosition(currentLatLng);
//                        moveCameraToLocation(currentLatLng);
//                        updateCirclePosition(currentLatLng);
//                        updateSilentMode(currentLatLng);
//                    } else {
//                        Toast.makeText(this, "Failed to get current location", Toast.LENGTH_SHORT).show();
//                    }
//                });
    }

//    private void updateSilentMode(LatLng currentLatLng) {
//        String lat = MySharedPreferences.getString(this, "Lat");
//        String lng = MySharedPreferences.getString(this, "Lng");
//
//        float v = Float.parseFloat(lat);
//        float v1 = Float.parseFloat(lng);
//
//        float[] distance = new float[1];
//        Location.distanceBetween(currentLatLng.latitude, currentLatLng.longitude,
//                v, v1, distance);
//
////        Log.e("NewLogic",distance[0]+"---");
//        if (distance[0] > radius) {
//            // New location is outside the geofence radius, change to normal mode
//            setRingerMode(RINGER_MODE_NORMAL);
//        } else {
//            // New location is inside the geofence radius, change to vibrate mode
//            setRingerMode(RINGER_MODE_VIBRATE);
//        }
//
//    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        // This method is called when you start dragging the marker.
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        // This method is called as you drag the marker.
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        LatLng newLocation = marker.getPosition();
        updateGeofence(newLocation);
        updateCirclePosition(newLocation);

        updateMarkerPosition(newLocation);
    }


    private void checkGeofenceStatus() {
        if (googleMap != null && centerLatLng != null) {
            float[] distance = new float[1];
            Location.distanceBetween(centerLatLng.latitude, centerLatLng.longitude,
                    centerLatLng.latitude, centerLatLng.longitude, distance);

            boolean insideGeofence = distance[0] <= radius;

//            // Set the ringer mode based on whether the user is inside or outside the geofence
//            if (insideGeofence) {
//                setRingerMode(RINGER_MODE_VIBRATE); // User is inside the geofence, set to vibrate mode
//            } else {
//                setRingerMode(RINGER_MODE_NORMAL); // User is outside the geofence, set to normal mode
//            }

            // Update the geofence status in the notification if the service is bound
            if (isBound) {
                geofenceService.updateGeofenceStatus(insideGeofence);
            }
        }
    }


    private void setRingerMode(int ringerMode) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(ringerMode);
        }
    }

    private void updateCirclePosition(LatLng centerLatLng) {

        if (googleMap != null && circle != null) {
            circle.setCenter(centerLatLng);
        }
    }

    private void registerGeofence(LatLng centerLatLng, float radius) {
        Geofence geofence = new Geofence.Builder()
                .setRequestId("GEOFENCE_ID")
                .setCircularRegion(centerLatLng.latitude, centerLatLng.longitude, radius)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build();

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .addGeofence(geofence)
                .build();

        // Update PendingIntent to include geofence transition type
        Intent geofenceIntent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofenceIntent.setAction("com.alpha.silentme.ACTION_RECEIVE_GEOFENCE");
        geofenceIntent.putExtra("geofenceTransition", Geofence.GEOFENCE_TRANSITION_EXIT);

        PendingIntent pendingIntent = null;
        int flags;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            // Set the mutability flag for Android S and above
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }

        pendingIntent = PendingIntent.getBroadcast(this, 0, geofenceIntent, flags);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        geofencingClient.addGeofences(request, pendingIntent)
                .addOnSuccessListener(aVoid -> {
                    // Geofence added successfully
                    Toast.makeText(this, "Geofence registered!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to add geofence
                    Toast.makeText(this, "Failed to register geofence", Toast.LENGTH_SHORT).show();
                });
    }

    private void unregisterGeofence() {
        geofencingClient.removeGeofences(getGeofencePendingIntent());
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent geofenceIntent = new Intent(this, GeofenceBroadcastReceiver.class);
        geofenceIntent.setAction("com.alpha.silentme.ACTION_RECEIVE_GEOFENCE"); // Add the action to match the registered receiver
        int flags;

        // Use PendingIntent.getBroadcast for Android versions before Android S (API 31)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            flags = PendingIntent.FLAG_UPDATE_CURRENT;
        } else {
            // Set the mutability flag for Android S and above
            flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;
        }

        return PendingIntent.getBroadcast(this, 0, geofenceIntent, flags);
    }

    private void updateGeofence(LatLng newLocation) {
        unregisterGeofence();
        centerLatLng = newLocation; // Update the centerLatLng with the new location
        registerGeofence(newLocation, GEOFENCE_RADIUS);
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        bindGeofenceService();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
        unbindGeofenceService();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        unbindGeofenceService();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void bindGeofenceService() {
        Intent intent = new Intent(this, GeofenceService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    private void unbindGeofenceService() {
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GeofenceService.GeofenceServiceBinder binder = (GeofenceService.GeofenceServiceBinder) service;
            geofenceService = binder.getService();
            isBound = true;
            checkGeofenceStatus(); // Update geofence status when the service is connected
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
        }
    };

    private void setGeofenceStatus(boolean insideGeofence) {
        // Update your UI or notifications here based on the geofence status
        // For example, you can show a toast message indicating if the device is inside or outside the geofence.
        String message = insideGeofence ? "Inside Geofence" : "Outside Geofence";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
