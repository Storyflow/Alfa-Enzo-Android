package com.thirdandloom.storyflow.views;

import com.thirdandloom.storyflow.utils.AndroidUtils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ScrollView;

public class LockedNotifyScrollView extends ScrollView {

    public interface Actions {
        void onDragStarted();
        void onDragFinished(int velocity);
        void onClicked(View view);
        void onDrag(float scrollAbsolute, float scrollDelta, View view);
    }

    public LockedNotifyScrollView(Context context) {
        super(context);
    }

    public LockedNotifyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LockedNotifyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private float startScrollPosition;
    private float previousScrollPosition;
    private boolean dragInProcess;
    private VelocityTracker velocityTracker;

    private Actions actions;

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    private void finishDrag() {
        finishDrag(0);
    }

    private void finishDrag(int velocity) {
        dragInProcess = false;
        actions.onDragFinished(velocity);
    }

    @Override
        public boolean onTouchEvent(MotionEvent e) {
            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float delta = startScrollPosition - e.getY();
                    if (Math.abs(delta) > AndroidUtils.minScrollPx()) {
                        if (!dragInProcess) {
                            dragInProcess = true;
                            actions.onDragStarted();
                            if (velocityTracker != null) velocityTracker.addMovement(e);
                        }
                        float currentScrollPosition = delta - AndroidUtils.minScrollPx();
                        actions.onDrag(currentScrollPosition, currentScrollPosition - previousScrollPosition, this);
                        previousScrollPosition = currentScrollPosition;
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
                        actions.onClicked(this);
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

    /**
     *
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (t > oldt) {
            scrollTo(0, 0);
        } else {
            super.onScrollChanged(l, t, oldl, oldt);
        }
    }
}
