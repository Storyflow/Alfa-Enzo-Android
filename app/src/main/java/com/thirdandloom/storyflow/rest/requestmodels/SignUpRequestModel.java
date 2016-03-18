package com.thirdandloom.storyflow.rest.requestmodels;

import com.google.gson.annotations.SerializedName;

public class SignUpRequestModel extends BaseRequestModel {
    @SerializedName("email")
    public String email;
    @SerializedName("password")
    public String password;
    @SerializedName("firstName")
    public String firstName;
    @SerializedName("lastName")
    public String lastName;
    @SerializedName("passwordConfirmation")
    public String passwordConfirmation;
    @SerializedName("username")
    public String userName;

    public Wrapper wrap() {
        Wrapper wrapper = new Wrapper();
        wrapper.userData = this;
        return wrapper;
    }

    public static class Wrapper {
        @SerializedName("swan_user")
        public SignUpRequestModel userData;
    }

}
