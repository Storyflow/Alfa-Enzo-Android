package com.thirdandloom.storyflow.rest;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.Encoder;
import com.bumptech.glide.load.model.ImageVideoWrapper;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.google.common.base.Utf8;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.ProfileImageRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;
import com.thirdandloom.storyflow.utils.glide.CropRectTransformation;
import com.thirdandloom.storyflow.utils.glide.LogCrashRequestListener;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

public class RestClient implements IRestClient {

    private final IRestClient.ApiService apiService;

    public RestClient() {
        Retrofit client = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(createGsonFactory())
                .client(createOkClient())
                .build();
        apiService = client.create(ApiService.class);
    }

    private Converter.Factory createGsonFactory() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                .create();
        return GsonConverterFactory.create(gson);
    }

    private static OkHttpClient createOkClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level loggining = Config.REST_LOGGINING
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE;
        interceptor.setLevel(loggining);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .build();
        return okClient;
    }

    @Override
    public void signIn(String login, String password, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        SignInRequestMode model = new SignInRequestMode();
        model.login = login;
        model.password = password;

        apiService.signIn(model.wrap()).enqueue(new ResponseCallback<>(success, failure));
    }

    @Override
    public void checkEmail(String email, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        CheckEmailRequestModel model = new CheckEmailRequestModel();
        model.email = email;

        apiService.checkEmail(model).enqueue(new ResponseCallback<>(success, failure));
    }

    @Override
    public void signUp(String email, String userName, String password, String firstName, String lastName,
            ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        SignUpRequestModel model = new SignUpRequestModel();
        model.email = email;
        model.userName = userName;
        model.password = password;
        model.passwordConfirmation = password;
        model.firstName = firstName;
        model.lastName = lastName;

        apiService.signUp(model.wrap()).enqueue(new ResponseCallback<>(success, failure));
    }

    @Override
    public void createProfileImage(DrawableTypeRequest glideRequest, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        //glideRequest.asBitmap().toBytes(Bitmap.CompressFormat.JPEG, 100).into(new SimpleTarget<byte[]>() {
        //    @Override
        //    public void onResourceReady(byte[] resource, GlideAnimation<? super byte[]> ignore) {
        //        StoryflowApplication.runBackground(() -> {
        //            ProfileImageRequestModel model = new ProfileImageRequestModel();
        //            model.setImageData(resource);
        //            apiService.createProfileImage(model).enqueue(new ResponseCallback<>(success, failure));
        //        });
        //    }
        //
        //    @Override
        //    public void onLoadFailed(Exception ex, Drawable ignore) {
        //        Timber.e(ex, ex.getMessage());
        //        failure.failure(StoryflowApplication.resources().getString(R.string.error_while_loading_image));
        //    }
        //});

        glideRequest.asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                    StoryflowApplication.runBackground(() -> {
                        ProfileImageRequestModel model = new ProfileImageRequestModel();
                        model.setImageData(bitmap);
                        apiService.createProfileImage(model).enqueue(new ResponseCallback<>(success, failure));
                    });
            }
        });
    }

    public static class ResponseCallback<T> implements Callback<T> {
        public interface ISuccess<T> {
            void sucess(T responseBody);
        }

        public interface IFailure {
            void failure(String errorMessage);
        }

        private ISuccess successAction;
        private IFailure failureAction;

        public ResponseCallback(@Nullable ISuccess successAction, @Nullable IFailure failureAction) {
            this.successAction = successAction;
            this.failureAction = failureAction;
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (successAction == null) return;

            if (response.isSuccessful()) {
                successAction.sucess(response.body());
            } else {
                if (failureAction != null) {
                    failureAction.failure(ErrorHandler.getMessage(response));
                }
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            if (failureAction != null) {
                failureAction.failure(ErrorHandler.getMessage(t));
            }
        }
    }
}
