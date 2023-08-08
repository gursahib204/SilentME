package com.alpha.silentme;



import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;



public class Splash_Screen extends AppCompatActivity {


    private static final int SPLASH_DURATION = 2000; // 2 seconds
    private static final int LOCATION_PERMISSION_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Check and request location permission
        if (checkAndRequestLocationPermission()) {
            proceedToNextActivity();
        }
    }

    private boolean checkAndRequestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                proceedToNextActivity();
            }
        }
    }

    private void proceedToNextActivity() {
        // Handler to delay the intent to another activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an intent to start the target activity
                Intent intent = new Intent(Splash_Screen.this, LoginActivity.class);
                startActivity(intent);

                // Finish this activity, so the user cannot go back to the splash screen
                finish();
            }
        }, SPLASH_DURATION);
    }
}














       /* // Handler to delay the intent to another activity
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Create an intent to start the target activity
                Intent intent = new Intent(Splash_Screen.this, LoginActivity.class);
                startActivity(intent);

                // Finish this activity, so the user cannot go back to the splash screen
                finish();
            }
        }, SPLASH_DURATION);
    }
}
*/