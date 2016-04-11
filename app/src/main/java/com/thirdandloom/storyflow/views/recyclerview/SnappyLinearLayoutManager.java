package com.thirdandloom.storyflow.views.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.SensorManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewConfiguration;

public class SnappyLinearLayoutManager extends LinearLayoutManager implements ISnappyLayoutManager {
    // These variables are from android.widget.Scroller, which is used, via ScrollerCompat, by
    // Recycler View. The scrolling distance calculation logic originates from the same place. Want
    // to use their variables so as to approximate the look of normal Android scrolling.
    // Find the Scroller fling implementation in android.widget.Scroller.fling().
    private static final float INFLEXION = 0.35f; // Tension lines cross at (INFLEXION, 1)
    private static float DECELERATION_RATE = (float) (Math.log(0.78) / Math.log(0.9));
    private static double FRICTION = 0.84;

    private double deceleration;
    private Context context;

    public SnappyLinearLayoutManager(Context context) {
        super(context);
        calculateDeceleration(context);
        this.context = context;
    }

    public SnappyLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        calculateDeceleration(context);
    }

    private void calculateDeceleration(Context context) {
        deceleration = SensorManager.GRAVITY_EARTH // g (m/s^2)
                * 39.3700787 // inches per meter
                // pixels per inch. 160 is the "default" dpi, i.e. one dip is one pixel on a 160 dpi
                // screen
                * context.getResources().getDisplayMetrics().density * 160.0f * FRICTION;
    }


    private int velocityX;
    @Override
    public int getPositionForVelocity(int velocityX, int velocityY) {
        this.velocityX = Math.abs(velocityX);
        if (getChildCount() == 0) {
            return 0;
        }
        if (getOrientation() == HORIZONTAL) {
            return calcPosForVelocity(velocityX, getChildAt(0).getLeft(), getChildAt(0).getWidth(),
                    getPosition(getChildAt(0)));
        } else {
            return calcPosForVelocity(velocityY, getChildAt(0).getTop(), getChildAt(0).getHeight(),
                    getPosition(getChildAt(0)));
        }
    }

    @Override
    public int getScrollPixels(int position) {
        return getChildAt(0).getWidth() * position;
    }

    private int calcPosForVelocity(int velocity, int scrollPos, int childSize, int currPos) {
        double dist = getSplineFlingDistance(velocity);

        double tempScroll = scrollPos + (velocity > 0 ? dist : -dist);
        if (velocity > 0) {
            tempScroll = Math.abs(tempScroll);
        }

        double position = Math.max(currPos + (tempScroll / childSize) + 1, 0);
        position = velocity > 0 ? position + 0.2 : position - 0.2;
        position = position < 0 ? 0 : position;

        return (int) Math.round(position);
    }

    @Override
    public void scrollToPosition(int position) {
        super.scrollToPosition(position);
    }

    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        super.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        View firstVisibleChild = recyclerView.getChildAt(0);
        int itemWidth = firstVisibleChild.getWidth();
        int currentPosition = recyclerView.getChildPosition(firstVisibleChild);
        int distanceInPixels = Math.abs((currentPosition - position) * itemWidth);
        if (distanceInPixels == 0) {
            distanceInPixels = (int) Math.abs(firstVisibleChild.getX());
        }

        SmoothScroller smoothScroller = new SmoothScroller(recyclerView.getContext(), distanceInPixels, 1);
        smoothScroller.setTargetPosition(position);
        startSmoothScroll(smoothScroller);
    }

    private double getSplineFlingDistance(double velocity) {
        final double l = getSplineDeceleration(velocity);
        final double decelMinusOne = DECELERATION_RATE - 1.0;
        return ViewConfiguration.getScrollFriction() * deceleration
                * Math.exp(DECELERATION_RATE / decelMinusOne * l);
    }

    private double getSplineDeceleration(double velocity) {
        return Math.log(INFLEXION * Math.abs(velocity)
                / (ViewConfiguration.getScrollFriction() * deceleration));
    }

    /**
     * This implementation obviously doesn't take into account the direction of the
     * that preceded it, but there is no easy way to get that information without more
     * hacking than I was willing to put into it.
     */
    @Override
    public int getFixScrollPos() {
        if (this.getChildCount() == 0) {
            return 0;
        }

        final View child = getChildAt(0);
        final int childPos = getPosition(child);

        if (getOrientation() == HORIZONTAL
                && Math.abs(child.getLeft()) > child.getMeasuredWidth() / 2) {
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        } else if (getOrientation() == VERTICAL
                && Math.abs(child.getTop()) > child.getMeasuredWidth() / 2) {
            // Scrolled first view more than halfway offscreen
            return childPos + 1;
        }
        return childPos;
    }

    private class SmoothScroller extends LinearSmoothScroller {
        private static final int TARGET_SEEK_SCROLL_DISTANCE_PX = 10000;
        private final float distanceInPixels;
        private final float duration;

        public SmoothScroller(Context context, int distanceInPixels, int duration) {
            super(context);
            this.distanceInPixels = distanceInPixels;
            float millisecondsPerPx = calculateSpeedPerPixel(context.getResources().getDisplayMetrics());
            this.duration = distanceInPixels < TARGET_SEEK_SCROLL_DISTANCE_PX ?
                    (int) (Math.abs(distanceInPixels) * millisecondsPerPx) : duration;
        }

        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return SnappyLinearLayoutManager.this
                    .computeScrollVectorForPosition(targetPosition);
        }

        @Override
        protected int calculateTimeForScrolling(int dx) {
            float proportion = (float) dx / distanceInPixels;
            int scrollTime = (int) (duration * proportion);

            scrollTime = Math.max(scrollTime, 250);
            return scrollTime;
        }

        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            if (velocityX > TARGET_SEEK_SCROLL_DISTANCE_PX - 1 ) {
                velocityX = TARGET_SEEK_SCROLL_DISTANCE_PX - 1;
            }

            float speed = (100.f - velocityX/100.f)/displayMetrics.densityDpi;
            speed = (float) Math.max(speed, 0.025);
            return speed;
        }

        @Override
        protected int getHorizontalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        protected int getVerticalSnapPreference() {
            return SNAP_TO_START;
        }

        @Override
        public int calculateDtToFit(int viewStart, int viewEnd, int boxStart, int boxEnd, int snapPreference) {
            int dtToFit;
            switch (snapPreference) {
                case SNAP_TO_START:
                    dtToFit = boxStart - viewStart;
                    break;
                default:
                    throw new IllegalArgumentException("snap preference should be one: SNAP_TO_START");
            }

            int viewWidth = viewEnd - viewStart;
            dtToFit = (boxEnd - viewWidth)/2 + dtToFit ;

            return dtToFit;
        }
    }
}
