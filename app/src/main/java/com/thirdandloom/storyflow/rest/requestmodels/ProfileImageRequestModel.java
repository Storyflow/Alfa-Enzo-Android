package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.image.EncodeUtils;

import android.graphics.Bitmap;

public class ProfileImageRequestModel extends BaseRequestModel {
    @SerializedName("image")
    private String imageData;

    public void setImageData(Bitmap imageData) {
        this.imageData = EncodeUtils.encodeToBase64(imageData, Bitmap.CompressFormat.JPEG, 100);
    }

}
