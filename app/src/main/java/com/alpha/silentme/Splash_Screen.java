package com.alpha.silentme;



import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;


public class Splash_Screen extends AppCompatActivity {


    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

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
