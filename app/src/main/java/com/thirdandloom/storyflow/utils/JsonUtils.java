package com.thirdandloom.storyflow.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.annotation.Nullable;

public class JsonUtils {
    @Nullable
    public static <T> T parse(String json, Class<T> classOfT) {
        Gson gson = new Gson();
        T result = null;
        try {
            result = gson.fromJson(json, classOfT);
        } catch (JsonSyntaxException ex) {
            Timber.e(ex, ex.getMessage());
        }
        return result;
    }

    public static String toJson(Object object) {
        return new Gson().toJson(object);
    }

    public static JSONObject toJsonObject(Object object) {
        try {
            String jsonString = toJson(object);
            JSONObject jsonObject = new JSONObject(jsonString);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
