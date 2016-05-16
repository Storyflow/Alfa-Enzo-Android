package com.thirdandloom.storyflow.preferences;

import com.thirdandloom.storyflow.models.PendingStoriesContainer;
import com.thirdandloom.storyflow.preferences.sharedpreference.BaseSharedPreferences;
import com.thirdandloom.storyflow.preferences.sharedpreference.JsonPreference;
import com.thirdandloom.storyflow.utils.JsonUtils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PendingStoriesPreference<Key extends Enum> extends JsonPreference<PendingStoriesContainer, Key> {

    public PendingStoriesPreference(Key key, BaseSharedPreferences<Key> preferences) {
        super(key, preferences);
    }

    @Nullable
    @Override
    protected PendingStoriesContainer parse(@NonNull String json) {
        return JsonUtils.parse(json, PendingStoriesContainer.class);
    }

    void set(String json) {
        getEditor().putString(getKey(), json).apply();
    }
}
