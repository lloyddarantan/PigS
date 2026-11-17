
package com.example.pigs.network;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionManager {

    private static final String PREFS = "pigs_session";

    private SessionManager() {}

    public static void save(Context context, String token) {
        context.getSharedPreferences(
                        PREFS,
                        Context.MODE_PRIVATE
                ).edit()
                .putString("token", token)
                .apply();
    }

    public static String token(Context context) {
        return context.getSharedPreferences(
                PREFS,
                Context.MODE_PRIVATE
        ).getString("token", "");
    }

    public static void clear(Context context) {
        context.getSharedPreferences(
                        PREFS,
                        Context.MODE_PRIVATE
                ).edit()
                .clear()
                .apply();
    }
}
