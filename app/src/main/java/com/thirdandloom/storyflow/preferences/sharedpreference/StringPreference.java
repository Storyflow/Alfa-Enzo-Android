package com.thirdandloom.storyflow.preferences.sharedpreference;

public class StringPreference extends SharedProperty<String> {
    public StringPreference(String key, BaseSharedPreferences preferences) {
        super(key, preferences);
    }

    @Override
    public void set(String value) {
        getEditor().putString(getKey(), value).apply();
    }

    @Override
    public String get() {
        return get("");
    }

    @Override
    public String get(String defaultValue) {
        return getPreferences().getString(getKey(), defaultValue);
    }
}
