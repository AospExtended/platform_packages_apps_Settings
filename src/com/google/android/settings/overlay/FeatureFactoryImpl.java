package com.google.android.settings.overlay;

import android.content.Context;

import com.android.settings.accounts.AccountFeatureProvider;
import com.android.settings.applications.GameSettingsFeatureProvider;
import com.google.android.settings.accounts.AccountFeatureProviderGoogleImpl;
import com.google.android.settings.games.GameSettingsFeatureProviderGoogleImpl;

public final class FeatureFactoryImpl extends com.android.settings.overlay.FeatureFactoryImpl {

    private AccountFeatureProvider mAccountFeatureProvider;
    private GameSettingsFeatureProvider mGameSettingsFeatureProvider;

    @Override
    public AccountFeatureProvider getAccountFeatureProvider() {
        if (mAccountFeatureProvider == null) {
            mAccountFeatureProvider = new AccountFeatureProviderGoogleImpl();
        }
        return mAccountFeatureProvider;
    }

    @Override
    public GameSettingsFeatureProvider getGameSettingsFeatureProvider() {
        if (mGameSettingsFeatureProvider == null) {
            mGameSettingsFeatureProvider = new GameSettingsFeatureProviderGoogleImpl();
        }
        return mGameSettingsFeatureProvider;
    }
}
