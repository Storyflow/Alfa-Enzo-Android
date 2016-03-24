package com.thirdandloom.storyflow.preferences.sharedpreference;

public class DoublePreference extends SharedProperty<Double> {
    public DoublePreference(String key, BaseSharedPreferences preferences) {
        super(key, preferences);
    }

    public void set(double value) {
        getEditor().putLong(getKey(), Double.doubleToLongBits(value)).apply();
    }

    @Override
    public void set(Double value) {
        set(value.doubleValue());
    }

    @Override
    public Double get() {
        return get(0.0);
    }

    @Override
    public Double get(Double defaultValue) {
        if (!getPreferences().contains(getKey())) {
            return defaultValue;
        }
        Long value = getPreferences().getLong(getKey(), 0);
        return Double.longBitsToDouble(value);
    }
}
