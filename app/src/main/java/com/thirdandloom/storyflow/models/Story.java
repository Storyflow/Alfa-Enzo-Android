package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.models.Time;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
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
    private long date;
    @SerializedName("creationDate")
    private long createdAt;
    @SerializedName("description")
    private String description;
    @SerializedName("commentsCount")
    private int commentsCount;
    @SerializedName("type")
    private String type;
    @SerializedName("privacyId")
    private int privacyId;
    @SerializedName("image")
    private StoryImageModel imageData;
    //@SerializedName("mentions")
    //private mentions mentions;
    //@SerializedName("likes")
    //private likes likes;

    public enum FillType {
        Filled, Empty
    }

    private PendingStory.Status pendingStatus = PendingStory.Status.OnServer;
    private String localUid = "";
    private FillType fillType = FillType.Filled;

    public FillType getFillType() {
        return fillType;
    }

    public void setFillType(FillType fillType) {
        this.fillType = fillType;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public StoryImageModel getImageData() {
        return imageData;
    }

    public Date getCreatedAt() {
        return new Time(createdAt).convertToDate();
    }

    public int getPrivacyId() {
        return privacyId;
    }

    public String getDescription() {
        return description;
    }

    public Date getDate() {
        return new Time(date*1000).convertToDate();
    }

    public Author getAuthor() {
        return author;
    }

    public String getId() {
        return id;
    }

    public Type getType() {
        return typeFromString.get(type);
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(Date date) {
        this.date = new Time(date).roundToMillis()/DateUtils.MS_IN_SEC;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setImageData(StoryImageModel imageData) {
        this.imageData = imageData;
    }

    public void setPendingStatus(PendingStory.Status pendingStatus) {
        this.pendingStatus = pendingStatus;
    }

    public PendingStory.Status getPendingStatus() {
        return pendingStatus;
    }

    public String getLocalUid() {
        return localUid;
    }

    public void setLocalUid(String localUid) {
        this.localUid = localUid;
    }

    public enum Type { Text, Image }

    public static final Map<String, Type> typeFromString;
    static {
        Map<String, Type> map = new HashMap<>();
        map.put("Image", Type.Image);
        map.put("Text", Type.Text);
        typeFromString = Collections.unmodifiableMap(map);
    }

    public static final Map<Type, String> stringFromType;
    static {
        Map<Type, String> map = new HashMap<>();
        map.put(Type.Image, "Image");
        map.put(Type.Text, "Text");
        stringFromType = Collections.unmodifiableMap(map);
    }

    public static class WrapList implements Serializable {
        private static final long serialVersionUID = 2232982624031951655L;
        @SerializedName("stories")
        private List<Story> stories = new ArrayList<>();
        @SerializedName("nextRequestStartDate")
        private String nextStoryStartDate;
        @SerializedName("prevRequestStartDate")
        private String previousStoryStartDate;

        public Story getStory(int position) {
            return getStories().get(position);
        }

        public List<Story> getStories() {
            return stories;
        }

        public String getNextStoryStartDate() {
            return nextStoryStartDate;
        }

        public String getPreviousStoryStartDate() {
            return previousStoryStartDate;
        }

        public void setStories(List<Story> stories) {
            this.stories = stories;
        }

        public void addStories(List<Story> stories) {
            this.stories.addAll(stories);
        }

        public void addStories(int location, List<Story> stories) {
            this.stories.addAll(location, stories);
        }

        public void setNextStoryStartDate(String nextStoryStartDate) {
            this.nextStoryStartDate = nextStoryStartDate;
        }

        public void setPreviousStoryStartDate(String previousStoryStartDate) {
            this.previousStoryStartDate = previousStoryStartDate;
        }
    }
}
