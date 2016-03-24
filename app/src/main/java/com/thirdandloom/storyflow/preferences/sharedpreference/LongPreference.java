package com.thirdandloom.storyflow.preferences.sharedpreference;

public class LongPreference extends SharedProperty<Long> {
    public LongPreference(String key, BaseSharedPreferences preferences) {
        super(key, preferences);
    }

    public void set(long value) {
        getEditor().putLong(getKey(), value).apply();
    }

    @Override
    public void set(Long value) {
        set(value.longValue());
    }

    @Override
    public Long get() {
        return get(0L);
    }

    @Override
    public Long get(Long defaultValue) {
        return getPreferences().getLong(getKey(), defaultValue);
    }
}
