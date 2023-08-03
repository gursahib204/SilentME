package com.alpha.silentme;

import android.content.Context;
import android.content.SharedPreferences;

public class MySharedPreferences {

    private static final String PREFS_NAME = "MyPrefs";

    // Method to store a string in SharedPreferences
    public static void saveString(Context context,String Key, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(Key, value);
        editor.apply(); // or editor.commit() if you want to block until the write is complete
    }

    // Method to retrieve the stored string from SharedPreferences
    public static String getString(Context context,String Key) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getString(Key, "");
        // The second parameter ("" in this case) is the default value returned if the key is not found.
    }
}
