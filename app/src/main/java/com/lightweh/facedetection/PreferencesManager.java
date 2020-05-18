package com.lightweh.facedetection;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREF_NAME = "FaceDetect";
    private static final String KEY_DEVICE_NAME = "DEVICE_NAME";
    private static final String KEY_TEST_MODE = "TEST_MODE";

    private static PreferencesManager sInstance;
    private final SharedPreferences mPref;

    private PreferencesManager(Context context) {
        mPref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized void initializeInstance(Context context) {
        if (sInstance == null) {
            sInstance = new PreferencesManager(context);
        }
    }

    public static synchronized PreferencesManager getInstance() {
        if (sInstance == null) {
            throw new IllegalStateException(PreferencesManager.class.getSimpleName() +
                    " is not initialized, call initializeInstance(..) method first.");
        }
        return sInstance;
    }

    public void setTestMode(Boolean value) {
        mPref.edit()
                .putBoolean(KEY_TEST_MODE, value)
                .commit();
    }

    public Boolean getTestMode() {
        return mPref.getBoolean(KEY_TEST_MODE, false);
    }

    public void removeTestMode() {
        remove(KEY_TEST_MODE);
    }

    public void setDeviceName(String value) {
        mPref.edit()
                .putString(KEY_DEVICE_NAME, value)
                .commit();
    }

    public String getDeviceName() {
        return mPref.getString(KEY_DEVICE_NAME, "");
    }

    public void removeDeviceName() {
        remove(KEY_DEVICE_NAME);
    }

    public void remove(String key) {
        mPref.edit()
                .remove(key)
                .commit();
    }

    public boolean clear() {
        return mPref.edit()
                .clear()
                .commit();
    }
}
