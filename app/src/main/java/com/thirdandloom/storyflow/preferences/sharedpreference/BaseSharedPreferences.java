package com.thirdandloom.storyflow.preferences.sharedpreference;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

public abstract class BaseSharedPreferences<Key extends Enum> {
    public enum PreferenceId {
        COMMON
    }

    protected SharedPreferences getPreferences() {
        String name = getId().name();
        return StoryflowApplication.applicationContext.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    protected SharedPreferences.Editor getEditor() {
        return getPreferences().edit();
    }

    public void clear() {
        getEditor().clear().apply();
    }

    protected abstract PreferenceId getId();

    @NonNull
    protected BooleanPreference newBooleanProperty(Key key) {
        return new BooleanPreference(key.name(), this);
    }

    @NonNull
    protected LongPreference newLongProperty(Key key) {
        return new LongPreference(key.name(), this);
    }

    @NonNull
    protected IntPreference newIntProperty(Key key) {
        return new IntPreference(key.name(), this);
    }

    @NonNull
    protected IntPreference newIntProperty(Key key, int defaultValue) {
        return new IntPreference(key.name(), this, defaultValue);
    }

    @NonNull
    protected StringPreference newStringProperty(Key key) {
        return new StringPreference(key.name(), this);
    }

    @NonNull
    protected DoublePreference newDoubleProperty(Key key) {
        return new DoublePreference(key.name(), this);
    }
}
