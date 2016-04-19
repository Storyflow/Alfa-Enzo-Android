package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class SizeModel extends BaseModel {
    @SerializedName("width")
    private int width;
    @SerializedName("height")
    private int height;

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}
