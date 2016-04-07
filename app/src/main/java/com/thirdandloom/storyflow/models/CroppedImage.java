package com.thirdandloom.storyflow.models;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.utils.image.ConvertRectUtils;

public class CroppedImage extends BaseModel {
    @SerializedName("url")
    private String imageUrl;
    @SerializedName("rect")
    private String rect;

    public String getImageUrl() {
        return imageUrl;
    }

    @Nullable
    public Rect getRect() {
        return ConvertRectUtils.getRect(rect);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setRect(String rect) {
        this.rect = rect;
    }

    public void setRect(RectF rectF) {
        this.rect = ConvertRectUtils.getRectString(rectF);
    }
}
