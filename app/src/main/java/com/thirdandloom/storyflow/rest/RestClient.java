package com.thirdandloom.storyflow.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.CheckEmailRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignInRequestMode;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.support.annotation.Nullable;

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
