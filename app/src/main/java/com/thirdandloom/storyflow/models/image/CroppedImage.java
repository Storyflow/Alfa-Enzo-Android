package com.thirdandloom.storyflow.models.image;

import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.BaseModel;
import com.thirdandloom.storyflow.utils.image.ConvertRectUtils;

public class CroppedImage extends BaseModel {
    private static final long serialVersionUID = -3431107892123815707L;
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
