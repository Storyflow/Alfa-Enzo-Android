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
    @SerializedName("image")
    private StoryImageModel imageData;

    public StoryImageModel getImageData() {
        return imageData;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public int getPrivacyId() {
        return privacyId;
    }

    public String getType() {
        return type;
    }

    public boolean isUserLikes() {
        return userLikes;
    }

    public int getLikes() {
        return likes;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return date;
    }

    public Author getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public static class WrapList {
        @SerializedName("stories")
        private List<Story> stories;
        @SerializedName("nextId")
        private String nextStoryId;
        @SerializedName("prevId")
        private String previousStoryId;

        public Story getStory(int position) {
            return getStories().get(position);
        }

        public List<Story> getStories() {
            return stories;
        }

        public String getNextStoryId() {
            return nextStoryId;
        }

        public String getPreviousStoryId() {
            return previousStoryId;
        }
    }
}
