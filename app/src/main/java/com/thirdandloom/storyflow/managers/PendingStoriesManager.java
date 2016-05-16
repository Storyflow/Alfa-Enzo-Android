package com.thirdandloom.storyflow.managers;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.PendingStoriesContainer;
import com.thirdandloom.storyflow.models.PendingStory;

import android.support.annotation.Nullable;

import java.util.List;

public class PendingStoriesManager {
    private PendingStoriesContainer pendingStoriesContainer;

    public void add(@Nullable PendingStory pendingStory) {
        if (pendingStory != null) {
            getPendingStoriesContainer().pendingStories.add(pendingStory);
            saveData();
        }
    }

    public void remove(PendingStory pendingStory) {
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

    public List<PendingStory> getPendingStories() {
        return getPendingStoriesContainer().pendingStories;
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
