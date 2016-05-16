package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.rest.RestClient;
import com.thirdandloom.storyflow.utils.DateUtils;

public class PostTextStoryRequestModel extends BaseRequestModel {
    @SerializedName("description")
    public String description;
    @SerializedName("storyType")
    public String storyType;
    @SerializedName("privacyId")
    public String privacyId;
    @SerializedName("storyDate")
    public String storyDate;

    public PostTextStoryRequestModel(PendingStory pendingStory) {
        this.description = pendingStory.getDescription();
        this.storyType = "Text";
        this.privacyId = "3";
        this.storyDate = DateUtils.getDateString("yyyy/MM/dd", pendingStory.getDate());
    }

}
