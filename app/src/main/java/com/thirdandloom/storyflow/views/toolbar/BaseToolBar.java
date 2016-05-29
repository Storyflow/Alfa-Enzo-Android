package com.thirdandloom.storyflow.views.toolbar;

import com.thirdandloom.storyflow.R;
import rx.functions.Action0;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;
import android.view.View;
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
        inflate(context, getInnerViewLayoutId(), this);
        titleTextView = (TextView)findViewById(R.id.view_toolbar_title_text_view);
        upButton = findViewById(R.id.view_toolbar_up);
        init();
        setContentInsetsAbsolute(0, 0);
    }

    private TextView titleTextView;
    private View upButton;

    public void setTitleText(@StringRes int titleId) {
        if (titleTextView == null) {
            throw new UnsupportedOperationException("You should add TextView with id=view_toolbar_title_text_view into your custom toolbar");
        }
        titleTextView.setText(titleId);
    }

    @LayoutRes
    protected abstract int getInnerViewLayoutId();

    protected abstract void init();

    public void onUpButtonClicked(Action0 click) {
        if (upButton == null) {
            throw new UnsupportedOperationException("If u want to use up button "
                    + "u should add view with id:R.id.view_toolbar_up into your layout");
        }
        upButton.setOnClickListener(v -> click.call());
    }
}
