package com.thirdandloom.storyflow.rest;

import com.thirdandloom.storyflow.config.Config;
import com.thirdandloom.storyflow.rest.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

public class RestClient {

    private static WebApi apiClient;

    public static WebApi getInstance() {
        if (apiClient == null) {

            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient okClient = new OkHttpClient.Builder().addInterceptor(interceptor).build();

            Retrofit client = new Retrofit.Builder()
                    .baseUrl(Config.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okClient)
                    .build();
            apiClient = client.create(WebApi.class);
        }
        return apiClient;
    }

    public interface WebApi {

    }

}
