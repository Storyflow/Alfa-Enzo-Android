package com.thirdandloom.storyflow.views.toolbar;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.widget.TextView;

public abstract class BaseToolBar extends Toolbar {

    public BaseToolBar(Context context) {
        this(context, null);
    }

    public BaseToolBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public BaseToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        inflate(context, getInnetViewLayoutId(), this);
        titleTextView = (TextView)findViewById(R.id.view_toolbar_title_text_view);
        init();
    }

    private TextView titleTextView;

    public void setTitleText(@StringRes int titleId) {
        if (titleTextView == null) {
            throw new UnsupportedOperationException("You should add TextView with id=view_toolbar_title_text_view into your custom toolbar");
        }
        titleTextView.setText(titleId);
    }

    @LayoutRes
    protected abstract int getInnetViewLayoutId();

    protected abstract void init();
}
