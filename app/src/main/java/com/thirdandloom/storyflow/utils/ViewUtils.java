package com.thirdandloom.storyflow.utils;

import rx.functions.Action1;
import rx.functions.Action2;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

public class ViewUtils {

    public static void removeFromParent(View view) {
        ViewGroup parent = (ViewGroup)view.getParent();
        parent.removeView(view);
    }

    public static void setHidden(View view, boolean hidden) {
        view.setVisibility(hidden ? View.GONE : View.VISIBLE);
    }

    public static void hide(View view) {
        setHidden(view, true);
    }

    public static void show(View view) {
        setHidden(view, false);
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

    public static void callOnPreDraw(View view, Action1<View> action) {
        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                action.call(view);
                return false;
            }
        });
    }

}
