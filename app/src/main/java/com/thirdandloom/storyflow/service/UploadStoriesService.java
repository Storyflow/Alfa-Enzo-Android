package com.thirdandloom.storyflow.service;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.PendingStoriesManager;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.BackgroundRunnable;
import com.thirdandloom.storyflow.utils.event.StoryCreationFailedEvent;
import com.thirdandloom.storyflow.utils.event.StoryCreationSuccessEvent;

import android.app.Service;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

public class UploadStoriesService extends Service {
    private Future computation;
    private volatile boolean running;
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

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
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

        for (PendingStory story : getPendingStoriesManager().getPendingStories()) {
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
                case CreateImpossible:
                case OnServer:
                    break;
                case CreateSucceed:
                    succeedStories.add(story);
                    break;

                default:
                    throw new UnsupportedOperationException("You are using unsupported pending story status");
            }
        }
        getPendingStoriesManager().removeAll(succeedStories);
        if (inProgressStories.isEmpty() && !waitingForSendStories.isEmpty()) {
            prepareForUpload(waitingForSendStories.get(0));
        }
        if (waitingForSendStories.isEmpty() && inProgressStories.isEmpty()) {
            uploadFinished();
        }
    }

    private void uploadFinished() {
        computation.cancel(true);
        running = false;
    }

    private void prepareForUpload(PendingStory story) {
        switch (story.getType()) {
            case Image:
                if (TextUtils.isEmpty(story.getStoryId())) {
                    getPendingStoriesManager().updateStoryStatus(PendingStory.Status.ImageUploading, story);
                    sendUploadImageRequest(story);
                } else {
                    getPendingStoriesManager().updateStoryStatus(PendingStory.Status.CreatingStory, story);
                    sendCreateImageStoryRequest(story);
                }
                break;
            case Text:
                getPendingStoriesManager().updateStoryStatus(PendingStory.Status.CreatingStory, story);
                sendCreateTextStoryRequest(story);
                break;
            default:
                storyCreationImpossible(story);
                throw new UnsupportedOperationException("try to upload Unsupported pending story type");
        }
    }

    private void sendUploadImageRequest(PendingStory story) {
        StoryflowApplication.restClient().uploadImageSync(story, () -> {
            storyCreationImpossible(story);
        }, storyId -> {
            getPendingStoriesManager().setStoryId(storyId.getId(), story);
            sendCreateImageStoryRequest(story);
        }, (errorMessage, errorType) -> {
            storyCreationFailed(story);
        });
    }

    private void sendCreateTextStoryRequest(PendingStory story) {
        StoryflowApplication.restClient().createTextStorySync(story, responseBody -> {
            storyCreationSucceed(story);
        }, (errorMessage, errorType) -> {
            storyCreationFailed(story);
        });
    }

    private void sendCreateImageStoryRequest(PendingStory story) {
        StoryflowApplication.restClient().createImageStorySync(story, responseBody -> {
            storyCreationSucceed(story);
        }, (errorMessage, errorType) -> {
            storyCreationFailed(story);
        });
    }

    private PendingStoriesManager getPendingStoriesManager() {
        return StoryflowApplication.getPendingStoriesManager();
    }

    private void storyCreationSucceed(PendingStory story) {
        getPendingStoriesManager().updateStoryStatus(PendingStory.Status.CreateSucceed, story);
        needRefresh = true;
        Timber.d("StoryCreationFailed onEvent post");

        EventBus.getDefault().post(new StoryCreationSuccessEvent(story));
    }

    private void storyCreationFailed(PendingStory story) {
        getPendingStoriesManager().updateStoryStatus(PendingStory.Status.CreateFailed, story);
        needRefresh = true;
        EventBus.getDefault().post(new StoryCreationFailedEvent(story));
    }

    private void storyCreationImpossible(PendingStory story) {
        getPendingStoriesManager().updateStoryStatus(PendingStory.Status.CreateImpossible, story);
        needRefresh = true;
        EventBus.getDefault().post(new StoryCreationFailedEvent(story));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uploadFinished();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

    @Nullable
    @Override
    public android.os.IBinder onBind(Intent intent) {
        return null;
    }
}
