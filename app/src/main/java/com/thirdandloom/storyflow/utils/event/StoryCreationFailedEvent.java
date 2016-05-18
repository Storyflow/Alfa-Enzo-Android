package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.models.PendingStory;

public class StoryCreationFailedEvent {
    private PendingStory story;

    public StoryCreationFailedEvent(PendingStory story) {
        this.story = story;
    }

    public PendingStory getStory() {
        return story;
    }
}
