package com.thirdandloom.storyflow.preferences;

import com.thirdandloom.storyflow.preferences.sharedpreference.BaseSharedPreferences;
import com.thirdandloom.storyflow.preferences.sharedpreference.Property;

@SuppressWarnings({"PublicField", "InstanceVariableNamingConvention"})
public class userDataPreferences extends BaseSharedPreferences<userDataPreferences.Key> {

    enum Key {
        USER_PROFILE,
        PASSWORD
    }

    public final ProfilePreference<Key> userProfile = new ProfilePreference<>(Key.USER_PROFILE, this);
    public final Property<String> password = newStringProperty(Key.PASSWORD);


    @Override
    protected PreferenceId getId() {
        return PreferenceId.COMMON;
    }
}
