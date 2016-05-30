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

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public CroppedImage getCroppedAvatar() {
        return croppedImageProfile;
    }

    public CroppedImage getCroppedImageCover() {
        return croppedImageCover;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCroppedImageProfile(CroppedImage croppedImageProfile) {
        this.croppedImageProfile = croppedImageProfile;
    }

    public void setCroppedImageCover(CroppedImage croppedImageCover) {
        this.croppedImageCover = croppedImageCover;
    }
}
