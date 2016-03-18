package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;

public class LoginRequestModel extends BaseRequestModel {
    @SerializedName("login")
    public String login;
    @SerializedName("password")
    public String password;

    public Wrapper wrap() {
        Wrapper wrapper = new Wrapper();
        wrapper.userData = this;
        return wrapper;
    }

    public static class Wrapper {
        @SerializedName("swan_user")
        public LoginRequestModel userData;
    }

}
