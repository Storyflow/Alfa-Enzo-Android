package com.thirdandloom.storyflow.views.toolbar;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.util.AttributeSet;

public class StoryflowToolBar extends BaseToolBar {
    
    public StoryflowToolBar(Context context) {
        super(context);
    }

    public StoryflowToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoryflowToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getInnerViewLayoutId() {
        return R.layout.view_toolbar_storyflow;
    }

    @Override
    protected void init() {

    }
}
