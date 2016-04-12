package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Story extends BaseModel {
    @SerializedName("id")
    private String id;
    @SerializedName("author")
    private Author author;
    @SerializedName("storyDate")
    private Date date;
    @SerializedName("description")
    private String description;
    @SerializedName("likes")
    private int likes;
    @SerializedName("userLikes")
    private boolean userLikes;
    @SerializedName("type")
    private String type;
    @SerializedName("privacyId")
    private int privacyId;
    @SerializedName("createdAt")
    private Date createdAt;

    public static class WrapList {
        @SerializedName("stories")
        private List<Story> stories;

        public List<Story> getStories() {
            return stories;
        }
    }
}
