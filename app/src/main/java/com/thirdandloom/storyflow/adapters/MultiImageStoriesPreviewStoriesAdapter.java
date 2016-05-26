package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.LinkedList;
import java.util.List;

public class MultiImageStoriesPreviewStoriesAdapter extends RecyclerView.Adapter<MultiImageStoriesPreviewStoriesAdapter.PreviewStoryHolder> {

    private List<Story> stories = new LinkedList<>();
    private Context context;
    private boolean needAppearAnimation;

    private int thumbnailLeft;
    private int thumbnailTop;
    private int thumbnailWidth;
    private int thumbnailHeight;

    public MultiImageStoriesPreviewStoriesAdapter(Context context, Story initialStory) {
        this.context = context;
        this.stories.add(initialStory);
    }

    public void setThumbnailData(int left, int top, int width, int height) {
        needAppearAnimation = true;
        thumbnailLeft = left;
        thumbnailTop = top;
        thumbnailHeight = height;
        thumbnailWidth = width;
    }

    public void addStories(List<Story> addedStories, int start) {
        stories.addAll(start, addedStories);
        notifyItemRangeInserted(start, addedStories.size());
    }

    @Override
    public PreviewStoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_preview_story, parent, false);
        PreviewStoryHolder previewStoryHolder = new PreviewStoryHolder(v);
        if (needAppearAnimation) {
            previewStoryHolder.setThumbnailData(thumbnailLeft, thumbnailTop, thumbnailWidth, thumbnailHeight);
        }
        needAppearAnimation = false;
        return previewStoryHolder;
    }

    @Override
    public void onBindViewHolder(PreviewStoryHolder holder, int position) {
        holder.setData(context, stories.get(position));
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class PreviewStoryHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private View backgroundView;

        private boolean needAppearAnimation;
        private int leftDelta;
        private int topDelta;
        private float widthScale;
        private float heightScale;

        private int thumbnailLeft;
        private int thumbnailTop;
        private int thumbnailWidth;
        private int thumbnailHeight;

        public PreviewStoryHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView)itemView.findViewById(R.id.adapter_recycler_item_preview_story_image_view);
            this.backgroundView = itemView.findViewById(R.id.adapter_recycler_item_preview_story_background);
            backgroundView.setAlpha(1);
        }

        public void setThumbnailData(int left, int top, int width, int height) {
            needAppearAnimation = true;
            backgroundView.setAlpha(0);
            thumbnailLeft = left;
            thumbnailTop = top;
            thumbnailHeight = height;
            thumbnailWidth = width;
        }

        public void setData(Context context, Story story) {
            switch (story.getType()) {
                case Image:
                    String imageUrl = story.getImageData().getNormalSizedImage().url();
                    Glide
                            .with(context)
                            .load(imageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    imageView.setImageDrawable(resource);
                                    if (needAppearAnimation) prepareAppearAnimation();
                                }
                            });
                    break;
            }
        }

        private void prepareAppearAnimation() {
            needAppearAnimation = false;
            ViewUtils.callOnPreDraw(imageView, view -> {
                ViewUtils.getLocationInWindow(view, (x, y) -> {
                    leftDelta = thumbnailLeft - x;
                    topDelta = thumbnailTop - y;
                    widthScale = (float) thumbnailWidth / view.getWidth();
                    heightScale = (float) thumbnailHeight / view.getHeight();
                    startAppearAnimation();
                });
            });
        }

        private void startAppearAnimation() {
            imageView.setPivotX(0);
            imageView.setPivotY(0);
            imageView.setScaleX(widthScale);
            imageView.setScaleY(heightScale);
            imageView.setTranslationX(leftDelta);
            imageView.setTranslationY(topDelta);
            imageView.animate().setDuration(300)
                    .scaleX(1).scaleY(1)
                    .translationX(0).translationY(0)
                    .setInterpolator(new DecelerateInterpolator());


            imageView.setAlpha(0.f);
            imageView.animate().setDuration(300).alphaBy(1).setInterpolator(new AccelerateInterpolator());
            backgroundView.animate().setDuration(300).alphaBy(1).setInterpolator(new AccelerateInterpolator());
        }
    }
}
