package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.Date;
import java.util.UUID;

public class PendingStory extends BaseModel {
    private static final long serialVersionUID = 856012311368902178L;

    public enum Status {
        WaitingForSend, ImageUploading, CreatingStory, CreateSucceed, CreateFailed, CreateImpossible
    }

    public enum Type {
        Text, Image
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
    private Type type;
    @SerializedName("localUid")
    private final String localUid = UUID.randomUUID().toString();

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

    public String getLocalUid() {
        return localUid;
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
}
