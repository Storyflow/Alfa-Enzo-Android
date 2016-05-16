package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Avatar;
import com.thirdandloom.storyflow.models.image.CroppedImage;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.preferences.UserPreferences;
import com.thirdandloom.storyflow.utils.StringUtils;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.util.ArrayList;

public class AccountManager {
    private User currentUser;
    private String password;

    public AccountManager() {
        UserPreferences preferences = StoryflowApplication.userPreferences;
        password = preferences.password.get();
        getUser();
    }

    public void updateProfile(@NonNull User user) {
        currentUser = user;
        UserPreferences preferences = StoryflowApplication.userPreferences;
        preferences.userProfile.set(currentUser);
    }

    public void updateProfile(@NonNull Avatar avatar) {
        User user = getUser();

        if (user.getProfileImages() == null)
            user.setProfileImages(new ArrayList<>());
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

    public User getUser() {
        if (currentUser == null) {
            UserPreferences preferences = StoryflowApplication.userPreferences;
            currentUser = preferences.userProfile.get();
            if (currentUser == null) {
                currentUser = new User();
            }
        }
        return currentUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        UserPreferences preferences = StoryflowApplication.userPreferences;
        preferences.password.set(this.password);
    }

    public void resetAccount() {
        currentUser = null;
        password = StringUtils.EMPTY;
        StoryflowApplication.userPreferences.clear();
    }

    public boolean isCurrentUser(String userUid){
        return currentUser != null && currentUser.getUid().equals(userUid);
    }

}
