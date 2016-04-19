package com.thirdandloom.storyflow.rest;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.SerializedName;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.Timber;
import org.json.JSONArray;
import retrofit2.Response;

import android.text.TextUtils;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
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

        try {
            JsonElement jsonElement = new JsonParser().parse(errorBodyString);

            if (jsonElement.getAsJsonObject().get("errors") instanceof JsonArray) {
                ApiError error = ApiError.newInstance(errorBodyString);
                return ApiError.getMessage(error);
            } else if (jsonElement.getAsJsonObject().get("errors") instanceof JsonPrimitive) {
                // TODO temp solution, should be removed after backend fixes:
                // https://www.pivotaltracker.com/story/show/117060705
                JsonPrimitive primitive = (JsonPrimitive) jsonElement.getAsJsonObject().get("errors");
                return primitive.getAsString();
            }

        } catch (JsonSyntaxException e) {
            Timber.e(e, e.getMessage());
            return unknownServerErrorMessage();
        }
        return unknownServerErrorMessage();
    }

    public static String getMessage(Throwable t) {
        if (t instanceof ConnectException || t instanceof SocketTimeoutException) {
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
            map.put("EXISTING_ACCOUNT", R.string.existing_account);
            map.put("EMPTY_USERNAME", R.string.empty_user_name);
            map.put("INVALID_USERNAME", R.string.invalid_user_name);
            map.put("EXISTING_USERNAME", R.string.user_name_exist);
            map.put("INVALID_EMAIL", R.string.email_is_invalid);
            map.put("THIS_EMAIL_IS_ALREADY_TAKEN", R.string.email_is_already_taken);

            codes = Collections.unmodifiableMap(map);
        }
    }
}
