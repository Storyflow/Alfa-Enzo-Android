package com.thirdandloom.storyflow.service;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class UploadStoriesService extends Service {

    //Intent intent = UploadStoriesService.createIntent();
    //StoryflowApplication.getInstance().startService(intent);
    public static Intent createIntent() {
        Intent intent = new Intent(StoryflowApplication.getInstance(), UploadStoriesService.class);
        return intent;
    }

    public static void addStory() {

    }

    public static void removeStory() {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
