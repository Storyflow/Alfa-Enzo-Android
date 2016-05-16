package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Story extends BaseModel {
    private static final long serialVersionUID = -4822229773464454707L;
    @SerializedName("id")
    private String id;
    @SerializedName("author")
    private Author author;
    @SerializedName("storyDate")
    private Date date;
    @SerializedName("description")
    private String description;
    @SerializedName("likesCount")
    private int likesCount;
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
    @SerializedName("commentsCount")
    private int commentsCount;

    public int getCommentsCount() {
        return commentsCount;
    }

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

    public int getLikesCount() {
        return likesCount;
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

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageData(StoryImageModel imageData) {
        this.imageData = imageData;
    }

    public enum Type { Text, Image }

    static final Map<String, Type> types;
    static {
        Map<String, Type> map = new HashMap<>();
        map.put("image", Type.Image);
        map.put("text", Type.Text);
        types = Collections.unmodifiableMap(map);
    }

    public static class WrapList implements Serializable {
        private static final long serialVersionUID = 2232982624031951655L;
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

        public void setStories(List<Story> stories) {
            this.stories = stories;
        }

        public void addStories(List<Story> stories) {
            this.stories.addAll(stories);
        }

        public void setNextStoryId(String nextStoryId) {
            this.nextStoryId = nextStoryId;
        }

        public void setPreviousStoryId(String previousStoryId) {
            this.previousStoryId = previousStoryId;
        }
    }
}
