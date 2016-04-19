package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Avatar;
import com.thirdandloom.storyflow.models.image.CroppedImage;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.preferences.CommonPreferences;
import com.thirdandloom.storyflow.utils.StringUtils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

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

    public void updateProfile(@NonNull Avatar avatar) {
        User user = getUser();
        if (user.getProfileImages() == null) user.setProfileImages(new ArrayList<>());
        if (user.getProfileImages().contains(avatar)) {
            int index = user.getProfileImages().indexOf(avatar);
            user.getProfileImages().set(index, avatar);
        } else {
            user.getProfileImages().add(avatar);
        }

        if (!TextUtils.isEmpty(avatar.getCroppedImage().url())) {
            CroppedImage profileImage = new CroppedImage();
            profileImage.setRect(avatar.getCroppedRect());
            profileImage.setImageUrl(avatar.getCroppedImage().url());
            user.setProfileImage(profileImage);
        }
        updateProfile(user);
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
