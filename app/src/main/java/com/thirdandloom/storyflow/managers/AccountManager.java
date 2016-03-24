package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.preferences.CommonPreferences;
import com.thirdandloom.storyflow.utils.StringUtils;

import android.support.annotation.NonNull;

public class AccountManager {
    private User currentUser;
    private String mPassword;

    public AccountManager() {
        CommonPreferences preferences = StoryflowApplication.preferences();
        mPassword = preferences.password.get();
    }

    public void updateProfile(@NonNull User user) {
        currentUser = user;
        CommonPreferences preferences = StoryflowApplication.preferences();
        preferences.userProfile.set(currentUser);
    }

    @NonNull
    public User getUser() {
        if (currentUser != null) {
            return currentUser;
        }

        CommonPreferences preferences = StoryflowApplication.preferences();
        currentUser = preferences.userProfile.get();
        if (currentUser == null) {
            currentUser = new User();
        }
        return currentUser;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
        CommonPreferences preferences = StoryflowApplication.preferences();
        preferences.password.set(mPassword);
    }

    public void resetAccount() {
        currentUser = null;
        mPassword = StringUtils.EMPTY;
    }

    public boolean isCurrentUser(String userUid){
        return getUser().getUid().equals(userUid);
    }
}
