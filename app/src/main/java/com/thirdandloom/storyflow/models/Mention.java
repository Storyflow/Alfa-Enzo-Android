package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class Mention extends BaseModel {
    private static final long serialVersionUID = -4632449873394599539L;

    @SerializedName("userId")
    private String userId;
    @SerializedName("username")
    private String userName;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;

    public String getFullName() {
        return String.format("%s %s", firstName, lastName);
    }

    public String getUserName() {
        return userName;
    }

    public String getMentionUserName() {
        return String.format("@%s", userName);
    }
}
