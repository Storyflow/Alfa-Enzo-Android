package com.thirdandloom.storyflow.models;

import com.google.gson.annotations.SerializedName;

public class Likes extends BaseModel {
    private static final long serialVersionUID = 5889299700998244934L;

    @SerializedName("count")
    private int count;
    @SerializedName("current")
    private boolean containsCurrentUserLike;
    @SerializedName("last")
    private Author lastLikeAuthor;

    public int getCount() {
        return count;
    }

    public Author getLastLikeAuthor() {
        return lastLikeAuthor;
    }

    public boolean containsCurrentUserLike() {
        return containsCurrentUserLike;
    }
}
