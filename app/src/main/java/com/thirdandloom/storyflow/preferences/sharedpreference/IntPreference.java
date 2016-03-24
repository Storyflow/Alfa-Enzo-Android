package com.thirdandloom.storyflow.preferences.sharedpreference;

public class IntPreference extends SharedProperty<Integer> {
    private final int mDefaultValue;

    public IntPreference(String key, BaseSharedPreferences preferences) {
        this(key, preferences, 0);
    }

    public IntPreference(String key, BaseSharedPreferences preferences, int defaultValue) {
        super(key, preferences);
        mDefaultValue = defaultValue;
    }

    @Override
    public void set(Integer value) {
        getEditor().putInt(getKey(), value).apply();
    }

    @Override
    public Integer get() {
        return get(mDefaultValue);
    }

    @Override
    public Integer get(Integer defaultValue) {
        return getPreferences().getInt(getKey(), defaultValue);
    }
}
