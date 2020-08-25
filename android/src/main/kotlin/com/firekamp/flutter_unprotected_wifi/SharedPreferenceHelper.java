package com.firekamp.flutter_unprotected_wifi;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceHelper {
    final private SharedPreferences preferences;
    final private SharedPreferences.Editor editor;
    final private String CONFIGURATION_KEY = "com.firekamp.flutter_unprotected_wifi.configuration";
    final private String LAST_CONNECTION_KEY = "com.firekamp.flutter_unprotected_wifi.lastconnection";


    SharedPreferenceHelper(Context context) {
        preferences = context.getSharedPreferences("UnProtectedWiFi", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }


    void SaveConfiguration(String value) {
        editor.putString(CONFIGURATION_KEY, value);
        editor.commit();
    }

    String GetConfiguration() {
        return preferences.getString(CONFIGURATION_KEY, null);
    }

    boolean GetLastConnectionStatus() {
        return preferences.getBoolean(LAST_CONNECTION_KEY, false);
    }

    void SaveLastConnectionStatus(boolean value) {
        editor.putBoolean(LAST_CONNECTION_KEY, value);
        editor.commit();
    }

    void clearConfiguration() {
        editor.putString(CONFIGURATION_KEY, null);
        editor.commit();
    }
}

