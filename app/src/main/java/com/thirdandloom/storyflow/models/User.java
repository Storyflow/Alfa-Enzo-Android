package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class User extends BaseModel {
    @SerializedName("id")
    private int id;
    @SerializedName("email")
    private String email;
    @SerializedName("lastRequestAt")
    private String lastRequestedAt;
    @SerializedName("login")
    private String login;
    @SerializedName("shipAddressId")
    private int shipAddressId;
    @SerializedName("billAddressId")
    private int billAddressId;
    @SerializedName("createdAt")
    private Date createdAt;
    @SerializedName("updatedAt")
    private Date updatedAt;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("defaultMeasurementsId")
    private int defaultMeasurementsId;
    @SerializedName("continueRegistrationToken")
    private boolean continueRegistrationToken;
    @SerializedName("identityId")
    private int identityId;
    @SerializedName("location")
    private String uid;
    @SerializedName("personalQuote")
    private String personalQuote;
    @SerializedName("username")
    private String username;
    @SerializedName("confirmed")
    private boolean confirmed;
    @SerializedName("lastSignOut")
    private String lastSignOut;
    @SerializedName("coverBackgroundColor")
    private String coverBackgroundColor;
    @SerializedName("online")
    private String online;
    @SerializedName("profileImage")
    private CroppedImage profileImage;
    @SerializedName("profileImages")
    private List<Avatar> profileImages;
    @SerializedName("coverImage")
    private CroppedImage coverImage;
    @SerializedName("coverImages")
    private List<Avatar> coverImages;
    @SerializedName("onlineImage")
    private WrappedImage onlineImage;
    @SerializedName("chatImage")
    private WrappedImage chatImage;

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getLogin() {
        return login;
    }

    public String getLastRequestedAt() {
        return lastRequestedAt;
    }

    public int getShipAddressId() {
        return shipAddressId;
    }

    public int getBillAddressId() {
        return billAddressId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public int getDefaultMeasurementsId() {
        return defaultMeasurementsId;
    }

    public boolean isContinueRegistrationToken() {
        return continueRegistrationToken;
    }

    public String getPersonalQuote() {
        return personalQuote;
    }

    public int getIdentityId() {
        return identityId;
    }

    public String getUid() {
        return uid;
    }

    public String getUsername() {
        return username;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getCoverBackgroundColor() {
        return coverBackgroundColor;
    }

    public String getLastSignOut() {
        return lastSignOut;
    }

    public String getOnline() {
        return online;
    }

    public CroppedImage getProfileImage() {
        return profileImage;
    }

    public List<Avatar> getProfileImages() {
        return profileImages;
    }

    public void setProfileImages(List<Avatar> profileImages) {
        this.profileImages = profileImages;
    }

    public CroppedImage getCoverImage() {
        return coverImage;
    }

    public WrappedImage getOnlineImage() {
        return onlineImage;
    }

    public List<Avatar> getCoverImages() {
        return coverImages;
    }

    public WrappedImage getChatImage() {
        return chatImage;
    }

    public void setProfileImage(CroppedImage profileImage) {
        this.profileImage = profileImage;
    }
}
