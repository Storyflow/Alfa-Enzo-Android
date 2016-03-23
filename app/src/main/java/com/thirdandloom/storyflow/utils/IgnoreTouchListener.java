package com.thirdandloom.storyflow.utils;

import android.view.MotionEvent;
import android.view.View;

public class IgnoreTouchListener implements View.OnTouchListener {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
