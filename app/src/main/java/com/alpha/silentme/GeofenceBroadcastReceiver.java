package com.alpha.silentme;
import static android.media.AudioManager.RINGER_MODE_NORMAL;
import static android.media.AudioManager.RINGER_MODE_VIBRATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;
public class GeofenceBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null && intent.getAction() != null) {
            if (intent.getAction().equals("com.alpha.silentme.ACTION_RECEIVE_GEOFENCE")) {
                int geofenceTransition = intent.getIntExtra("geofenceTransition", -1);
                if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
                    // Device entered the geofence radius
                    setRingerMode(context, RINGER_MODE_VIBRATE);
                } else if (geofenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT) {
                    // Device exited the geofence radius
                    setRingerMode(context, RINGER_MODE_NORMAL);
                }
            }
        }
    }

    private void setRingerMode(Context context, int ringerMode) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            audioManager.setRingerMode(ringerMode);
        }
    }
}
