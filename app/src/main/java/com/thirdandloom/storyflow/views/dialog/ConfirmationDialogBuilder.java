package com.thirdandloom.storyflow.views.dialog;

import com.thirdandloom.storyflow.R;

import android.app.Activity;
import android.support.annotation.NonNull;

public class ConfirmationDialogBuilder extends DialogBuilder {
    public ConfirmationDialogBuilder(@NonNull Activity activity) {
        super(activity);
        positiveText(R.string.ok);
        negativeText(R.string.cancel);
    }
}
