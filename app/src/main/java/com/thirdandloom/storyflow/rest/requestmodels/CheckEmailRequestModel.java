package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;

public class CheckEmailRequestModel extends BaseRequestModel {
    @SerializedName("email")
    public String email;
}
