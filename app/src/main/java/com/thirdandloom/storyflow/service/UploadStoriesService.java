package com.thirdandloom.storyflow.service;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.BackgroundRunnable;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class UploadStoriesService extends Service {
    private static final String PENDING_STORY = "pending_story";

    private Future computation;
    private boolean running;
    private volatile boolean needRefresh;

    private static Intent createIntent() {
        return new Intent(StoryflowApplication.applicationContext, UploadStoriesService.class);
    }

    public static void notifyService() {
        notifyService(createIntent());
    }

    private static void notifyService(Intent intent) {
        StoryflowApplication.applicationContext.startService(intent);
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
            StoryflowApplication.runBackground(new BackgroundRunnable() {
                @Override
                public void run() {
                    super.run();
                    while (running) {
                        if (needRefresh) {
                            needRefresh = false;
                            refreshPendingStories();
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            Timber.e(e.getMessage());
                        }
                    }
                }
            }, future -> computation = future);
        }

        needRefresh = true;
        return START_NOT_STICKY;
    }

    private void refreshPendingStories() {
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
            prepareForUpload(waitingForSendStories.get(0));
        }
        if (!failedStories.isEmpty()) {
            failedStories(failedStories);
        }
        if (!impossibleUploadStories.isEmpty()) {
            impossibleStories(impossibleUploadStories);
        }
        if (waitingForSendStories.isEmpty() && inProgressStories.isEmpty()) {
            uploadFinished();
        }
        //just for testing
        //if (pendingStoriesList.size() >= 4) {
        //    pendingStoriesList.clear();
        //    uploadFinished();
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
        switch (story.getType()) {
            case Image:
                story.setStatus(PendingStory.Status.ImageUploading);
                sendUploadImageRequest(story);
                break;
            case Text:
                story.setStatus(PendingStory.Status.CreatingStory);
                sendCreateTextStoryRequest(story);
                break;
            default:
                story.setStatus(PendingStory.Status.CreateImpossible);
                throw new UnsupportedOperationException("try to upload Unsupported pending story type");
        }
    }

    private void sendUploadImageRequest(PendingStory story) {
        StoryflowApplication.restClient().uploadImage(story, () -> {
            story.setStatus(PendingStory.Status.CreateImpossible);
            needRefresh = true;
        }, storyId -> {
            story.setStoryId(storyId.getId());
            sendCreateImageStoryRequest(story);
        }, (errorMessage, errorType) -> {
            story.setStatus(PendingStory.Status.CreateFailed);
            needRefresh = true;
        });
    }

    private void sendCreateTextStoryRequest(PendingStory story) {
        StoryflowApplication.restClient().createTextStory(story, responseBody -> {
            story.setStatus(PendingStory.Status.CreateSucceed);
            needRefresh = true;
        }, (errorMessage, errorType) -> {
            story.setStatus(PendingStory.Status.CreateFailed);
            needRefresh = true;
        });
    }

    private void sendCreateImageStoryRequest(PendingStory story) {
        StoryflowApplication.restClient().createImageStory(story, responseBody -> {
            story.setStatus(PendingStory.Status.CreateSucceed);
            needRefresh = true;
        }, (errorMessage, errorType) -> {
            story.setStatus(PendingStory.Status.CreateFailed);
            needRefresh = true;
        });
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
