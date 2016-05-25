package com.thirdandloom.storyflow.views.recyclerview;

import android.content.Context;
import android.graphics.PointF;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.RecyclerView;

public class OneSnapLinearLayoutManager extends SnappyLinearLayoutManager {
    public OneSnapLinearLayoutManager(Context context) {
        super(context);
    }

    public OneSnapLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    @Override
    public int getPositionForVelocity(int velocityX, int velocityY) {
        if (getChildCount() == 0) {
            return 0;
        }
                if (getOrientation() == HORIZONTAL) {
            return calcPosForVelocity(velocityX, getPosition(getChildAt(0)));
        } else {
            return calcPosForVelocity(velocityY, getPosition(getChildAt(0)));
        }
    }

    private int calcPosForVelocity(int velocity, int currPos) {
        if (velocity < 0) {
            return Math.max(currPos, 0);
        } else {
            return currPos + 1;
        }
    }

    public LinearSmoothScroller getSmoothScroller(RecyclerView recyclerView, int distanceInPixels) {
        return new LinearSmoothScroller(recyclerView.getContext()) {
            @Override
            public PointF computeScrollVectorForPosition(int targetPosition) {
                return OneSnapLinearLayoutManager.this
                        .computeScrollVectorForPosition(targetPosition);
            }
        };
    }
}
