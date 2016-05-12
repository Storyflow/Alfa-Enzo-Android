package com.thirdandloom.storyflow.service;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStory;
import rx.functions.Action0;
import rx.functions.Action1;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class UploadStoriesService extends Service {
    private static final String PENDING_STORY = "pending_story";

    private Future computation;
    private boolean running;
    private boolean needRefresh;

    private static Intent createIntent() {
        return new Intent(StoryflowApplication.getInstance(), UploadStoriesService.class);
    }

    public static void notifyService() {
        notifyService(createIntent());
    }

    private static void notifyService(Intent intent) {
        StoryflowApplication.getInstance().startService(intent);
    }

    public static void addStory(PendingStory story) {
        Intent intent = createIntent();
        intent.putExtra(PENDING_STORY, story);
        notifyService(intent);
    }

    public static void removeStory(PendingStory story) {

    }

    private List<PendingStory> pendingStoriesList = new ArrayList<>();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        PendingStory story = (PendingStory)intent.getSerializableExtra(PENDING_STORY);
        if (story != null) {
            pendingStoriesList.add(story);
        }

        if (!running) {
            running = true;
            StoryflowApplication.runBackground(new Runnable() {
                @Override
                public void run() {
                    while (running) {
                        if (needRefresh) {
                            needRefresh = false;
                            refreshPendingStories(UploadStoriesService.this::prepareForUpload,
                                    UploadStoriesService.this::failedStories,
                                    UploadStoriesService.this::impossibleStories,
                                    UploadStoriesService.this::uploadFinished);
                        }
                    }
                }
            }, future -> computation = future);
        }

        needRefresh = true;
        return START_NOT_STICKY;
    }

    private void refreshPendingStories(Action1<PendingStory> uploadStory,
            Action1<List<PendingStory>> failedDetected,
            Action1<List<PendingStory>> impossibleDetected,
            Action0 uploadFinished) {
        List<PendingStory> waitingForSendStories = new ArrayList<>();
        List<PendingStory> succeedStories = new ArrayList<>();
        List<PendingStory> inProgressStories = new ArrayList<>();
        List<PendingStory> failedStories = new ArrayList<>();
        List<PendingStory> impossibleUploadStories = new ArrayList<>();

        for (PendingStory story : pendingStoriesList) {
            PendingStory.Status storyStatus = story.getStatus();
            switch (storyStatus) {
                case WaitingForSend:
                    waitingForSendStories.add(story);
                    break;

                case ImageUploading:
                case CreatingStory:
                    inProgressStories.add(story);
                    break;

                case CreateFailed:
                    failedStories.add(story);
                    break;

                case CreateImpossible:
                    impossibleUploadStories.add(story);
                    break;

                case CreateSucceed:
                    succeedStories.add(story);
                    break;

                default:
                    throw new UnsupportedOperationException("You are using unsupported pending story status");
            }
        }
        pendingStoriesList.removeAll(succeedStories);
        if (inProgressStories.isEmpty() && !waitingForSendStories.isEmpty()) {
            uploadStory.call(waitingForSendStories.get(0));
        }
        if (!failedStories.isEmpty()) {
            failedDetected.call(failedStories);
        }
        if (!impossibleUploadStories.isEmpty()) {
            impossibleDetected.call(impossibleUploadStories);
        }
        if (waitingForSendStories.isEmpty() && inProgressStories.isEmpty()) {
            uploadFinished.call();
        }

        //just for testing
        //if (pendingStoriesList.size() >= 2) {
        //    pendingStoriesList.clear();
        //    uploadFinished.call();
        //}

    }

    private void impossibleStories(List<PendingStory> stories) {
        // notify impossible stories for user
    }

    private void failedStories(List<PendingStory> stories) {
        // notify failed stories for user
    }

    private void uploadFinished() {
        computation.cancel(true);
        running = false;
    }

    private void prepareForUpload(PendingStory story) {

    }

    // Requests:
    private void uploadImage(PendingStory story) {

    }

    private void createStory(PendingStory story) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        computation.cancel(true);
        running = false;
        needRefresh = false;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
