package com.thirdandloom.storyflow.preferences.sharedpreference;

public class BooleanPreference extends SharedProperty<Boolean> {
    public BooleanPreference(String key, BaseSharedPreferences preferences) {
        super(key, preferences);
    }

    @Override
    public void set(Boolean value) {
        getEditor().putBoolean(getKey(), value).apply();
    }

    @Override
    public Boolean get() {
        return get(false);
    }

    @Override
    public Boolean get(Boolean defaultValue) {
        return getPreferences().getBoolean(getKey(), defaultValue);
    }
}
