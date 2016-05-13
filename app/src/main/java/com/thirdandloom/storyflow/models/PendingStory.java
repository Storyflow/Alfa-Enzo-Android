package com.thirdandloom.storyflow.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;

public class PendingStory extends BaseModel {
    private static final long serialVersionUID = 856012311368902178L;

    public enum Status {
        WaitingForSend, ImageUploading, CreatingStory, CreateSucceed, CreateFailed, CreateImpossible
    }

    public enum Type {
        Text, Image
    }

    private Status status = Status.WaitingForSend;
    private String description;
    private String imageUrl;
    private String storyId;
    private Date date;
    private Type type;

    public Status getStatus() {
        return status;
    }

    public void setData(@NonNull String description, @Nullable String imageUrl, @NonNull Date date) {
        this.description = description;
        this.imageUrl = imageUrl;
        this.date = date;
        if (TextUtils.isEmpty(imageUrl)) {
            this.type = Type.Text;
        } else {
            this.type = Type.Image;
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public Type getType() {
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
}
