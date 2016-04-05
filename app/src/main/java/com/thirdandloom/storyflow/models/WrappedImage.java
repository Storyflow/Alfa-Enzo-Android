package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class WrappedImage extends BaseModel {
    @SerializedName("url")
    private String url;

    public String url() {
        return url;
    }
}
