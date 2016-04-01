package com.thirdandloom.storyflow.utils;

import com.thirdandloom.storyflow.StoryflowApplication;

import android.content.Context;
import android.content.res.Resources;

public abstract class BaseUtils  {

    protected static Context getContext() {
        return StoryflowApplication.getInstance();
    }

    protected static Resources getResources() {
        return getContext().getResources();
    }

    protected static void exception(Throwable throwable) {
        Timber.e(throwable, throwable.getMessage());
    }
}
