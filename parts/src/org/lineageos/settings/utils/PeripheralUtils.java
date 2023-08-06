package org.lineageos.settings.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.UserHandle;
import android.util.Log;

import vendor.xiaomi.hardware.touchfeature.V1_0.ITouchFeature;

import static org.lineageos.settings.stylus.StylusSettingsFragment.SHARED_STYLUS;
import static org.lineageos.settings.keyboard.XiaomiKeyboardSettingsFragment.SHARED_KEYBOARD;
import static org.lineageos.settings.keyboard.XiaomiKeyboardSettingsFragment.KEYBOARDPATH;
import static org.lineageos.settings.keyboard.XiaomiKeyboardSettingsFragment.KEYBOARDEN;
import static org.lineageos.settings.keyboard.XiaomiKeyboardSettingsFragment.KEYBOARDDI;

public class PeripheralUtils {
    private static final String TAG = "PeripheralUtils";
    private static final boolean DEBUG = false;
    private static ITouchFeature mTouchFeature;
    private static SharedPreferences stylus;
    private static SharedPreferences keyboard;

    public static void BootResetState(Context context) {
        if (DEBUG) Log.d(TAG, "Starting service");
        // Initialize stylus and keyboard shared preferences
        stylus = context.getSharedPreferences(SHARED_STYLUS, Context.MODE_PRIVATE);
        keyboard = context.getSharedPreferences(SHARED_KEYBOARD, Context.MODE_PRIVATE);

        // try to get the touchfeature service.
        try {
            mTouchFeature = ITouchFeature.getService();
        } catch (Exception e) {
            Log.e(TAG, "Failed to get touchfeature service", e);
        }

        // Sync all peripherals
        SyncAll();
    }

    // Enable stylus based on shared preference.
    private static void SyncStylus() {
        if (DEBUG) Log.d(TAG, "Enabling stylus");
        if (mTouchFeature != null) {
            try {
                mTouchFeature.setTouchMode(20, stylus.getInt(SHARED_STYLUS, 0));
            } catch (Exception e) {
                Log.e(TAG, "Failed to enable stylus", e);
            }
        }
    }

    // Enable keyboard based on shared preference.
    private static void SyncKeyboard() {
        if (DEBUG) Log.d(TAG, "Enabling keyboard");
        // Write to sysfs to enable/disable keyboard
        if (keyboard.getInt(SHARED_KEYBOARD, 0) == 1) {
            FileUtils.writeLine(KEYBOARDPATH, KEYBOARDEN);
        } else {
            FileUtils.writeLine(KEYBOARDPATH, KEYBOARDDI);
        }
    }

    // Sync all peripherals
    private static void SyncAll() {
        SyncStylus();
        SyncKeyboard();
    }
}
