package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.image.SizedImage;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;
import java.util.UUID;

public class PendingStory extends BaseModel {
    private static final long serialVersionUID = 856012311368902178L;

    public enum Status {
        WaitingForSend, ImageUploading, CreatingStory, CreateSucceed, CreateFailed, CreateImpossible, OnServer
    }

    @SerializedName("status")
    private Status status = Status.WaitingForSend;
    @SerializedName("description")
    private String description;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("storyId")
    private String storyId;
    @SerializedName("date")
    private Date date;
    @SerializedName("type")
    private Story.Type type;
    @SerializedName("localUid")
    private final String localUid = UUID.randomUUID().toString();

    public Status getStatus() {
        return status;
    }

    public void setData(@Nullable String description, @Nullable String imageUrl, @NonNull Date date) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        if (TextUtils.isEmpty(imageUrl)) {
            this.type = Story.Type.Text;
        } else {
            this.type = Story.Type.Image;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public Story.Type getType() {
        return type;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Date getDate() {
        return date;
    }

    public void setStoryId(String storyId) {
        this.storyId = storyId;
    }

    public String getStoryId() {
        return storyId;
    }

    public String getLocalUid() {
        return localUid;
    }

    public String getStringType() {
        return  Story.stringFromType.get(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PendingStory)) {
            return false;
        }
        PendingStory that = (PendingStory) o;
        return this.localUid.equals(that.localUid);
    }

    public Story convertToStory() {
        Story story = new Story();
        User user = StoryflowApplication.account().getUser();
        story.setAuthor(user.convertToAuthor());
        story.setDate(date);
        story.setDescription(description);

        SizeModel sizeModel = new SizeModel();
        sizeModel.setHeight(0);
        sizeModel.setWidth(0);
        SizedImage sizedImage = new SizedImage();
        sizedImage.setUrl(imageUrl);
        sizedImage.setSize(sizeModel);
        StoryImageModel imageModel = new StoryImageModel();
        imageModel.setNormalSizedImage(sizedImage);
        imageModel.setCollapsedSizedImage(sizedImage);
        imageModel.setExpandedSizedImage(sizedImage);

        story.setImageData(imageModel);
        story.setType(getStringType());
        story.setPendingStatus(status);
        story.setLocalUid(localUid);

        return story;
    }
}
