package com.thirdandloom.storyflow.rest;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.Timber;
import retrofit2.Response;

import android.text.TextUtils;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ErrorHandler {

    public static String getMessage(Response response) {
        String errorBodyString;
        try {
            errorBodyString = response.errorBody().string();
        } catch (IOException e) {
            Timber.e(e, response.message());
            return unknownServerErrorMessage();
        }
        if (TextUtils.isEmpty(errorBodyString)) return unknownServerErrorMessage();

        ApiError error = ApiError.newInstance(errorBodyString);
        return ApiError.getMessage(error);
    }

    public static String getMessage(Throwable t) {
        if (t instanceof ConnectException) {
            return StoryflowApplication.resources().getString(R.string.no_internet_connection);
        } else {
            return StoryflowApplication.resources().getString(R.string.unknown_error);
        }
    }

    private static String unknownServerErrorMessage() {
        return StoryflowApplication.resources().getString(R.string.unknown_server_error);
    }

    public static class ApiError {
        @SerializedName("errors")
        private List<String> codes;

        public static ApiError newInstance(String responseBody) {
            return new Gson().fromJson(responseBody, ApiError.class);
        }

        public static String getMessage(ApiError apiError) {
            return apiError != null
                    ? apiError.getMessage()
                    : unknownServerErrorMessage();
        }

        public String getMessage() {
            if (ArrayUtils.isEmpty(codes)) return unknownServerErrorMessage();

            String code = codes.get(0);
            return ApiErrors.codes.containsKey(code)
                    ? StoryflowApplication.resources().getString(ApiErrors.codes.get(code))
                    : unknownServerErrorMessage();
        }
    }

    public static class ApiErrors {
        private static final Map<String, Integer> codes;

        static {
            Map<String, Integer> map = new HashMap<>();
            map.put("NOT_EXISTENT_EMAIL", R.string.not_existent_email);
            codes = Collections.unmodifiableMap(map);
        }
    }
}
