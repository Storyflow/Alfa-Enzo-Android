package com.thirdandloom.storyflow.views.toolbar;

import com.thirdandloom.storyflow.R;
import rx.functions.Action0;

import android.content.Context;
import android.util.AttributeSet;

public class ConfigureAvatarToolBar extends BaseToolBar {
    public ConfigureAvatarToolBar(Context context) {
        super(context);
    }

    public ConfigureAvatarToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ConfigureAvatarToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    private Action0 onOkClicked;

    @Override
    protected int getInnerViewLayoutId() {
        return R.layout.view_toolbar_activity_choose_avatar;
    }

    @Override
    protected void init() {
        findViewById(R.id.view_toolbar_activity_choose_avatar_ok).setOnClickListener(v -> {
            if (onOkClicked != null) onOkClicked.call();
        });
    }

    public void setOnOkClicked(Action0 onOkClicked) {
        this.onOkClicked = onOkClicked;
    }
}
