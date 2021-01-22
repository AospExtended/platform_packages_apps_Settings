/*
 * Copyright (C) 2019 RevengeOS
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

package com.android.settings.fuelgauge.smartcharging;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.provider.Settings;
import androidx.preference.Preference;
import androidx.preference.Preference.OnPreferenceChangeListener;

import com.android.internal.logging.nano.MetricsProto.MetricsEvent;
import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.core.instrumentation.MetricsFeatureProvider;
import com.android.settingslib.core.lifecycle.Lifecycle;

import org.aospextended.extensions.preference.CustomSeekBarPreference;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import vendor.aosp.smartcharge.V1_0.ISmartCharge;

/**
 * Settings screen for Smart charging
 */
public class SmartChargingSettings extends DashboardFragment implements OnPreferenceChangeListener {

    private static final String TAG = "SmartChargingSettings";
    private static final String KEY_SMART_CHARGING_LEVEL = "smart_charging_level";
    private static final String KEY_SMART_CHARGING_RESUME_LEVEL = "smart_charging_resume_level";
    private CustomSeekBarPreference mSmartChargingLevel;
    private CustomSeekBarPreference mSmartChargingResumeLevel;

    private ISmartCharge mSmartCharge;
    private int mSmartChargingLevelDefaultConfig = 85;
    private int mSmartChargingResumeLevelDefaultConfig = 80;
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        mSmartCharge = getSmartCharge();
        try {
            mSmartChargingLevelDefaultConfig = mSmartCharge.getSuspendLevel();
            mSmartChargingResumeLevelDefaultConfig = mSmartCharge.getResumeLevel();
        } catch (RemoteException ex) { }
        mSmartChargingLevel = (CustomSeekBarPreference) findPreference(KEY_SMART_CHARGING_LEVEL);
        int currentLevel = Settings.System.getInt(getContentResolver(),
            Settings.System.SMART_CHARGING_LEVEL, mSmartChargingLevelDefaultConfig);
        mSmartChargingLevel.setValue(currentLevel);
        mSmartChargingLevel.setOnPreferenceChangeListener(this);
        mSmartChargingResumeLevel = (CustomSeekBarPreference) findPreference(KEY_SMART_CHARGING_RESUME_LEVEL);
        int currentResumeLevel = Settings.System.getInt(getContentResolver(),
            Settings.System.SMART_CHARGING_RESUME_LEVEL, mSmartChargingResumeLevelDefaultConfig);
        mSmartChargingResumeLevel.setMax(currentLevel - 1);
        if (currentResumeLevel >= currentLevel) currentResumeLevel = currentLevel -1;
        mSmartChargingResumeLevel.setValue(currentResumeLevel);
        mSmartChargingResumeLevel.setOnPreferenceChangeListener(this);
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.smart_charging;
    }

    @Override
    public int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        int resumeLevel = Settings.System.getInt(getContentResolver(),
                 Settings.System.SMART_CHARGING_RESUME_LEVEL, mSmartChargingResumeLevelDefaultConfig);
        int suspendLevel = Settings.System.getInt(getContentResolver(),
                 Settings.System.SMART_CHARGING_LEVEL, mSmartChargingLevelDefaultConfig);

        if (preference == mSmartChargingLevel) {
            suspendLevel = (Integer) objValue;
            Settings.System.putInt(getContentResolver(),
                    Settings.System.SMART_CHARGING_LEVEL, suspendLevel);
            mSmartChargingResumeLevel.setMax(suspendLevel - 1);
            if (suspendLevel <= resumeLevel) {
                resumeLevel--;
                mSmartChargingResumeLevel.setValue(suspendLevel -1);
                Settings.System.putInt(getContentResolver(),
                    Settings.System.SMART_CHARGING_RESUME_LEVEL, resumeLevel);
            }
            try {
                mSmartCharge.updateBatteryLevels(suspendLevel, resumeLevel);
            } catch (RemoteException ex) { }
            return true;
        } else if (preference == mSmartChargingResumeLevel) {
            resumeLevel = (Integer) objValue;
            mSmartChargingResumeLevel.setMax(suspendLevel - 1);
            Settings.System.putInt(getContentResolver(),
                Settings.System.SMART_CHARGING_RESUME_LEVEL, resumeLevel);
            try {
                mSmartCharge.updateBatteryLevels(suspendLevel, resumeLevel);
            } catch (RemoteException ex) { }
            return true;
        } else {
            return false;
        }
    }

    private synchronized ISmartCharge getSmartCharge() {
        try {
            return ISmartCharge.getService();
        } catch (RemoteException ex) {
            ex.printStackTrace();
        } catch (NoSuchElementException ex) {
            // service not available
        }

        return null;
    }
}
