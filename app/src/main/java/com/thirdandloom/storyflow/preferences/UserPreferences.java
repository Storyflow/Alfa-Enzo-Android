package com.thirdandloom.storyflow.preferences;

import com.thirdandloom.storyflow.preferences.sharedpreference.BaseSharedPreferences;
import com.thirdandloom.storyflow.preferences.sharedpreference.Property;

@SuppressWarnings({"PublicField", "InstanceVariableNamingConvention"})
public class UserPreferences extends BaseSharedPreferences<UserPreferences.Key> {

    enum Key {
        USER_PROFILE,
        PASSWORD
    }

    @Override
    protected PreferenceId getId() {
        return PreferenceId.COMMON;
    }

    public final ProfilePreference<Key> userProfile = new ProfilePreference<>(Key.USER_PROFILE, this);
    public final Property<String> password = newStringProperty(Key.PASSWORD);
}
