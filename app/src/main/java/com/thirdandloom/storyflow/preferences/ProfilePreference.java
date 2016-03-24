package com.thirdandloom.storyflow.preferences;

import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.preferences.sharedpreference.BaseSharedPreferences;
import com.thirdandloom.storyflow.preferences.sharedpreference.JsonPreference;
import com.thirdandloom.storyflow.utils.JsonUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ProfilePreference<Key extends Enum> extends JsonPreference<User, Key> {
    public ProfilePreference(Key key, BaseSharedPreferences<Key> preferences) {
        super(key, preferences);
    }

    @Nullable
    @Override
    protected User parse(@NonNull String json) {
        return JsonUtils.parse(json, User.class);
    }

    void set(String json) {
        getEditor().putString(getKey(), json).apply();
    }
}
