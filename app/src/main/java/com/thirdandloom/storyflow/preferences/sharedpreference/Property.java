package com.thirdandloom.storyflow.preferences.sharedpreference;

public interface Property<T> {
    void set(T value);
    T get();
    T get(T defaultValue);
    void reset();
}
