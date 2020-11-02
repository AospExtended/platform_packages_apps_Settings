/*
 * Copyright (C) 2020 The AospExtended Project
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

package com.android.settings.aex;

import android.app.Activity;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.suggestions.SuggestionFeatureProvider;
import com.android.settings.overlay.FeatureFactory;
import com.android.settings.overlay.SupportFeatureProvider;

import org.aospextended.extensions.fragments.Customisation;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;

public class CustomisationSuggestionActivity extends Activity {

    private static final String TAG = "CustomisationSugg";

    static final String PREF_KEY_SUGGGESTION_COMPLETE =
            "pref_customisation_suggestion_complete";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final SuggestionFeatureProvider featureProvider = FeatureFactory.getFactory(this)
                 .getSuggestionFeatureProvider(this);
        final SharedPreferences prefs = featureProvider.getSharedPrefs(this);
        prefs.edit().putBoolean(PREF_KEY_SUGGGESTION_COMPLETE, true).commit();
        launchFrag();
        finish();
    }

    public static boolean isSuggestionComplete(Context context) {
        return hasLaunchedBefore(context);
    }

    private static boolean hasLaunchedBefore(Context context) {
        final SuggestionFeatureProvider featureProvider = FeatureFactory.getFactory(context)
                .getSuggestionFeatureProvider(context);
        final SharedPreferences prefs = featureProvider.getSharedPrefs(context);
        return prefs.getBoolean(PREF_KEY_SUGGGESTION_COMPLETE, false);
    }

    public void launchFrag() {
        new SubSettingLauncher(this)
                .setDestination(Customisation.class.getName())
                .setTitleRes(R.string.customisation_title)
                .setSourceMetricsCategory(SettingsEnums.DASHBOARD_SUMMARY)
                .addFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT)
                .launch();
    }
}
