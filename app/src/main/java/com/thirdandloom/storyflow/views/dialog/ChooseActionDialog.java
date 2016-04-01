package com.thirdandloom.storyflow.views.dialog;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;
import rx.functions.Action0;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class ChooseActionDialog extends DialogFragment {
    private Action0 onTakeNewPhotoAction;
    private Action0 onUploadPhotoAction;

    public static ChooseActionDialog newInstance(Action0 takePhotoAction, Action0 uploadPhotoAction) {
        ChooseActionDialog dialog = new ChooseActionDialog();
        dialog.onTakeNewPhotoAction = takePhotoAction;
        dialog.onUploadPhotoAction = uploadPhotoAction;

        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_choose_action, container, false);

        ViewUtils.getMeasuredSize(view, (width, height) -> {
            Window window = getDialog().getWindow();
            window.setGravity(Gravity.TOP | Gravity.LEFT);
            WindowManager.LayoutParams params = window.getAttributes();
            params.y = DeviceUtils.getDisplayHeight() - height;
            params.x = 0;
            window.setAttributes(params);
        });

        view.findViewById(R.id.dialog_choose_action_cancel).setOnClickListener(v -> {
            dismiss();
        });
        view.findViewById(R.id.dialog_choose_action_take_photo).setOnClickListener(v -> {
            dismiss();
            if (onTakeNewPhotoAction != null) onTakeNewPhotoAction.call();
        });
        view.findViewById(R.id.dialog_choose_action_upload_photo).setOnClickListener(v -> {
            dismiss();
            if (onUploadPhotoAction != null) onUploadPhotoAction.call();
        });

        return view;
    }

}
