package com.thirdandloom.storyflow.preferences.sharedpreference;

import com.google.gson.Gson;
import com.thirdandloom.storyflow.utils.StringUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public abstract class JsonPreference<T, Key extends Enum> extends SharedProperty<T> {
    public JsonPreference(Key key, BaseSharedPreferences<Key> preferences) {
        super(key.name(), preferences);
    }

    @Override
    public void set(T value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);
        getEditor().putString(getKey(), json).apply();
    }

    @Nullable
    @Override
    public T get() {
        String json = getPreferences().getString(getKey(), StringUtils.EMPTY);
        if (TextUtils.isEmpty(json)) {
            return null;
        }
        T value = parse(json);
        return value;
    }

    @Override
    public T get(T defaultValue) {
        T value = get();
        return (value != null) ? value : defaultValue;
    }

    @Nullable
    protected abstract T parse(@NonNull String json);
}
