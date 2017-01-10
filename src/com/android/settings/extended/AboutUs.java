
/*
 * Copyright (C) 2016 Cosmic-OS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.extended;

import android.os.Bundle;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.R;
import android.net.Uri;
import java.util.Locale;

import com.android.internal.logging.MetricsProto.MetricsEvent;

public class AboutUs extends SettingsPreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.about_us);
		
		PreferenceCategory maintainers = (PreferenceCategory)findPreference("maintainers");
		PreferenceCategory translators = (PreferenceCategory)findPreference("translators");
		
		String[] maintainers_title = getResources().getStringArray(R.array.maintainers_title);
        String[] maintainers_devices = getResources().getStringArray(R.array.maintainers_devices);
        String[] maintainers_url = getResources().getStringArray(R.array.maintainers_url);
		
		String[] translators_title = getResources().getStringArray(R.array.translators_title);
        String[] translators_language = getResources().getStringArray(R.array.translators_language);
        String[] translators_url = getResources().getStringArray(R.array.translators_url);

        for (int i = 0; i < maintainers_title.length; i++) {
            Preference maintainer = new Preference(this);
            final String maintainer_url = maintainers_url[i];
            maintainer.setIcon(R.drawable.ic_devs_phone);
            maintainer.setTitle(maintainers_title[i]);
            maintainer.setSummary(String.format(getString(R.string.maintainer_description), maintainers_devices[i]));
            maintainer.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(maintainer_url)));
                    return true;
                }
            });
            maintainers.addPreference(maintainer);
        }

        for (int i = 0; i < translators_title.length; i++) {
            Preference translator = new Preference(this);
            final String translator_url = translators_url[i];
            translator.setIcon(R.drawable.ic_trans);
            translator.setTitle(translators_title[i]);
			String displayName = "";
			try {
            Locale locale = new Locale.Builder().setLanguageTag(translators_language[i]).build();
            displayName = locale.getDisplayName();
			}catch (Exception ex){
            displayName = "";
            }
            if (!displayName.equals("")){
            translator.setSummary(String.format(getString(R.string.translator_description), displayName));
            }
            translator.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(translator_url)));
                    return true;
                }
            });
            translators.addPreference(translator);
        }
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.EXTENSIONS;
    }

}
