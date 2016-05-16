package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.image.EncodeUtils;

import android.graphics.Bitmap;

public class UploadImageRequestModel extends BaseRequestModel {
    @SerializedName("storyType")
    public String storyType;
    @SerializedName("image")
    public String imageData;

    public UploadImageRequestModel(Bitmap imageData) {
        this.imageData = EncodeUtils.encodeToBase64(imageData, Bitmap.CompressFormat.JPEG, 100);
        this.storyType = "Image";
    }
}
