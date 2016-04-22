package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.image.CroppedImage;

public class Author extends BaseModel {
    private static final long serialVersionUID = -4974172389611244699L;
    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String userName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("location")
    private String location;
    @SerializedName("profileUrl")
    private String avatarUrl;
    @SerializedName("profileImage")
    private CroppedImage croppedImageProfile;
    @SerializedName("coverImage")
    private CroppedImage croppedImageCover;

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getLocation() {
        return location;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public CroppedImage getCroppedImageProfile() {
        return croppedImageProfile;
    }

    public CroppedImage getCroppedImageCover() {
        return croppedImageCover;
    }
}
