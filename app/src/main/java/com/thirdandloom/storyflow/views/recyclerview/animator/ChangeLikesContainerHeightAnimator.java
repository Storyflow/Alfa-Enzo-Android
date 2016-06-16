package com.thirdandloom.storyflow.views.recyclerview.animator;

import com.thirdandloom.storyflow.adapters.holder.ReadStoriesBaseViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPopulatedViewHolder;
import com.thirdandloom.storyflow.utils.animations.AnimatorListener;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.DecelerateInterpolator;

import java.util.List;

public class ChangeLikesContainerHeightAnimator extends DefaultItemAnimator  {

    private DecelerateInterpolator decelerateInterpolator = new DecelerateInterpolator();
    private ArrayMap<RecyclerView.ViewHolder, AnimatorInfo> animatorMap = new ArrayMap<>();

    @Override
    public boolean canReuseUpdatedViewHolder(@NonNull RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state, @NonNull RecyclerView.ViewHolder viewHolder, int changeFlags, @NonNull List<Object> payloads) {
        HeightItemInfo info = (HeightItemInfo) super.recordPreLayoutInformation(state, viewHolder,
                changeFlags, payloads);
        final ReadStoriesBaseViewHolder readStoryHolder = (ReadStoriesBaseViewHolder) viewHolder;
        info.height = readStoryHolder.getPreLayoutContainerHeight();

        return info;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPostLayoutInformation(@NonNull RecyclerView.State state,
            @NonNull RecyclerView.ViewHolder viewHolder) {
        HeightItemInfo info = (HeightItemInfo) super.recordPostLayoutInformation(state, viewHolder);
        final ReadStoriesBaseViewHolder readStoryHolder = (ReadStoriesBaseViewHolder) viewHolder;
        info.height = readStoryHolder.getPostLayoutContainerHeight();

        return info;
    }

    @Override
    public ItemHolderInfo obtainHolderInfo() {
        return new HeightItemInfo();
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder, @NonNull RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preInfo, @NonNull ItemHolderInfo postInfo) {
        if (oldHolder != newHolder || !(newHolder instanceof ReadStoriesBaseViewHolder)) {
            return super.animateChange(oldHolder, newHolder, preInfo, postInfo);
        }

        final ReadStoriesPopulatedViewHolder viewHolder = (ReadStoriesPopulatedViewHolder) newHolder;

        HeightItemInfo oldInfo = (HeightItemInfo) preInfo;
        HeightItemInfo newInfo = (HeightItemInfo) postInfo;
        int oldHeight = oldInfo.height;
        int newHeight = newInfo.height;

        AnimatorInfo runningInfo = animatorMap.get(newHolder);
        if (runningInfo != null) {
            if (runningInfo.animator != null && runningInfo.animator.isRunning()) {
                runningInfo.animator.cancel();
            }
        }

        ValueAnimator itemViewHeightAnimator = ValueAnimator.ofInt(oldHeight, newHeight);
        itemViewHeightAnimator.setDuration(300);
        itemViewHeightAnimator.setInterpolator(decelerateInterpolator);
        itemViewHeightAnimator.addUpdateListener(animation -> {
            viewHolder.starsContainer.getLayoutParams().height = (Integer) animation.getAnimatedValue();
            viewHolder.starsContainer.requestLayout();
        });
        itemViewHeightAnimator.addListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                viewHolder.animationFinished();
                dispatchAnimationFinished(viewHolder);
                animatorMap.remove(viewHolder);
            }
        });
        itemViewHeightAnimator.setTarget(newHolder.itemView);
        viewHolder.animationStarted(oldHeight);
        itemViewHeightAnimator.start();

        AnimatorInfo runningAnimInfo = new AnimatorInfo(itemViewHeightAnimator);
        animatorMap.put(newHolder, runningAnimInfo);

        return true;
    }

    private class HeightItemInfo extends ItemHolderInfo {
        private int height;
    }

    private class AnimatorInfo {
        ValueAnimator animator;

        public AnimatorInfo(ValueAnimator animator) {
            this.animator = animator;
        }
    }
}
