package com.thirdandloom.storyflow.views.recyclerview;

import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.Timber;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Action3;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;

public class VerticalDragNotifierRecyclerView extends RecyclerView {
    private float startScrollPosition;
    private float previousScrollPosition;
    private boolean dragInProcess;
    private VelocityTracker velocityTracker;

    private Action0 dragStarted;
    private Action1<Integer> dragFinished;
    private Action3<Float, Float, View> onDrag;
    private Action0 onClick;

    public VerticalDragNotifierRecyclerView(Context context) {
        super(context);
    }

    public VerticalDragNotifierRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VerticalDragNotifierRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setDragFinished(Action1<Integer> dragFinished) {
        this.dragFinished = dragFinished;
    }

    public void setDragStarted(Action0 dragStarted) {
        this.dragStarted = dragStarted;
    }

    public void setOnDrag(Action3<Float, Float, View> onDrag) {
        this.onDrag = onDrag;
    }

    public void setOnClick(Action0 onClick) {
        this.onClick = onClick;
    }

    private void finishDrag() {
        finishDrag(0);
    }

    private void finishDrag(int velocity) {
        dragInProcess = false;
        if (dragFinished != null) dragFinished.call(velocity);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float delta = startScrollPosition - e.getY();
                if (Math.abs(delta) > DeviceUtils.minScrollPx()) {
                    if (!dragInProcess) {
                        dragInProcess = true;
                        if (dragStarted != null) dragStarted.call();
                        if (velocityTracker != null) velocityTracker.addMovement(e);
                    }
                    float currentPosition = delta - DeviceUtils.minScrollPx();
                    if (onDrag != null) onDrag.call(currentPosition, currentPosition - previousScrollPosition, this);
                    previousScrollPosition = currentPosition;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                if (dragInProcess) {
                    finishDrag();
                }
                if (velocityTracker != null) {
                    velocityTracker.recycle();
                    velocityTracker = null;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (dragInProcess) {
                    float velocity = 0;
                    if (velocityTracker != null) {
                        // Compute velocity within the last 100ms
                        velocityTracker.addMovement(e);
                        velocityTracker.computeCurrentVelocity(100);
                        velocity = Math.abs(velocityTracker.getYVelocity());
                    }
                    finishDrag(Math.round(velocity));
                } else {
                    if (onClick != null) onClick.call();
                }
                break;
            case MotionEvent.ACTION_DOWN:
                startScrollPosition = e.getY();
                previousScrollPosition = 0;
                velocityTracker = VelocityTracker.obtain();
                if (velocityTracker != null) {
                    velocityTracker.addMovement(e);
                }
                break;
        }

        return super.onTouchEvent(e);
    }
}
