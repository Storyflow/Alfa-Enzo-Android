package com.thirdandloom.storyflow.rest;

public interface IRestClient {
    void signIn(String login, String password,
                RestClient.ResponseCallback.ISuccess success,
                RestClient.ResponseCallback.IFailure failure);

    void checkEmail(String email,
            RestClient.ResponseCallback.ISuccess success,
            RestClient.ResponseCallback.IFailure failure);
}



