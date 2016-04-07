package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.animations.AnimatorListener;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class LaunchActivity extends BaseActivity {
    private static boolean RUN_SIGN_UP = false;
    private static int FLIP_REPEAT_COUNT_MIN = 4;

    private View circleView;
    private View textView;
    private Intent launchedIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        findViews();

        if (RUN_SIGN_UP) {
            startActivity(WelcomeActivity.newInstance());
            return;
        }

        ViewUtils.getMeasuredSize(textView, (width, height) -> {
            ViewUtils.setViewFrame(circleView, height, height);
            applyCameraDistance();
            startRotateAnimation();
        });

        signInWithSavedAccount();
    }

    private void signInWithSavedAccount() {
        String password = StoryflowApplication.account().getPassword();
        String email = StoryflowApplication.account().getUser().getEmail();
        if (!TextUtils.isEmpty(password)) {
            signIn(email, password);
        } else {
            launchedIntent = WelcomeActivity.newInstance();
        }
    }

    private void signIn(String email, String password) {
        StoryflowApplication.restClient().signIn(email, password, user -> {
            StoryflowApplication.account().updateProfile(user);
            launchedIntent = BrowseStoriesActivity.newInstance(true);
        }, errorMessage -> {
            StoryflowApplication.account().resetAccount();
            launchedIntent = WelcomeActivity.newInstance();
        });
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getStatusBarColor() {
        return R.color.yellow;
    }

    private void findViews() {
        View launchView = findViewById(R.id.launch_layout);
        circleView = launchView.findViewById(R.id.launch_circle_view);
        textView = launchView.findViewById(R.id.launch_text_view);
    }

    private void applyCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        circleView.setCameraDistance(scale);
    }

    static int repeatCount = 0;
    private void startRotateAnimation() {
        circleView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ObjectAnimator flip = ObjectAnimator.ofFloat(circleView, "rotationY", 0f, 180f);
        flip.setDuration(1000);
        flip.setInterpolator(new AccelerateInterpolator());
        flip.setRepeatCount(ValueAnimator.INFINITE);
        flip.setRepeatMode(ValueAnimator.INFINITE);
        flip.setTarget(circleView);
        flip.start();

        flip.addListener(new AnimatorListener() {
            @Override
            public void onAnimationRepeat(Animator animation) {
                repeatCount++;
                if (repeatCount >= FLIP_REPEAT_COUNT_MIN && (launchedIntent != null)) {
                    animation.end();
                    circleView.setLayerType(View.LAYER_TYPE_NONE, null);
                    startActivity(launchedIntent);
                    overridePendingTransition(0, 0);
                    LaunchActivity.this.finish();
                }
            }
        });
    }
}
