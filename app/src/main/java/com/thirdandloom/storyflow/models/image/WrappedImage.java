package com.thirdandloom.storyflow.models.image;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.BaseModel;

public class WrappedImage extends BaseModel {
    private static final long serialVersionUID = -6806005568392622368L;
    @SerializedName("url")
    private String url;

    public String url() {
        return url;
    }
}
