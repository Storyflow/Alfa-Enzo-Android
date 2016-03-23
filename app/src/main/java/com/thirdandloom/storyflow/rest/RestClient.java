package com.thirdandloom.storyflow.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import android.support.annotation.Nullable;

import java.net.ConnectException;

public class RestClient implements IRestClient {

    private final ApiService apiService;

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
        HttpLoggingInterceptor.Level logining = Config.REST_LOGGINING
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE;
        interceptor.setLevel(logining);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
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
                if (failureAction != null) failureAction.failure(response.message());
            }
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            if (failureAction == null) return;

            if (t instanceof ConnectException) {
                failureAction.failure(StoryflowApplication.getInstance().getResources().getString(R.string.no_internet_connection));
            }
        }
    }


    private interface ApiService {
        @Headers({
                "Accept: */*",
        })
        @POST("swan_user/sign_in")
        Call<User> signIn(@Body SignInRequestMode.Wrapper login);

        @Headers({
                "Accept: */*"
        })
        @POST("/swan_user")
        Call<User> signUp(@Body SignUpRequestModel.Wrapper signUp);
    }
}
