package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.animations.AnimatorListener;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

public class LaunchActivity extends BaseActivity {

    private View circleView;
    private View textView;

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
        //StoryflowApplication.restClient().signIn("rom", "fortest2", (user) -> {
        //    Timber.d("RestClient.ResponseCallback.ResponseSuccessInterface sucess");
        //}, () -> {
        //    Timber.d("RestClient.ResponseCallback.ResponseSuccessInterface failure");
        //});
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
                if (repeatCount >= 1) {
                    animation.end();
                    circleView.setLayerType(View.LAYER_TYPE_NONE, null);
                    startActivity(SignUpActivity.newInstance());
                    overridePendingTransition(0, 0);
                    LaunchActivity.this.finish();
                }
            }
        });
    }
}
