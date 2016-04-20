package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Type getType() {
        return types.get(type);
    }

    public enum Type { Text, Image }

    static final Map<String, Type> types;
    static {
        Map<String, Type> map = new HashMap<>();
        map.put("image", Type.Image);
        map.put("text", Type.Text);
        types = Collections.unmodifiableMap(map);
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
