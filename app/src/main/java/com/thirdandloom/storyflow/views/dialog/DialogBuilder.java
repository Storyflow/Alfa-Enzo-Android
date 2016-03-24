package com.thirdandloom.storyflow.views.dialog;

import com.afollestad.materialdialogs.MaterialDialog;
import com.thirdandloom.storyflow.R;

import android.app.Activity;
import android.support.annotation.NonNull;

public class DialogBuilder extends MaterialDialog.Builder {
    public DialogBuilder(@NonNull Activity activity) {
        super(activity);
        int accentColorRes = R.color.grey;
        positiveColorRes(accentColorRes);
        neutralColorRes(accentColorRes);
        negativeColorRes(accentColorRes);
    }
}
