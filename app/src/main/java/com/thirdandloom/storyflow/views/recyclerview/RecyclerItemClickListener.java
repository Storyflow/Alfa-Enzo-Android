package com.thirdandloom.storyflow.views.recyclerview;

import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

public class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
    private OnItemClickListener itemClickListener;

    public interface OnItemClickListener {
        void onItemClick(RecyclerView.ViewHolder viewHolder, int position);
    }

    private GestureDetector singleTapDetector;

    public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
        itemClickListener = listener;
        singleTapDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
        ViewGroup viewGroup = (ViewGroup)view.findChildViewUnder(e.getX(), e.getY());
        View childViewUnder = ViewUtils.findChildViewUnder(viewGroup, e.getX(), e.getY());
        if (childViewUnder != null && childViewUnder.hasOnClickListeners()) return false;

        View childView = view.findChildViewUnder(e.getX(), e.getY());
        if (childView != null && itemClickListener != null && singleTapDetector.onTouchEvent(e)) {
            itemClickListener.onItemClick(view.getChildViewHolder(childView), view.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }
}
