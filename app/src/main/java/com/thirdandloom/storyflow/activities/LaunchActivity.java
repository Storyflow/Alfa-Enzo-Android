package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.rest.ErrorHandler;
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
    private static int FLIP_REPEAT_COUNT_MIN = 2;

    private View circleView;
    private View textView;
    private View retryView;
    private Intent launchedIntent;
    private int repeatCount = 0;
    private boolean terminateAnimation;
    private boolean signInInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        findViews();

        ViewUtils.getMeasuredSize(textView, (width, height) -> {
            ViewUtils.setViewFrame(circleView, height, height);
            applyCameraDistance();
            startRotateAnimation();
        });

        if (savedInstanceState == null) signInWithSavedAccount();
        ViewUtils.setHidden(retryView, savedInstanceState == null);
        retryView.setOnClickListener(v -> {
            if (!signInInProgress) {
                startRotateAnimation();
                signInWithSavedAccount();
            }
        });
    }

    private void signInWithSavedAccount() {
        signInInProgress = true;
        StoryflowApplication.account().getUser(user -> {
            String password = StoryflowApplication.account().getPassword();
            if (!TextUtils.isEmpty(password)) {
                signIn(user.getEmail(), password);
            } else {
                launchedIntent = WelcomeActivity.newInstance();
                terminateAnimation = true;
            }
        });
    }

    private void signIn(String email, String password) {
        StoryflowApplication.restClient().signIn(email, password, user -> {
            StoryflowApplication.account().updateProfile(user);
            launchedIntent = BrowseStoriesActivity.newInstance(true);
            signInInProgress = false;
        }, (errorMessage, type) -> {
            signInInProgress = false;
            if (type == ErrorHandler.Type.Connection) {
                showError(errorMessage);
                terminateAnimation = true;
            } else {
                StoryflowApplication.account().resetAccount();
                launchedIntent = WelcomeActivity.newInstance();
            }
        });
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Override
    protected int getStatusBarColorResourceId() {
        return R.color.yellow;
    }

    private void findViews() {
        View launchView = findViewById(R.id.launch_layout);
        circleView = launchView.findViewById(R.id.launch_circle_view);
        textView = launchView.findViewById(R.id.launch_text_view);
        retryView = findViewById(R.id.launch_retry_view);
    }

    private void applyCameraDistance() {
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        circleView.setCameraDistance(scale);
    }

    private void startRotateAnimation() {
        repeatCount = 0;
        if (circleView.getAnimation() != null) circleView.getAnimation().cancel();

        circleView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        ObjectAnimator flip = ObjectAnimator.ofFloat(circleView, "rotationY", 0f, 180f);
        flip.setDuration(1000);
        flip.setInterpolator(new AccelerateInterpolator());
        flip.setRepeatCount(ValueAnimator.INFINITE);
        flip.setRepeatMode(ValueAnimator.INFINITE);
        flip.setTarget(circleView);
        flip.addListener(flipAnimatorListener);
        flip.start();
    }

    private final AnimatorListener flipAnimatorListener = new AnimatorListener() {
        @Override
        public void onAnimationRepeat(Animator animation) {
            repeatCount++;
            if (terminateAnimation || repeatCount >= FLIP_REPEAT_COUNT_MIN) {
                animation.end();
                circleView.setLayerType(View.LAYER_TYPE_NONE, null);
                if (launchedIntent != null) {
                    startActivity(launchedIntent);
                    overridePendingTransition(0, 0);
                    LaunchActivity.this.finish();
                } else {
                    terminateAnimation = false;
                    ViewUtils.show(retryView);
                }
            }
        }
    };
}
