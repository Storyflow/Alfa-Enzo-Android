package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.Timber;

import android.os.Bundle;


public class LaunchActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);

        StoryflowApplication.restClient().signIn("rom", "fortest2", (user) -> {
            Timber.d("RestClient.ResponseCallback.ResponseSuccessInterface sucess");
        }, () -> {
            Timber.d("RestClient.ResponseCallback.ResponseSuccessInterface failure");
        });

    }
}
