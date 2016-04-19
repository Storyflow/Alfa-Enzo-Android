package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.image.WrappedImage;

import java.util.Date;

public class Avatar extends BaseModel {
    @SerializedName("id")
    private int id;
    @SerializedName("activated")
    private boolean activated;
    @SerializedName("swan_user_id")
    private int userId;
    @SerializedName("created_at")
    private Date createdAt;
    @SerializedName("updated_at")
    private Date updatedAt;
    @SerializedName("rect")
    private String croppedRect;
    @SerializedName("cropped")
    private WrappedImage croppedImage;
    @SerializedName("image")
    private WrappedImage image;

    public int getId() {
        return id;
    }

    public boolean isActivated() {
        return activated;
    }

    public int getUserId() {
        return userId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getCroppedRect() {
        return croppedRect;
    }

    public WrappedImage getCroppedImage() {
        return croppedImage;
    }

    public WrappedImage getImage() {
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Avatar)) {
            return false;
        }
        Avatar avatar = (Avatar) o;
        return avatar.getId() == getId();
    }
}
