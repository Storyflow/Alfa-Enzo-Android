package com.thirdandloom.storyflow.views.recyclerview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class SnappyRecyclerView extends RecyclerView {
    public SnappyRecyclerView(Context context) {
        this(context, null);
    }

    public SnappyRecyclerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SnappyRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        final LayoutManager lm = getLayoutManager();

        if (lm instanceof ISnappyLayoutManager) {
            int position = ((ISnappyLayoutManager) getLayoutManager())
                    .getPositionForVelocity(velocityX, velocityY);
            super.smoothScrollToPosition(position);
            return true;
        }
        return super.fling(velocityX, velocityY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // We want the parent to handle all touch events--there's a lot going on there,
        // and there is no reason to overwrite that functionality--bad things will happen.
        final boolean ret = super.onTouchEvent(e);
        final LayoutManager lm = getLayoutManager();

        if (lm instanceof ISnappyLayoutManager
                && (e.getAction() == MotionEvent.ACTION_UP ||
                e.getAction() == MotionEvent.ACTION_CANCEL)
                && getScrollState() == SCROLL_STATE_IDLE) {
            // The layout manager is a SnappyLayoutManager, which means that the
            // children should be snapped to a grid at the end of a drag or
            // fling. The motion event is either a user lifting their finger or
            // the cancellation of a motion events, so this is the time to take
            // over the scrolling to perform our own functionality.
            // Finally, the scroll state is idle--meaning that the resultant
            // velocity after the user's gesture was below the threshold, and
            // no fling was performed, so the view may be in an unaligned state
            // and will not be flung to a proper state.
            smoothScrollToPosition(((ISnappyLayoutManager) lm).getFixScrollPos());
        }

        return ret;
    }
}
