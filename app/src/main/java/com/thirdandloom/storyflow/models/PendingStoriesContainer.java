package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PendingStoriesContainer extends BaseModel {
    private static final long serialVersionUID = -605827697425286427L;

    @SerializedName("pendingStories")
    private List<PendingStory> pendingStories = new ArrayList<>();

    public List<PendingStory> getPendingStoriesSynchronizedList() {
        return Collections.synchronizedList(pendingStories);
    }
}
