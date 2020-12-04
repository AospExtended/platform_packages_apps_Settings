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

package com.android.settings.homepage;

import static com.android.settings.search.actionbar.SearchMenuController.NEED_SEARCH_ICON_IN_ACTION_BAR;
import static com.android.settingslib.search.SearchIndexable.MOBILE;

import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.provider.Settings;

import androidx.core.graphics.ColorUtils;
import androidx.fragment.app.Fragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceFragmentCompat;

import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settings.support.SupportPreferenceController;
import com.android.settingslib.core.instrumentation.Instrumentable;
import com.android.settingslib.search.SearchIndexable;
import com.android.settingslib.widget.AdaptiveIcon;

import com.android.settingslib.widget.AdaptiveIcon;
import com.android.settingslib.widget.AdaptiveIconShapeDrawable;

@SearchIndexable(forTarget = MOBILE)
public class TopLevelSettings extends DashboardFragment implements
        PreferenceFragmentCompat.OnPreferenceStartFragmentCallback {

    private static final String TAG = "TopLevelSettings";

    private int mIconStyle;
    private int mNormalColor;
    private int mAccentColor;
    private int mPrimaryColor;

    private Drawable fg;
    private Drawable bg;

    public TopLevelSettings() {
        final Bundle args = new Bundle();
        // Disable the search icon because this page uses a full search view in actionbar.
        args.putBoolean(NEED_SEARCH_ICON_IN_ACTION_BAR, false);
        setArguments(args);
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.top_level_settings;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    public int getMetricsCategory() {
        return SettingsEnums.DASHBOARD_SUMMARY;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        use(SupportPreferenceController.class).setActivity(getActivity());
    }

    @Override
    public int getHelpResource() {
        // Disable the help icon because this page uses a full search view in actionbar.
        return 0;
    }

    @Override
    public Fragment getCallbackFragment() {
        return this;
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragmentCompat caller, Preference pref) {
        new SubSettingLauncher(getActivity())
                .setDestination(pref.getFragment())
                .setArguments(pref.getExtras())
                .setSourceMetricsCategory(caller instanceof Instrumentable
                        ? ((Instrumentable) caller).getMetricsCategory()
                        : Instrumentable.METRICS_CATEGORY_UNKNOWN)
                .setTitleRes(-1)
                .launch();
        return true;
    }

    @Override
    protected boolean shouldForceRoundedIcon() {
        return getContext().getResources()
                .getBoolean(R.bool.config_force_rounded_icon_TopLevelSettings);
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.top_level_settings) {

                @Override
                protected boolean isPageSearchEnabled(Context context) {
                    // Never searchable, all entries in this page are already indexed elsewhere.
                    return false;
                }
            };

    @Override
    public void onResume() {
        super.onResume();
        updateTheme();
    }

    private void updateTheme() {
        int[] attrs = new int[] {
            android.R.attr.colorControlNormal,
            android.R.attr.colorAccent,
            android.R.attr.colorPrimary,
        };
        TypedArray ta = getContext().getTheme().obtainStyledAttributes(attrs);
        mNormalColor = ta.getColor(0, 0xff808080);
        mAccentColor = ta.getColor(1, 0xff808080);
        mPrimaryColor = ta.getColor(2, 0xff808080);
        ta.recycle();

        mIconStyle = Settings.System.getInt(getContext().getContentResolver(),
                Settings.System.THEMING_SETTINGS_DASHBOARD_ICONS, 0);
        themePreferences(getPreferenceScreen());
    }

    private void themePreferences(PreferenceGroup prefGroup) {
        themePreference(prefGroup);
        for (int i = 0; i < prefGroup.getPreferenceCount(); i++) {
            Preference pref = prefGroup.getPreference(i);
            if (pref instanceof PreferenceGroup) {
                themePreferences(prefGroup);
            } else {
                themePreference(pref);
            }
        }
    }

    private void themePreference(Preference pref) {
        Drawable icon = pref.getIcon();
        int bgc = 0;
        if (icon != null) {
            if (icon instanceof AdaptiveIcon) {
                AdaptiveIcon aIcon = (AdaptiveIcon) icon;
                bgc = aIcon.getBackgroundColor();
       // Clear colors from previous calls
        aIcon.resetColors();
        switch (mIconStyle) {
            case 0:
                aIcon.setForegroundColor(mPrimaryColor);
                break;
            case 1:
                aIcon.setForegroundColor(mPrimaryColor);
                aIcon.setCustomBackgroundColor(mAccentColor);
                break;
            case 2:
                aIcon.setForegroundColor(mNormalColor);
                aIcon.setCustomBackgroundColor(0);
                break;
            case 3:
                aIcon.setForegroundColor(mAccentColor);
                aIcon.setCustomBackgroundColor(0);
                break;
            case 4:
                aIcon.setForegroundColor(mAccentColor);
                aIcon.setCustomBackgroundColor(ColorUtils.setAlphaComponent(mAccentColor, 51));
                break;
            case 5:
                aIcon.setForegroundColor(bgc);
                aIcon.setCustomBackgroundColor(ColorUtils.setAlphaComponent(bgc, 51));
                break;
        }
            } else if (icon instanceof LayerDrawable) {
                LayerDrawable lIcon = (LayerDrawable) icon;
                if (lIcon.getNumberOfLayers() == 2) {
                    fg = lIcon.getDrawable(1);
                    bg = lIcon.getDrawable(0);
                    bgc = ((ShapeDrawable) bg).getPaint().getColor();
                    // Clear tints from previous calls
                    bg.setTintList(null);
                    fg.setTintList(null);
                    switch (mIconStyle) {
                        case 0:
                            fg.setTint(mPrimaryColor);
                            break;
                        case 1:
                            fg.setTint(mPrimaryColor);
                            bg.setTint(mAccentColor);
                            break;
                        case 2:
                            fg.setTint(mNormalColor);
                            bg.setTint(0);
                            break;
                        case 3:
                            fg.setTint(mAccentColor);
                            bg.setTint(0);
                            break;
                        case 4:
                            fg.setTint(mAccentColor);
                            bg.setTint(ColorUtils.setAlphaComponent(mAccentColor, 51));
                            break;
                        case 5:
                            fg.setTint(bgc);
                            bg.setTint(ColorUtils.setAlphaComponent(bgc, 51));
                            break;
                    }
                }
            }
        }
    }
}
