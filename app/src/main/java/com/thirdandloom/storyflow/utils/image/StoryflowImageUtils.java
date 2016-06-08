package com.thirdandloom.storyflow.utils.image;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.widget.ImageView;

public class StoryflowImageUtils {

    public static class Config {
        private Context context;
        private ImageView imageView;
        private Story story;
        private BrowsePeriodsAdapter.ItemType itemType = BrowsePeriodsAdapter.ItemType.Large;
        private int itemWidthPx;

        public static Config with(Context context, ImageView imageView) {
            Config config = new Config();
            config.context = context;
            config.imageView = imageView;
            return config;
        }

        public Config story(Story story) {
            this.story = story;
            return this;
        }

        public Config itemType(BrowsePeriodsAdapter.ItemType itemType) {
            this.itemType = itemType;
            return this;
        }

        public Config itemWidthPx(int itemWidthPx) {
            this.itemWidthPx = itemWidthPx;
            return this;
        }

        public int showText() {
            int imageHeight;
            int imageWidth;
            String imageUrl = story.getAuthor().getCroppedImageCover().getImageUrl();
            imageHeight = AndroidUtils.dp(150);
            imageWidth = itemWidthPx;
            int height = calculateHeight(imageWidth, imageHeight, itemWidthPx);
            ViewUtils.applyHeight(imageView, height);
            Glide
                    .with(context)
                    .load(imageUrl)
                    .override(itemWidthPx, height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            return height;
        }

        public int showImage() {
            String imageUrl;
            int imageHeight;
            int imageWidth;
            switch (itemType) {
                case Small:
                case Smallest:
                default:
                    imageUrl = story.getImageData().getCollapsedSizedImage().url();
                    imageHeight = story.getImageData().getCollapsedSizedImage().size().height();
                    imageWidth = story.getImageData().getCollapsedSizedImage().size().width();
                    break;
                case Large:
                    imageUrl = story.getImageData().getNormalSizedImage().url();
                    imageHeight = story.getImageData().getNormalSizedImage().size().height();
                    imageWidth = story.getImageData().getNormalSizedImage().size().width();
                    break;
            }

            //TODO
            //this code should be removed after story.getImageData().getNormalSizedImage().size()
            //fixed: story.getImageData().getNormalSizedImage().size() = (0, 0)
            if (imageHeight == 0 || imageWidth == 0) {
                imageHeight = AndroidUtils.dp(100);
                imageWidth = itemWidthPx;
            }

            int height = calculateHeight(imageWidth, imageHeight, itemWidthPx);
            ViewUtils.applyHeight(imageView, height);
            Glide
                    .with(context)
                    .load(imageUrl)
                    .override(itemWidthPx, height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);
            return height;
        }
    }

    private static int calculateHeight(int realWidth, int realHeight, int boxWidth) {
        float coef = MathUtils.calculateMaxScaleRatio(realWidth, realHeight, boxWidth);
        return Math.round(coef * realHeight);
    }
}
