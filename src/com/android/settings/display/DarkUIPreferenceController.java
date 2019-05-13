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
 * limitations under the License
 */

package com.android.settings.display;

import android.app.UiModeManager;
import android.content.Context;
import android.provider.Settings;
import android.support.annotation.VisibleForTesting;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;

import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.lifecycle.Lifecycle;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnResume;

import com.android.settings.R;
import com.android.settings.core.PreferenceControllerMixin;

import static com.android.settings.display.ThemeUtils.isSubstratumOverlayInstalled;

public class DarkUIPreferenceController extends AbstractPreferenceController
        implements Preference.OnPreferenceChangeListener, PreferenceControllerMixin, LifecycleObserver, OnResume {

    private static final String DARK_UI_KEY = "dark_ui_mode";
    private final UiModeManager mUiModeManager;
    private ListPreference mSystemThemeStyle;

    public DarkUIPreferenceController(Context context, Lifecycle lifecycle) {
        super(context);
        mUiModeManager = context.getSystemService(UiModeManager.class);
        if (lifecycle != null) {
            lifecycle.addObserver(this);
        }
    }

    @VisibleForTesting
    DarkUIPreferenceController(Context context, UiModeManager uiModeManager) {
        super(context);
        mUiModeManager = uiModeManager;
    }

    @Override
    public String getPreferenceKey() {
        return DARK_UI_KEY;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mSystemThemeStyle = (ListPreference) screen.findPreference(DARK_UI_KEY);
        updateState();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if(preference == mSystemThemeStyle) {
            mUiModeManager.setNightMode(modeToInt((String) newValue));
            updateSummary();
        }
        return true;
    }

    public void updateState() {
        if (!isSubstratumOverlayInstalled(mContext) || isForceThemeAllowed()) {
            mSystemThemeStyle.setEnabled(true);
            updateSummary();
        } else {
            mSystemThemeStyle.setEnabled(false);
            mSystemThemeStyle.setSummary(R.string.substratum_installed_title);
        }
    }

    private void updateSummary() {
        int mode = mUiModeManager.getNightMode();
        mSystemThemeStyle.setValue(modeToString(mode));
        mSystemThemeStyle.setSummary(modeToDescription(mode));
    }

    private String modeToDescription(int mode) {
        String[] values = mContext.getResources().getStringArray(R.array.dark_ui_mode_entries);
        switch (mode) {
            case UiModeManager.MODE_NIGHT_AUTO:
                return values[0];
            case UiModeManager.MODE_NIGHT_YES:
                return values[1];
            case UiModeManager.MODE_NIGHT_NO:
            default:
                return values[2];

        }
    }

    private String modeToString(int mode) {
        switch (mode) {
            case UiModeManager.MODE_NIGHT_AUTO:
                return "auto";
            case UiModeManager.MODE_NIGHT_YES:
                return "yes";
            case UiModeManager.MODE_NIGHT_NO:
            default:
                return "no";

        }
    }

    private int modeToInt(String mode) {
        switch (mode) {
            case "auto":
                return UiModeManager.MODE_NIGHT_AUTO;
            case "yes":
                return UiModeManager.MODE_NIGHT_YES;
            case "no":
            default:
                return UiModeManager.MODE_NIGHT_NO;
        }
    }

    public boolean isForceThemeAllowed() {
        return Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.FORCE_ALLOW_SYSTEM_THEMES, 0) == 1;
    }

    @Override
    public void onResume() {
        updateState();
    }
}
