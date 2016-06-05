package com.thirdandloom.storyflow.adapters.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;

import java.util.Arrays;
import java.util.List;

public class BrowsePeriodLargePopulatedHolder extends BrowsePeriodSmallPopulatedHolder {

    public static BrowsePeriodLargePopulatedHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_large_filled_content, parent, false);
        return new BrowsePeriodLargePopulatedHolder(itemView, actions);
    }

    public BrowsePeriodLargePopulatedHolder(View itemView, Actions actions) {
        super(itemView, actions);
    }

    private List<TextView> authorFullNameTextViews;
    private List<ImageView> authorAvatarImageViews;

    @Override
    protected void findViews() {
        dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_date_top_text_view);
        dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_date_bottom_text_view);

        imageViews = Arrays.asList((ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_image_view0),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_image_view1),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_image_view2),
                (ImageView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_image_view3)
        );
        descriptionTextViews = Arrays.asList((TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_story_description_text_view0),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_story_description_text_view1),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_story_description_text_view2),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_story_description_text_view3)
        );
        authorFullNameTextViews = Arrays.asList((TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_author_full_name0),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_author_full_name1),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_author_full_name2),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_large_filled_content_author_full_name3)
        );
        authorAvatarImageViews = Arrays.asList((ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_avatar0),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_avatar1),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_avatar2),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_author_avatar3)
                );
    }

    @Override
    protected void resetData(int position) {
        super.resetData(position);
        authorAvatarImageViews.get(position).setImageDrawable(null);
        authorFullNameTextViews.get(position).setText("");
    }

    @Override
    protected void configureData(Context context, Story story, Integer position, int itemWidthPixels, BrowsePeriodsAdapter.ItemType itemType) {
        super.configureData(context, story, position, itemWidthPixels, itemType);
        initAuthorUi(context, story, position);
    }

    private void initAuthorUi(Context context, Story story, int position) {
        authorFullNameTextViews.get(position).setText(story.getAuthor().getFullName());
        Glide
                .with(context)
                .load(story.getAuthor().getCroppedAvatar().getImageUrl())
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .bitmapTransform(new CropCircleTransformation(context))
                .dontAnimate()
                .into(authorAvatarImageViews.get(position));
    }

}
