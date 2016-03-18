package com.thirdandloom.storyflow.rest;

import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import com.thirdandloom.storyflow.rest.requestmodels.LoginRequestModel;
import com.thirdandloom.storyflow.rest.requestmodels.SignUpRequestModel;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

import android.support.annotation.Nullable;

public class RestClient implements IRestClient {

    private final ApiService apiService;

    public RestClient() {
        Retrofit client = new Retrofit.Builder()
                .baseUrl(Config.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkClient())
                .build();
        apiService = client.create(ApiService.class);
    }

    private static OkHttpClient createOkClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        HttpLoggingInterceptor.Level logginig = Config.REST_LOGGINING
                ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE;
        interceptor.setLevel(logginig);
        OkHttpClient okClient = new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .build();
        return okClient;
    }

    @Override
    public void signIn(String login, String password, ResponseCallback.ISuccess success, ResponseCallback.IFailure failure) {
        LoginRequestModel model = new LoginRequestModel();
        model.login = login;
        model.password = password;

        apiService.signIn(model.wrap()).enqueue(new ResponseCallback<>(success, failure));
    }

    public static class ResponseCallback<T> implements Callback<T> {
        public interface ISuccess<T> {
            void sucess(T responseBody);
        }

        public interface IFailure {
            void failure();
        }

        private ISuccess successAction;
        private IFailure failureAction;

        public ResponseCallback(@Nullable ISuccess successAction, @Nullable IFailure failureAction) {
            this.successAction = successAction;
            this.failureAction = failureAction;
        }

        @Override
        public void onResponse(Call call, Response response) {
            if (successAction != null) successAction.sucess(response.body());
        }

        @Override
        public void onFailure(Call call, Throwable t) {
            if (failureAction != null) failureAction.failure();
        }
    }


    private interface ApiService {
        @Headers({
                "Accept: */*"
        })
        @POST("swan_user/sign_in")
        Call<User> signIn(@Body LoginRequestModel.Wrapper login);

        @Headers({
                "Accept: */*"
        })
        @POST("/swan_user")
        Call<User> signUp(@Body SignUpRequestModel.Wrapper signUp);
    }
}
