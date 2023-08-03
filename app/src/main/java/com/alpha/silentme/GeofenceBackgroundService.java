package com.alpha.silentme;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

public class GeofenceBackgroundService extends IntentService {

    public GeofenceBackgroundService() {
        super("GeofenceBackgroundService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofenceBroadcastReceiver receiver = new GeofenceBroadcastReceiver();
        receiver.onReceive(this, intent);
    }

    private void setVolumeToLowest(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int lowestVolume = audioManager.getStreamMinVolume(AudioManager.STREAM_RING);
            audioManager.setStreamVolume(AudioManager.STREAM_RING, lowestVolume, 0);

            // Set the device to vibrate mode
            audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        }
    }
}
