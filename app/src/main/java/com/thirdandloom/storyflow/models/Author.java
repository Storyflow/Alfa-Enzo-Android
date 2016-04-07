package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class Author extends BaseModel {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("location")
    private String location;
    @SerializedName("profileUrl")
    private String avatarUrl;
}
