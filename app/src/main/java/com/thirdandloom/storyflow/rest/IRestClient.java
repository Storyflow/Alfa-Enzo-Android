package com.thirdandloom.storyflow.rest;

import android.graphics.RectF;

import com.bumptech.glide.DrawableTypeRequest;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Avatar;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.ProfileImageRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.UpdateProfileImageRequestModel;
import com.thirdandloom.storyflow.utils.image.Size;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

import java.util.Map;

public interface IRestClient {
    void signIn(String login, String password,
                RestClient.ResponseCallback.ISuccess<User> success,
                RestClient.ResponseCallback.IFailure failure);

    void checkEmail(String email,
            RestClient.ResponseCallback.ISuccess success,
            RestClient.ResponseCallback.IFailure failure);

    void signUp(String email, String userName, String password, String firstName, String lastName,
            RestClient.ResponseCallback.ISuccess<User> success,
            RestClient.ResponseCallback.IFailure failure);

    void createProfileImage(DrawableTypeRequest glideRequest,
            Size imageSize,
            RestClient.ResponseCallback.ISuccess<Avatar> success,
            RestClient.ResponseCallback.IFailure failure);

    void createCroppedProfileImage(DrawableTypeRequest glideRequest,
                            int id, Size imageSize, RectF croppedRect,
                            RestClient.ResponseCallback.ISuccess<Avatar> success,
                            RestClient.ResponseCallback.IFailure failure);

    void loadStories(StoriesManager.RequestData requestData,
            RestClient.ResponseCallback.ISuccess<Story.WrapList> success,
            RestClient.ResponseCallback.IFailure failure);

    void clearCookies();

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

        @Headers({
                "Accept: */*"
        })
        @POST("/swan/account/profile_images/")
        Call<Avatar> createProfileImage(@Body ProfileImageRequestModel signUp);

        @Headers({
                "Accept: */*"
        })
        @PUT("/swan/account/profile_images/{fullImageId}")
        Call<Avatar> updateProfileImage(@Path("fullImageId") int fullImageId, @Body UpdateProfileImageRequestModel signUp);

        @Headers({
                "Accept: */*",
        })
        @GET("/swan/stories/")
        Call<Story.WrapList> loadStories(@Query("period") String period,
                @Query("limit") int storiesLimit,
                @Query("startStoryId") String startStoryId,
                @Query("direction") String direction,
                @QueryMap Map<String, String> filters);
    }
}
