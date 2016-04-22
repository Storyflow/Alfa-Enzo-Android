package com.thirdandloom.storyflow.models.image;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.BaseModel;
import com.thirdandloom.storyflow.models.SizeModel;

public class SizedImage extends BaseModel {
    private static final long serialVersionUID = 8935208818566333931L;
    @SerializedName("url")
    private String url;
    @SerializedName("size")
    private SizeModel size;

    public String url() {
        return url;
    }

    public SizeModel size() {
        return size;
    }
}
