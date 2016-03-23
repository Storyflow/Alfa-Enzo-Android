package com.thirdandloom.storyflow.views.alert;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AnimationUtils;
import com.thirdandloom.storyflow.utils.CancelableRunnable;

import android.graphics.PixelFormat;
import android.support.annotation.UiThread;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public class QuickAlertController {
    private static final int ALERT_DISPLAYING_TIME = 2000;

    private Window window;
    private QuickAlertView mQuickAlertView;
    private CancelableRunnable hideAlerRunnable;

    public QuickAlertController(Window window) {
        this.window = window;
    }

    @UiThread
    public void show(String message, QuickAlertView.Type type) {
        hidePreviousAlert();
        showQuickAlert(message, type);
        hideAlerRunnable = createHideAlertRunnable();
        mQuickAlertView.postDelayed(hideAlerRunnable, ALERT_DISPLAYING_TIME);
        mQuickAlertView.setOnClickListener(v -> hideAlertAnimate());
    }

    public void hide() {
        if (mQuickAlertView != null && mQuickAlertView.isOnScreen()) {
            ViewGroup viewGroup = (ViewGroup) mQuickAlertView.getParent();
            viewGroup.removeView(mQuickAlertView);
        }
    }

    private void showQuickAlert(String message, QuickAlertView.Type type) {
        mQuickAlertView = new QuickAlertView(StoryflowApplication.getInstance());
        mQuickAlertView.setText(message, type);
        window.addContentView(mQuickAlertView, createLayoutParams());
        mQuickAlertView.measure(0, 0);
        AnimationUtils.showHeader(mQuickAlertView, 200);
    }

    private void hidePreviousAlert() {
        if (mQuickAlertView != null) {
            cancelPreviousRunnable();
            hideAlertAnimate();
        }
    }

    private void cancelPreviousRunnable() {
        hideAlerRunnable.cancel();
    }

    private void hideAlertAnimate() {
        AnimationUtils.hideHeader(mQuickAlertView, 200);
        hide();
    }

    private CancelableRunnable createHideAlertRunnable() {
        return new CancelableRunnable() {
            @Override
            public void execute() {
                hideAlertAnimate();
            }
        };
    }

    private static WindowManager.LayoutParams createLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.format = PixelFormat.OPAQUE;
        layoutParams.windowAnimations = 0;
        return layoutParams;
    }

}
