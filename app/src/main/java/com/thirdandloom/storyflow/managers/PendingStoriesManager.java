package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStoriesContainer;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.service.UploadStoriesService;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.concurrent.BackgroundRunnable;
import com.thirdandloom.storyflow.utils.event.StoryDeletePendingEvent;

import rx.functions.Action1;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PendingStoriesManager {
    private PendingStoriesContainer pendingStoriesContainer;

    public void add(@Nullable PendingStory pendingStory) {
        if (pendingStory != null) {
            getPendingStoriesContainer().getPendingStoriesSynchronizedList().add(0, pendingStory);
            saveData();
        }
    }

    public void clearAll() {
        StoryflowApplication.applicationPreferences.clear();
        pendingStoriesContainer = null;
    }

    public void retry(String pendingStoryLocalUid) {
        StoryflowApplication.runBackground(new BackgroundRunnable() {
            @Override
            public void run() {
                super.run();
                getPendingStory(pendingStoryLocalUid, story -> {
                    updateStoryStatus(PendingStory.Status.WaitingForSend, story);
                    UploadStoriesService.notifyService();
                });
            }
        });
    }

    public void remove(String pendingStoryLocalUid) {
        StoryflowApplication.runBackground(new BackgroundRunnable() {
            @Override
            public void run() {
                super.run();
                getPendingStory(pendingStoryLocalUid, story -> {
                    PendingStoriesManager.this.remove(story);
                    EventBus.getDefault().post(new StoryDeletePendingEvent(story));
                });
            }
        });
    }

    private void remove(PendingStory pendingStory) {
        getPendingStories().remove(pendingStory);
        saveData();
    }

    public void removeAll(List<PendingStory> stories) {
        getPendingStories().removeAll(stories);
        saveData();
    }

    private void saveData() {
        StoryflowApplication.applicationPreferences.pendingStoriesPreference.set(getPendingStoriesContainer());
    }

    private PendingStoriesContainer getPendingStoriesContainer() {
        if (pendingStoriesContainer == null) {
            pendingStoriesContainer = StoryflowApplication.applicationPreferences.pendingStoriesPreference.get();
            if (pendingStoriesContainer == null) {
                pendingStoriesContainer = new PendingStoriesContainer();
            }
        }
        return pendingStoriesContainer;
    }

    private void getPendingStory(String pendingStoryLocalUid, Action1<PendingStory> found) {
        for (PendingStory story : getPendingStories()) {
            if (story.getLocalUid().equals(pendingStoryLocalUid)) {
                found.call(story);
                break;
            }
        }
    }

    public List<PendingStory> getPendingStories() {
        return getPendingStoriesContainer().getPendingStoriesSynchronizedList();
    }

    public List<Story> getStories(@NonNull Calendar calendar, StoriesManager.RequestData.Period.Type period) {
        List<Story> storiesForDate = new ArrayList<>();
        for (PendingStory story : getPendingStoriesContainer().getPendingStoriesSynchronizedList()) {
            switch (period) {
                case Daily:
                    if (DateUtils.isSameDay(calendar.getTime(), story.getDate())) {
                        storiesForDate.add(story.convertToStory());
                    }
                    break;
                case Monthly:
                    if (DateUtils.isSameMonth(calendar.getTime(), story.getDate())) {
                        storiesForDate.add(story.convertToStory());
                    }
                    break;
                case Yearly:
                    if (DateUtils.isSameYear(calendar.getTime(), story.getDate())) {
                        storiesForDate.add(story.convertToStory());
                    }
                    break;
            }
        }

        return storiesForDate;
    }

    public void updateStoryStatus(PendingStory.Status newStatus, PendingStory story) {
        int previousIndex = getPendingStories().indexOf(story);
        story.setStatus(newStatus);
        getPendingStories().set(previousIndex, story);
        saveData();
    }

    public void setStoryId(String storyId, PendingStory story) {
        int previousIndex = getPendingStories().indexOf(story);
        story.setStoryId(storyId);
        getPendingStories().set(previousIndex, story);
        saveData();
    }
}
