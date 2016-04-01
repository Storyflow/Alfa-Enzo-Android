package com.thirdandloom.storyflow.utils.glide;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.thirdandloom.storyflow.utils.Timber;

public class LogCrashRequestListener<T, R> implements RequestListener<T, R> {

    @Override
    public boolean onException(Exception e, T model, Target<R> target, boolean isFirstResource) {
        if (e != null) Timber.e(e, model.toString());
        return false;
    }

    @Override
    public boolean onResourceReady(R resource, T model, Target<R> target, boolean isFromMemoryCache, boolean isFirstResource) {
        return false;
    }
}
