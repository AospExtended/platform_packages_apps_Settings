/*
 * Copyright (C) 2017 AospExtended
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
package com.android.settings.deviceinfo;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemProperties;
import android.os.PersistableBundle;
import android.os.UserManager;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.telephony.CarrierConfigManager;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import org.aospextended.extensions.Utils;
import com.android.settings.core.PreferenceControllerMixin;
import com.android.settingslib.core.AbstractPreferenceController;

public class AexOtaPreferenceController extends AbstractPreferenceController implements
        PreferenceControllerMixin {

    private static final String TAG = "AexOtaPrefContr";

    private static final String KEY_AEX_OTA = "aex_ota";
    private static final String KEY_AEXOTA_PACKAGE_NAME = "com.aospextended.ota";

    private final UserManager mUm;

    public AexOtaPreferenceController(Context context, UserManager um) {
        super(context);
        mUm = um;
    }

    @Override
    public boolean isAvailable() {
     String buildtype = SystemProperties.get("ro.extended.releasetype","unofficial");
     if (Utils.isPackageInstalled(mContext, KEY_AEXOTA_PACKAGE_NAME) && buildtype.equalsIgnoreCase("official")) {

        return mUm.isAdminUser();

    } else {

        return false;

    }
    }

    @Override
    public String getPreferenceKey() {
        return KEY_AEX_OTA;
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        if (!isAvailable()) {
            removePreference(screen, KEY_AEX_OTA);
        }
    }


}
