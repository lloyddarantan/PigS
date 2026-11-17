package com.example.pigtech;

import android.content.Context;

public class ApiConfig {
    public static final String BASE_URL = "http://10.0.2.2/pigsensor_api/";

    private static final String PREFS_NAME = "PigTechPrefs";
    private static final String KEY_TOKEN  = "token";

    private static final String DEBUG_TOKEN = "a1b2c3d4e5f60718293a4b5c6d7e8f90a1b2c3d4e5f60718293a4b5c6d7e8f90";

    public static String getToken(Context context) {
        String saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, "");
        return saved.isEmpty() ? DEBUG_TOKEN : saved;
    }
}
