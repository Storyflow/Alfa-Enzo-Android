package com.thirdandloom.storyflow.adapters.holder;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.image.StoryflowImageUtils;
import com.thirdandloom.storyflow.views.LockedNotifyScrollView;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;

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
        swipeRefreshLayout = (OnSwipeStartNotifyRefreshLayout) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_swipe_container);
        notifyScrollView = (LockedNotifyScrollView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_scroll_view);

        imageViews = Arrays.asList((ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view0),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view1),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view2),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view3),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view4),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view5),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view6),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view7),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view8),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_image_view9)
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
        switch (story.getType()) {
            case Text:
                return StoryflowImageUtils.Config.with(context, imageView)
                        .itemType(itemType)
                        .itemWidthPx(itemWidthPixels)
                        .story(story)
                        .showText();
            case Image:
                return StoryflowImageUtils.Config.with(context, imageView)
                        .itemType(itemType)
                        .itemWidthPx(itemWidthPixels)
                        .story(story)
                        .showImage();
            default:
                throw new UnsupportedOperationException("Unsupported story type.");
        }
    }
}
