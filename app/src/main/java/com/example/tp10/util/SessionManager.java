package com.example.tp10.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TP10_Session";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_FULL_NAME = "full_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_DATA_SOURCE = "data_source_mode"; // "SQLITE" or "WEB_SERVICE"
    private static final String KEY_BASE_URL = "base_url";

    public static final String MODE_SQLITE = "SQLITE";
    public static final String MODE_WEB = "WEB_SERVICE";
    public static final String DEFAULT_BASE_URL = "http://10.0.2.2/tp10_api/";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void saveUser(long userId, String username, String fullName) {
        editor.putLong(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_FULL_NAME, fullName);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.apply();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public long getUserId() {
        return pref.getLong(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, "");
    }

    public String getFullName() {
        return pref.getString(KEY_FULL_NAME, "User");
    }

    public void logout() {
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USERNAME);
        editor.remove(KEY_FULL_NAME);
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }

    public void setDataSourceMode(String mode) {
        editor.putString(KEY_DATA_SOURCE, mode);
        editor.apply();
    }

    public String getDataSourceMode() {
        return pref.getString(KEY_DATA_SOURCE, MODE_SQLITE);
    }

    public void setBaseUrl(String url) {
        if (url != null && !url.trim().isEmpty()) {
            if (!url.endsWith("/")) {
                url = url + "/";
            }
            editor.putString(KEY_BASE_URL, url.trim());
            editor.apply();
        }
    }

    public String getBaseUrl() {
        return pref.getString(KEY_BASE_URL, DEFAULT_BASE_URL);
    }
}
