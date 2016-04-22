package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.image.SizedImage;

public class StoryImageModel extends BaseModel {
    private static final long serialVersionUID = -1925998522422167654L;
    @SerializedName("normal")
    private SizedImage normalSizedImage;
    @SerializedName("collapsed")
    private SizedImage collapsedSizedImage;
    @SerializedName("expanded")
    private SizedImage expandedSizedImage;

    public SizedImage getNormalSizedImage() {
        return normalSizedImage;
    }

    public SizedImage getCollapsedSizedImage() {
        return collapsedSizedImage;
    }

    public SizedImage getExpandedSizedImage() {
        return expandedSizedImage;
    }
}
