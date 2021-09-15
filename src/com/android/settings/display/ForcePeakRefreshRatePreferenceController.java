/*
 * Copyright (C) 2020 The Android Open Source Project
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

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Display;

import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settings.core.TogglePreferenceController;

public class ForcePeakRefreshRatePreferenceController extends TogglePreferenceController {

    private static final String TAG = "ForcePeakRefreshRateCtr";
    private static final boolean DEBUG = false;
    private static float DEFAULT_REFRESH_RATE = 60f;
    private static float NO_CONFIG = 0f;

    private float mPeakRefreshRate;

    public ForcePeakRefreshRatePreferenceController(Context context, String key) {
        super(context, key);

        final DisplayManager dm = context.getSystemService(DisplayManager.class);
        final Display display = dm.getDisplay(Display.DEFAULT_DISPLAY);

        if (display == null) {
            Log.w(TAG, "No valid default display device");
            mPeakRefreshRate = DEFAULT_REFRESH_RATE;
        } else {
            mPeakRefreshRate = findPeakRefreshRate(display.getSupportedModes());
        }

        if (DEBUG) Log.d(TAG, "DEFAULT_REFRESH_RATE : " + DEFAULT_REFRESH_RATE
            + " mPeakRefreshRate : " + mPeakRefreshRate);
    }

    @Override
    public boolean isChecked() {
        final float peakRefreshRate = Settings.System.getFloat(mContext.getContentResolver(),
            Settings.System.MIN_REFRESH_RATE, NO_CONFIG);
        return peakRefreshRate >= mPeakRefreshRate;
    }

    @Override
    public boolean setChecked(boolean isChecked) {
        final float peakRefreshRate = isChecked ? mPeakRefreshRate : NO_CONFIG;
        return Settings.System.putFloat(mContext.getContentResolver(),
            Settings.System.MIN_REFRESH_RATE, peakRefreshRate);
    }

    @Override
    public int getAvailabilityStatus() {
        if (mContext.getResources().getBoolean(R.bool.config_show_smooth_display)) {
            return mPeakRefreshRate > DEFAULT_REFRESH_RATE ? AVAILABLE : UNSUPPORTED_ON_DEVICE;
        } else {
            return UNSUPPORTED_ON_DEVICE;
        }
    }

    private float findPeakRefreshRate(Display.Mode[] modes) {
        float peakRefreshRate = DEFAULT_REFRESH_RATE;
        for (Display.Mode mode : modes) {
            if (Math.round(mode.getRefreshRate()) > peakRefreshRate) {
                peakRefreshRate = mode.getRefreshRate();
            }
        }

        return peakRefreshRate;
    }
}
