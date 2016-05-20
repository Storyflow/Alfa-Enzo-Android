package com.thirdandloom.storyflow.views.recyclerview.decoration;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AndroidUtils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.v7.widget.RecyclerView;

public class GradientOnTopStickyHeaderDecoration extends StickyHeaderDecoration {

    private Paint topViewPaint;

    public GradientOnTopStickyHeaderDecoration(StickyHeaderAdapter adapter, boolean renderInline) {
        super(adapter, renderInline);
        initPaint();
    }

    public GradientOnTopStickyHeaderDecoration(StickyHeaderAdapter adapter) {
        super(adapter);
        initPaint();
    }

    private void initPaint() {
        topViewPaint = new Paint();
        topViewPaint.setStyle(Paint.Style.FILL);
        topViewPaint.setShader(new LinearGradient(0, 0, 0, StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.headerHeight), Color.BLACK, Color.TRANSPARENT, Shader.TileMode.MIRROR));
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        c.drawRect(parent.getLeft(), parent.getTop(), parent.getRight(), StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.headerHeight), topViewPaint);
        super.onDrawOver(c, parent, state);
    }

}
