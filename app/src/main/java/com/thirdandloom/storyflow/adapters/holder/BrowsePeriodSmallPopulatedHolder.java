package com.thirdandloom.storyflow.adapters.holder;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.BrowsePeriodsAdapter;
import com.thirdandloom.storyflow.models.Mention;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.SpannableUtils;
import com.thirdandloom.storyflow.utils.Timber;

import java.util.Arrays;
import java.util.List;

public class BrowsePeriodSmallPopulatedHolder extends BrowsePeriodSmallestPopulatedHolder {

    public static BrowsePeriodSmallPopulatedHolder newInstance(ViewGroup parent, Actions actions) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_small_filled_content, parent, false);
        return new BrowsePeriodSmallPopulatedHolder(itemView, actions);
    }

    public BrowsePeriodSmallPopulatedHolder(View itemView, Actions actions) {
        super(itemView, actions);
    }

    protected List<TextView> descriptionTextViews;

    @Override
    protected void findViews() {
        dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_top_text_view);
        dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_text_view);
        dataContainer = itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_container);

        imageViews = Arrays.asList((ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view0),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view1),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view2),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view3),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view4),
                (ImageView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_image_view5)
        );
        descriptionTextViews = Arrays.asList((TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view0),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view1),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view2),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view3),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view4),
                (TextView)itemView.findViewById(R.id.adapter_recycler_item_browse_story_small_filled_content_date_bottom_story_description_view5)
        );
    }

    @Override
    protected void configureData(Context context, Story story, Integer position, int itemWidthPixels, BrowsePeriodsAdapter.ItemType itemType) {
        super.configureData(context, story, position, itemWidthPixels, itemType);
        setStoryDescription(story, position);
    }

    @Override
    protected void resetData(int position) {
        TextView textView = descriptionTextViews.get(position);
        textView.setText("");
    }

    public void setStoryDescription(Story story, int position) {
        TextView textView = descriptionTextViews.get(position);
        String storyDescription = story.getDescription();
        if (ArrayUtils.isEmpty(story.getMentionsList())) {
            textView.setText(storyDescription);
        } else {
            for (Mention mention : story.getMentionsList()) {
                String mentionName = mention.getMentionUserName();
                storyDescription = storyDescription.replace(mentionName, mention.getFullName());
            }
            SpannableString ss = new SpannableString(storyDescription);
            for (Mention mention : story.getMentionsList()) {
                SpannableUtils.setOnClick(ss, new MentionClickable(mention),
                        mention.getFullName(), storyDescription);
            }
            textView.setText(ss);
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setHighlightColor(Color.TRANSPARENT);
        }
    }

    private static class MentionClickable extends ClickableSpan {
        private final Mention mention;

        public MentionClickable(Mention mention) {
            this.mention = mention;
        }

        @Override
        public void onClick(View textView) {
            Timber.d("onClick :%s", mention);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setColor(StoryflowApplication.resources().getColor(R.color.yellow));
            ds.setUnderlineText(false);
        }
    }
}
