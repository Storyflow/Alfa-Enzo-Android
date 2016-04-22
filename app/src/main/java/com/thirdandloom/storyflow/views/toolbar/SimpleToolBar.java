package com.thirdandloom.storyflow.views.toolbar;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.util.AttributeSet;

public class SimpleToolBar extends BaseToolBar {
    
    public SimpleToolBar(Context context) {
        super(context);
    }

    public SimpleToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SimpleToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getInnerViewLayoutId() {
        return R.layout.view_toolbar_simple;
    }

    @Override
    protected void init() {

    }
}
