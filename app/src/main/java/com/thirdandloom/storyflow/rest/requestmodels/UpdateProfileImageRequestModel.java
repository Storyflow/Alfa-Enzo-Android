package com.thirdandloom.storyflow.rest.requestmodels;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.image.ConvertRectUtils;
import com.thirdandloom.storyflow.utils.image.EncodeUtils;

public class UpdateProfileImageRequestModel extends BaseRequestModel {
    @SerializedName("rect")
    private String croppedRect;

    @SerializedName("cropped")
    private String imageData;

    public void setCroppedRect(RectF rectF) {
        this.croppedRect = ConvertRectUtils.getRectString(rectF);
    }

    public void setImageData(Bitmap imageData) {
        this.imageData = EncodeUtils.encodeToBase64(imageData, Bitmap.CompressFormat.JPEG, 100);
    }
}
