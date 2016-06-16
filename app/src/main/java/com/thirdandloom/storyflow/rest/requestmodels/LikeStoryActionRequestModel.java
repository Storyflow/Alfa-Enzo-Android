package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;

public class LikeStoryActionRequestModel extends BaseRequestModel {

    @SerializedName("id")
    private String id;
    @SerializedName("type")
    private String type = "story";

    public LikeStoryActionRequestModel(String id) {
        this.id = id;
    }
}
