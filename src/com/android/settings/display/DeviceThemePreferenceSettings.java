/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.display;

import static android.os.UserHandle.USER_SYSTEM;

import android.app.UiModeManager;
import android.content.Context;
import android.content.om.IOverlayManager;
import android.content.om.OverlayInfo;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import android.util.Log;

public class DeviceThemePreferenceSettings {

    private Context mContext;
    private UiModeManager mUiModeManager;
    private IOverlayManager overlayManager;

    public DeviceThemePreferenceSettings(Context context) {
        mContext = context;
        mUiModeManager = context.getSystemService(UiModeManager.class);
        overlayManager = IOverlayManager.Stub
                .asInterface(ServiceManager.getService(Context.OVERLAY_SERVICE));
    }

    public static final String TAG = "DeviceThemePreferenceSettings";

    public static final int DEVICE_THEME_LIGHT = 1;
    public static final int DEVICE_THEME_DARK = 2;
    public static final int DEVICE_THEME_BLACK = 3;
    public static final int DEVICE_THEME_EXTENDED = 4;

    public static final String[] BLACK_THEMES = {
            "com.android.theme.pitchblack.system",
            "com.android.theme.pitchblack.systemui",
    };

    public static final String[] EXTENDED_THEMES = {
            "com.android.theme.extendedui.system",
            "com.android.theme.extendedui.systemui",
    };

    public String[] getTheme(int theme) {
        switch (theme) {
            case DEVICE_THEME_LIGHT:
            case DEVICE_THEME_DARK:
            case DEVICE_THEME_BLACK:
                return BLACK_THEMES;
            case DEVICE_THEME_EXTENDED:
                return EXTENDED_THEMES;
        }
        return null;
    }

    public void setTheme(int theme) {
	    int mCurrentTheme = Settings.System.getIntForUser(mContext.getContentResolver(),
                Settings.System.SYSTEM_THEME_STYLE, 0, USER_SYSTEM);

        if (theme != mCurrentTheme) {
            setEnabled(getTheme(mCurrentTheme), false);
        } else if (theme == mCurrentTheme) {
            return;
        }

        if (theme == DEVICE_THEME_LIGHT) {
            mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_NO);
        }
        else if(theme == DEVICE_THEME_DARK) {
            mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
        }
        else {
            mUiModeManager.setNightMode(UiModeManager.MODE_NIGHT_YES);
            setEnabled(getTheme(theme), true);
        }

        Settings.System.putInt(mContext.getContentResolver(), Settings.System.SYSTEM_THEME_STYLE, theme);
    }

    public void setEnabled(String[] themes, boolean enabled) {
        if (themes == null) return;
        for (String theme : themes) {
            try {
                overlayManager.setEnabled(theme, enabled, USER_SYSTEM);
            } catch (RemoteException e) {
                Log.e(TAG, "Can't change theme", e);
            }
        }
    }
}