package com.thirdandloom.storyflow.utils;

import rx.functions.Action1;
import rx.functions.Action2;

import android.graphics.PixelFormat;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ScrollView;

public class ViewUtils extends BaseUtils{

    public static void removeFromParent(View view) {
        ViewGroup parent = (ViewGroup)view.getParent();
        parent.removeView(view);
    }

    public static void setShown(View view, boolean shown) {
        view.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public static void setHidden(View view, boolean hidden) {
        view.setVisibility(hidden ? View.GONE : View.VISIBLE);
    }

    public static void hide(View view) {
        setHidden(view, true);
    }

    public static void hide(View... views) {
        for (View view : views) {
            setHidden(view, true);
        }
    }

    public static void show(View view) {
        setHidden(view, false);
    }

    public static void show(View... views) {
        for (View view : views) {
            setHidden(view, false);
        }
    }

    public static void applyWidth(View itemView, int widthPixel) {
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.width = widthPixel;
        itemView.setLayoutParams(params);
    }

    public static void applyHeight(View itemView, int heightPixel) {
        ViewGroup.LayoutParams params = itemView.getLayoutParams();
        params.height = heightPixel;
        itemView.setLayoutParams(params);
    }

    public static WindowManager.LayoutParams getFullScreenLayoutParams() {
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        return layoutParams;
    }

    public static WindowManager.LayoutParams getWrapContentWindowLayoutParams() {
        //WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.x = x;
        //layoutParams.y = y;
        //layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
        layoutParams.format = PixelFormat.OPAQUE;
        layoutParams.windowAnimations = 0;
        return layoutParams;
    }

    public static void getMeasuredSize(View view, Action2<Integer, Integer> action) {
        int spec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(spec, spec);
        action.call(view.getMeasuredWidth(), view.getMeasuredHeight());
    }

    public static void setViewFrame(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static void applyWrapContentHeight(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setLayoutParams(params);
    }

    public static void applyFrameLayoutParamsGravity(View layout, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)layout.getLayoutParams();
        params.gravity = gravity;
        layout.setLayoutParams(params);
    }

    public static void applyMatchParent(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.height = WindowManager.LayoutParams.MATCH_PARENT;
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(params);
    }

    public static void callOnPreDraw(View view, Action1<View> action) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                action.call(view);
                return true;
            }
        });
    }

    public static void getLocationInWindow(View view, Action2<Integer, Integer> action) {
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int x = location[0];
        int y = location[1];
        action.call(x, y);
    }

    public static int getScrollViewContentHeight(ScrollView view) {
        return view.getChildAt(0).getHeight();
    }

    /**
     * Copied from recycler view
     * Find the topmost view under the given point.
     *
     * @param x Horizontal position in pixels to search
     * @param y Vertical position in pixels to search
     * @return The child view under (x, y) or null if no matching child is found
     */
    @Nullable
    public static View findChildViewUnder(@Nullable ViewGroup viewGroup, float x, float y) {
        if (viewGroup == null) return null;

        final int count = viewGroup.getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = viewGroup.getChildAt(i);
            final float translationX = ViewCompat.getTranslationX(child);
            final float translationY = ViewCompat.getTranslationY(child);

            int[] location = new int[2];
            child.getLocationInWindow(location);
            final int childLeft = location[0];
            final int childRight = location[0] + child.getWidth();
            final int childTop = location[1];
            final int childBottom = location[1] + child.getHeight();

            if (x >= childLeft + translationX &&
                    x <= childRight + translationX &&
                    y >= childTop + translationY &&
                    y <= childBottom + translationY) {
                if (child instanceof ViewGroup) {
                    return findChildViewUnder((ViewGroup)child, x, y);
                } else {
                    return child;
                }
            }
        }
        return null;
    }

}
