package com.thirdandloom.storyflow.preferences;

import com.thirdandloom.storyflow.preferences.sharedpreference.BaseSharedPreferences;

public class AppDataPreferences extends BaseSharedPreferences<AppDataPreferences.Key> {

    enum Key {
        PENDING_STORIES_CONTAINER
    }

    @Override
    protected PreferenceId getId() {
        return PreferenceId.DATA;
    }

    public final PendingStoriesPreference<Key> pendingStoriesPreference = new PendingStoriesPreference<>(Key.PENDING_STORIES_CONTAINER, this);
}
