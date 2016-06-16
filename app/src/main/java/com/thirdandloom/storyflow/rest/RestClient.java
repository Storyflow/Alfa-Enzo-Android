package com.thirdandloom.storyflow.rest;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.PendingStory;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.models.StoryId;
import com.thirdandloom.storyflow.rest.cookies.JavaNetCookieJar;
import com.thirdandloom.storyflow.rest.cookies.PersistentCookieStore;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.LikeStoryActionRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.PostImageStoryRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.PostTextStoryRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.ProfileImageRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.UpdateProfileImageRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.UploadImageRequestModel;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.concurrent.ThreadUtils;
import com.thirdandloom.storyflow.utils.glide.CropRectTransformation;
import com.thirdandloom.storyflow.utils.image.Size;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import rx.functions.Action0;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class RestClient implements IRestClient {
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss Z";

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
                .setDateFormat(DATE_FORMAT)
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
        sendAsync(apiService.signIn(model.wrap()), new ResponseCallback<>(success, failure));
    }

    @Override
    public void checkEmail(String email, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        CheckEmailRequestModel model = new CheckEmailRequestModel();
        model.email = email;
        sendAsync(apiService.checkEmail(model), new ResponseCallback<>(success, failure));
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
        sendAsync(apiService.signUp(model.wrap()), new ResponseCallback<>(success, failure));
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
                            sendAsync(apiService.createProfileImage(model), new ResponseCallback<>(success, failure));
                        });
                    }
                });
    }

    @Override
    public void createCroppedProfileImage(DrawableTypeRequest glideRequest, int id, Size imageSize, RectF croppedRect,
                                          ResponseCallback.ISuccess success,
                                          ResponseCallback.IFailure failure) {
        glideRequest.asBitmap().override(imageSize.width(), imageSize.height())
                .transform(new CropRectTransformation(StoryflowApplication.applicationContext, croppedRect))
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(Bitmap bitmap, GlideAnimation glideAnimation) {
                        StoryflowApplication.runBackground(() -> {
                            UpdateProfileImageRequestModel model = new UpdateProfileImageRequestModel();
                            model.setCroppedRect(croppedRect);
                            model.setImageData(bitmap);
                            sendAsync(apiService.updateProfileImage(id, model), new ResponseCallback<>(success, failure));
                        });
                    }
                });
    }

    @Override
    public void loadStories(StoriesManager.RequestData requestData, ResponseCallback.ISuccess<Story.WrapList> success, ResponseCallback.IFailure failure) {
        String direction = requestData.getDirection();
        String period = requestData.getPeriod();
        Map filters = requestData.getFilters();
        int limit = requestData.getLimit();
        String startDate = requestData.getNextStoryDate();

        sendAsync(apiService.loadStories(period, limit, startDate, direction, filters), new ResponseCallback<>(success, failure));
    }

    @Override
    public void createTextStorySync(PendingStory pendingStory, ResponseCallback.ISuccess<Story> success, ResponseCallback.IFailure failure) {
        PostTextStoryRequestModel requestModel = new PostTextStoryRequestModel(pendingStory);
        sendSync(apiService.createTextStory(requestModel), new ResponseCallback<>(success, failure));
    }

    @Override
    public void createImageStorySync(PendingStory pendingStory, ResponseCallback.ISuccess<Story> success, ResponseCallback.IFailure failure) {
        PostImageStoryRequestModel requestModel = new PostImageStoryRequestModel(pendingStory);
        sendSync(apiService.createImageStory(requestModel), new ResponseCallback<>(success, failure));
    }

    @Override
    public void uploadImageSync(PendingStory pendingStory, Action0 uploadImpossible, ResponseCallback.ISuccess<StoryId> success, ResponseCallback.IFailure failure) {
        Bitmap imageBitmap;
        try {
            imageBitmap =  Glide
                    .with(StoryflowApplication.applicationContext)
                    .load(pendingStory.getImageUrl())
                    .asBitmap()
                    .centerCrop()
                    .into(pendingStory.getImageWidth(), pendingStory.getImageHeight())
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            ErrorHandler.getMessage(e, failure::failure);
            return;
        }
        if (imageBitmap == null) {
            uploadImpossible.call();
            return;
        }
        UploadImageRequestModel model = new UploadImageRequestModel(imageBitmap);

        sendSync(apiService.uploadImage(model), new ResponseCallback<>(success, failure));
    }

    @Override
    public void logout(ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        sendAsync(apiService.logout(), new ResponseCallback<>(success, failure));
    }

    @Override
    public void likeStory(String storyId, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        sendAsync(apiService.like(new LikeStoryActionRequestModel(storyId)), new ResponseCallback<>(success, failure));
    }

    @Override
    public void dislikeStory(String storyId, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        sendAsync(apiService.dislike(storyId, "story"), new ResponseCallback<>(success, failure));
    }

    public static <T> void sendAsync(Call<T> call, Callback<T> responseCallback) {
        call.enqueue(responseCallback);
    }

    public static <T> void sendSync(Call<T> call, Callback<T> responseCallback) {
        ThreadUtils.assertBackgroundThread();
        try {
            responseCallback.onResponse(call, call.execute());
        } catch (IOException e) {
            responseCallback.onFailure(call, e);
        }
    }

    public static class ResponseCallback<T> implements Callback<T> {
        public interface ISuccess<T> {
            void success(T responseBody);
        }

        public interface IFailure {
            void failure(String errorMessage, ErrorHandler.Type errorType);
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
                    ErrorHandler.getMessage(response, failureAction::failure);
                }
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            Timber.e(t, t.getMessage());
            if (failureAction != null) {
                ErrorHandler.getMessage(t, failureAction::failure);
            }
        }
    }
}
