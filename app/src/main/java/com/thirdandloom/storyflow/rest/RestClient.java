package com.thirdandloom.storyflow.rest;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.rest.cookies.JavaNetCookieJar;
import com.thirdandloom.storyflow.rest.cookies.PersistentCookieStore;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.ProfileImageRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.UpdateProfileImageRequestModel;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.glide.CropRectTransformation;
import com.thirdandloom.storyflow.utils.image.Size;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.concurrent.TimeUnit;

public class RestClient implements IRestClient {

    private final IRestClient.ApiService apiService;
    private CookieManager cookieManager;

    public RestClient(Context context) {
        Retrofit client = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(gsonFactory())
                .client(okHttpClient(context))
                .build();
        apiService = client.create(ApiService.class);
    }

    private Converter.Factory gsonFactory() {
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd HH:mm:ss Z")
                .create();
        return GsonConverterFactory.create(gson);
    }

    private OkHttpClient okHttpClient(Context context) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level logging = Config.REST_LOGGINING
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE;
        loggingInterceptor.setLevel(logging);

        cookieManager = new CookieManager(new PersistentCookieStore(context), CookiePolicy.ACCEPT_ALL);

        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .connectTimeout(120, TimeUnit.SECONDS)
                .cookieJar(new JavaNetCookieJar(cookieManager))
                .build();
        return okClient;
    }

    @Override
    public void clearCookies() {
        cookieManager.getCookieStore().removeAll();
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
    public void createProfileImage(DrawableTypeRequest glideRequest, Size imageSize,
                                   ResponseCallback.ISuccess success,
                                   ResponseCallback.IFailure failure) {
        glideRequest.asBitmap().override(imageSize.width(), imageSize.height())
                .into(new SimpleTarget<Bitmap>() {
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

    @Override
    public void createCroppedProfileImage(DrawableTypeRequest glideRequest, int id, Size imageSize, RectF croppedRect,
                                          ResponseCallback.ISuccess success,
                                          ResponseCallback.IFailure failure) {
        glideRequest.asBitmap().override(imageSize.width(), imageSize.height())
                .transform(new CropRectTransformation(StoryflowApplication.getInstance(), croppedRect))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        StoryflowApplication.runBackground(() -> {
                            UpdateProfileImageRequestModel model = new UpdateProfileImageRequestModel();
                            model.setCroppedRect(croppedRect);
                            model.setImageData(bitmap);

                            apiService.updateProfileImage(id, model).enqueue(new ResponseCallback<>(success, failure));
                        });
                    }
                });
    }

    public static class ResponseCallback<T> implements Callback<T> {
        public interface ISuccess<T> {
            void success(T responseBody);
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
                successAction.success(response.body());
            } else {
                if (failureAction != null) {
                    failureAction.failure(ErrorHandler.getMessage(response));
                }
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            Timber.e(t, t.getMessage());
            if (failureAction != null) {
                failureAction.failure(ErrorHandler.getMessage(t));
            }
        }
    }
}
