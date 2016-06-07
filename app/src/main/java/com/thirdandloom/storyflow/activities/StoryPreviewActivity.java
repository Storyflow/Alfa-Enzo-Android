package com.thirdandloom.storyflow.activities;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.ViewUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.io.Serializable;

public class StoryPreviewActivity extends BaseActivity {

    public static Intent newInstance(Story story, View fromView) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, StoryPreviewActivity.class);
        SavedState state = new SavedState();
        state.story = story;
        ViewUtils.getLocationInWindow(fromView, (x, y) -> {
            state.fromViewX = x;
            state.fromViewY = y;
            state.fromViewWidth = fromView.getWidth();
            state.fromViewHeight = fromView.getHeight();
        });
        putExtra(intent, state);
        return intent;
    }

    private ImageView imageView;
    private View contentView;
    private PhotoViewAttacher photoViewAttacher;

    private int leftDelta;
    private int topDelta;
    private float widthScale;
    private float heightScale;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_preview);
        restoreState(SavedState.class, savedInstanceState,
                restored -> state = restored,
                inited -> state = inited);
        findViews();
        initGui(savedInstanceState == null);
    }

    private void findViews() {
        imageView = (ImageView)findViewById(R.id.activity_story_preview_image_view);
        contentView = findViewById(R.id.activity_story_preview_content);
    }

    private void initGui(boolean isFirstStart) {
        String imageUrl;
        switch (state.story.getType()) {
            case Image:
                imageUrl = state.story.getImageData().getNormalSizedImage().url();
                break;
            case Text:
                imageUrl = state.story.getAuthor().getCroppedImageCover().getImageUrl();
                break;
            default:
                imageUrl = null;
        }
        if (imageUrl == null) return;
        contentView.setAlpha(isFirstStart ? 0.f : 1.f);
        Glide
                .with(this)
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        imageView.setImageDrawable(resource);
                        if (isFirstStart) {
                            prepareAppearAnimation();
                        } else {
                            initImageViewAttacher();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {
        imageView.animate().setDuration(300)
                .scaleX(widthScale).scaleY(heightScale)
                .translationX(leftDelta).translationY(topDelta)
                .withEndAction(() -> {
                    finish();
                    overridePendingTransition(0, 0);
                })
                .setInterpolator(new DecelerateInterpolator());
        contentView.animate().setDuration(300).alpha(0.f).setInterpolator(new DecelerateInterpolator());
    }

    private void prepareAppearAnimation() {
        ViewUtils.callOnPreDraw(imageView, view -> {
            ViewUtils.getLocationInWindow(imageView, (x, y) -> {
                leftDelta = state.fromViewX - x;
                topDelta = state.fromViewY - y;
                widthScale = (float) state.fromViewWidth / imageView.getWidth();
                heightScale = (float) state.fromViewHeight / imageView.getHeight();
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
                .withEndAction(this::initImageViewAttacher)
                .setInterpolator(new DecelerateInterpolator());

        contentView.animate().setDuration(300).alphaBy(1).setInterpolator(new DecelerateInterpolator());
    }

    private void initImageViewAttacher() {
        photoViewAttacher = new PhotoViewAttacher(imageView);
        photoViewAttacher.setScaleType(ImageView.ScaleType.CENTER_CROP);
    }

    @Override
    public int getStatusBarColorResourceId() {
        return R.color.black;
    }

    @Override
    protected boolean hasToolBar() {
        return false;
    }

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    private SavedState state;
    private static class SavedState implements Serializable {
        private static final long serialVersionUID = -8795943301848300317L;

        public Story story;
        public int fromViewX;
        public int fromViewY;
        public int fromViewWidth;
        public int fromViewHeight;
    }
}
