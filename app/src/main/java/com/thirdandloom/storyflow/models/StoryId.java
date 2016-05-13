package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class StoryId extends BaseModel {
    private static final long serialVersionUID = 3942420757985956322L;

    @SerializedName("customStoryId")
    private String id;

    public String getId() {
        return id;
    }
}
