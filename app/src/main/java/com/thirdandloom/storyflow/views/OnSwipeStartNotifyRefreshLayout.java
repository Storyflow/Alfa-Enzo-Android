package com.thirdandloom.storyflow.views;

import rx.functions.Action0;
import rx.functions.Action1;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class OnSwipeStartNotifyRefreshLayout extends SwipeRefreshLayout {
    private Action1<Integer> notifier;

    public OnSwipeStartNotifyRefreshLayout(Context context, AttributeSet attrs) {
        super( context, attrs );
    }

    public void setNotifier(Action1<Integer> notifier) {
        this.notifier = notifier;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (notifier != null) notifier.call(ev.getAction());
        return super.onTouchEvent(ev);
    }
}
