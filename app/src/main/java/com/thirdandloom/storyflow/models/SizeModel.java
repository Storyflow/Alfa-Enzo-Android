package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class SizeModel extends BaseModel {
    private static final long serialVersionUID = 6934178384535056054L;
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

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
