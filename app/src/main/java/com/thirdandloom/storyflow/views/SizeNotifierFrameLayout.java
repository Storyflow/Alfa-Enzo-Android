package com.thirdandloom.storyflow.views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.thirdandloom.storyflow.utils.DeviceUtils;

public class SizeNotifierFrameLayout extends FrameLayout {

    public interface Actions {
        void onSizeChanged(int keyboardHeight);
    }

    private Rect rect = new Rect();
    private int keyboardHeight;
    private Actions actions;

    public SizeNotifierFrameLayout(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    public SizeNotifierFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public SizeNotifierFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        notifyHeightChanged();
    }

    public void notifyHeightChanged() {
        if (actions != null) {
            keyboardHeight = getKeyboardHeight();
            post(() -> {
                if (actions != null) {
                    actions.onSizeChanged(keyboardHeight);
                }
            });
        }
    }

    public int getKeyboardHeight() {
        View rootView = getRootView();
        getWindowVisibleDisplayFrame(rect);
        int usableViewHeight = rootView.getHeight() - (rect.top != 0 ? DeviceUtils.getStatusBarHeight() : 0) - DeviceUtils.getViewInset(rootView);
        return usableViewHeight - (rect.bottom - rect.top);
    }
}
