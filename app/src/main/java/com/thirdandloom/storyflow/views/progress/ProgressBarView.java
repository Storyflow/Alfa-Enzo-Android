package com.thirdandloom.storyflow.views.progress;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.IgnoreTouchListener;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

public class ProgressBarView extends FrameLayout {

    private ProgressBar progressBar;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_full_screen_progress_bar, this);
        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
    }

    public void setProgressGravity(int gravity) {
        LayoutParams params = (LayoutParams) progressBar.getLayoutParams();
        params.gravity = gravity;
        setOnTouchListener(new IgnoreTouchListener());
    }
}
