package com.thirdandloom.storyflow.utils.event;

import android.support.annotation.StringRes;

public class ShowWarningEvent {
    private int resId;

    public ShowWarningEvent(@StringRes int resId) {
        this.resId = resId;
    }

    @StringRes
    public int getMessageResId() {
        return resId;
    }
}
