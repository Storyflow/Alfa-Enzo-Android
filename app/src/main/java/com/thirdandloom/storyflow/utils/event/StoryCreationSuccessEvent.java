package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.models.PendingStory;

public class StoryCreationSuccessEvent {
    private PendingStory story;

    public StoryCreationSuccessEvent(PendingStory story) {
        this.story = story;
    }

    public PendingStory getStory() {
        return story;
    }
}
