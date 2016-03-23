package com.thirdandloom.storyflow.views.progress;

import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class ProgressBarController {
    private ProgressBarView progressBarView;
    private Window window;
    private int progressGravity = Gravity.CENTER;

    public ProgressBarController(Context context, Window window) {
        this.window = window;
        progressBarView = new ProgressBarView(context);
    }

    public void showProgress(int gravity) {
        progressGravity = gravity;
        removeProgressSubview();
        addProgressSubview();
    }

    public void showProgress() {
        hideProgress();
        addProgressSubview();
    }

    public void hideProgress() {
        removeProgressSubview();
        resetProgressGravity();
    }

    private void resetProgressGravity() {
        progressGravity = Gravity.CENTER;
    }

    private void removeProgressSubview() {
        getContainerView().removeView(progressBarView);
    }

    private void addProgressSubview() {
        ViewGroup container = getContainerView();
        progressBarView.setProgressGravity(progressGravity);
        progressBarView.setPadding(0, DeviceUtils.getStatusBarHeight(), 0, 0);
        WindowManager.LayoutParams layoutParams = ViewUtils.getFullScreenLayoutParams();
        layoutParams.height = DeviceUtils.getDisplayHeight();
        container.addView(progressBarView, layoutParams);
    }

    private ViewGroup getContainerView() {
        return (ViewGroup) window.getDecorView().getRootView();
    }
}
