package com.thirdandloom.storyflow.rest;

import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface IRestClient {
    void signIn(String login, String password,
                RestClient.ResponseCallback.ISuccess<User> success,
                RestClient.ResponseCallback.IFailure failure);

    void checkEmail(String email,
            RestClient.ResponseCallback.ISuccess success,
            RestClient.ResponseCallback.IFailure failure);

    interface ApiService {
        @Headers({
                "Accept: */*",
        })
        @POST("swan_user/sign_in")
        Call<User> signIn(@Body SignInRequestMode.Wrapper login);

        @Headers({
                "Accept: */*",
        })
        @POST("swan_user/lost_password")
        Call<ResponseBody> checkEmail(@Body CheckEmailRequestModel email);

        @Headers({
                "Accept: */*"
        })
        @POST("/swan_user")
        Call<User> signUp(@Body SignUpRequestModel.Wrapper signUp);
    }
}



