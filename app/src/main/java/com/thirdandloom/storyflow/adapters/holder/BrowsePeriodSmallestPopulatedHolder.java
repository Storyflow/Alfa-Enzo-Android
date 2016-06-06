package com.thirdandloom.storyflow.adapters.holder;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.ViewUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class BrowsePeriodSmallestPopulatedHolder extends BrowsePeriodEmptyHolder {

    public static BrowsePeriodSmallestPopulatedHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_filled_content, parent, false);
        return new BrowsePeriodSmallestPopulatedHolder(itemView, actions);
    }

    public BrowsePeriodSmallestPopulatedHolder(View itemView, Actions actions) {
        super(itemView, actions);
    }

    protected List<ImageView> imageViews;
    private int addedImageViewsHeight;

    @Override
    protected void findViews() {
        dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_top_text_view);
        dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_text_view);
        dataContainer = itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_container);

        imageViews = Arrays.asList((ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view0),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view1),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view2),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view3),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view4),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view5),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view6),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view7),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view8),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view9)
        );
    }

    public void setStories(List<Story> stories, Context context, int itemWidthPixels, BrowsePeriodsAdapter.ItemType itemType, int parentHeight) {
        this.addedImageViewsHeight = 0;

        ArrayUtils.forEach(imageViews, (imageView, pos) -> {
            boolean settingsStoriesCompleted = addedImageViewsHeight >= (parentHeight - dateTopTextView.getMeasuredHeight() - dateBottomTextView.getMeasuredHeight());
            resetData(pos);

            if (pos < stories.size() && !settingsStoriesCompleted) {
                configureData(context, stories.get(pos), pos, itemWidthPixels, itemType);
            }
        });
    }

    protected void configureData(Context context, Story story, Integer pos, int itemWidthPixels, BrowsePeriodsAdapter.ItemType itemType) {
        int height = configureImageView(imageViews.get(pos), story, context, itemWidthPixels, itemType);
        addedImageViewsHeight += height;
    }

    protected void resetData(int position) {
        imageViews.get(position).setImageDrawable(null);
    }

    /**
     *
     * @param imageView
     * @param story
     * @param context
     * @param itemWidthPixels
     * @param itemType
     * @return added height
     */
    public int configureImageView(ImageView imageView, Story story, Context context, int itemWidthPixels, BrowsePeriodsAdapter.ItemType itemType) {
        //storyLocalUid = story.getLocalUid();

        String imageUrl;
        int height;
        int imageHeight;
        int imageWidth;
        switch (story.getType()) {
            case Text:
                imageUrl = story.getAuthor().getCroppedImageCover().getImageUrl();
                if (story.getAuthor().getCroppedImageCover().getRect() != null
                        && story.getAuthor().getCroppedImageCover().getRect().height() != 0
                        && story.getAuthor().getCroppedImageCover().getRect().width() != 0) {
                    imageHeight = story.getAuthor().getCroppedImageCover().getRect().height();
                    imageWidth = story.getAuthor().getCroppedImageCover().getRect().width();
                } else {
                    imageHeight = AndroidUtils.dp(100);
                    imageWidth = AndroidUtils.dp(100);
                }
                break;
            case Image:
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
                    imageWidth = AndroidUtils.dp(100);
                }

                break;
            default:
                throw new UnsupportedOperationException("Unsupported story type.");
        }

        float coef = MathUtils.calculateMaxScaleRatio(imageWidth, imageHeight, itemWidthPixels);
        height = Math.round(coef * imageHeight);

        configureImage(imageView, context, imageUrl, height, itemWidthPixels);
        return height;
        //configurePendingActions(story.getPendingStatus());
    }

    private void configureImage(ImageView imageView, Context context, String url, int height, int width) {
        //TODO
        //scaleType be removed after story.getAuthor().getCroppedImageCover().getRect()
        //fixed: -180x106x735x391
        //imageView.setScaleType(scaleType);
        ViewUtils.applyHeight(imageView, height);
        Glide
                .with(context)
                .load(url)
                .override(width, height)
                .diskCacheStrategy(DiskCacheStrategy.RESULT)
                .into(imageView);
    }
}
