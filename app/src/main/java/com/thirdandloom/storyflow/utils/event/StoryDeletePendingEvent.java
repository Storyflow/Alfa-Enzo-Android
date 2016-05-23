package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.models.PendingStory;

public class StoryDeletePendingEvent {

    private PendingStory story;

    public StoryDeletePendingEvent(PendingStory story) {
        this.story = story;
    }

    public PendingStory getStory() {
        return story;
    }
}
