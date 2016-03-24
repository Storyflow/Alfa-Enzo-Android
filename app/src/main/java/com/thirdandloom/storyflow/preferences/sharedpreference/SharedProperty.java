package com.thirdandloom.storyflow.preferences.sharedpreference;

import android.content.SharedPreferences;

public abstract class SharedProperty<T> implements Property<T> {
    private final BaseSharedPreferences mPreferences;
    private final String mKey;

    public SharedProperty(String key, BaseSharedPreferences preferences) {
        mKey = key;
        mPreferences = preferences;
    }

    protected String getKey() {
        return mKey;
    }

    protected SharedPreferences getPreferences() {
        return mPreferences.getPreferences();
    }

    protected SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    @Override
    public void reset() {
        getEditor().remove(mKey).apply();
    }
}
