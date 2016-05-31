package com.thirdandloom.storyflow.views.toolbar;

import com.bumptech.glide.Glide;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.adapters.PeriodsAdapter;
import com.thirdandloom.storyflow.models.User;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;
import com.thirdandloom.storyflow.utils.glide.CropCircleTransformation;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BrowseStoriesToolBar extends BaseToolBar {
    public interface Actions {
        void onChangeSizeClicked();
        void onChangePeriodClicked();
        void onChangeAuthorsClicked();
    }

    public BrowseStoriesToolBar(Context context) {
        super(context);
    }

    public BrowseStoriesToolBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrowseStoriesToolBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected int getInnerViewLayoutId() {
        return R.layout.view_toolbar_activity_browsing_stories;
    }

    private TextView userNameTextView;
    private TextView fullUserNameTextView;
    private ImageView avatarImageView;
    private ImageView sizeFilterImageVie;
    private ImageView periodFilterImageView;
    private View authorsFilterImageView;
    private Actions actions;

    @Override
    protected void init() {
        userNameTextView = (TextView) findViewById(R.id.toolbar_activity_browsing_stories_user_name);
        fullUserNameTextView = (TextView) findViewById(R.id.toolbar_activity_browsing_stories_full_name);
        avatarImageView = (ImageView) findViewById(R.id.toolbar_activity_browsing_stories_avatar);
        sizeFilterImageVie = (ImageView) findViewById(R.id.toolbar_activity_browsing_stories_increase_size);
        periodFilterImageView = (ImageView) findViewById(R.id.toolbar_activity_browsing_stories_change_period);
        authorsFilterImageView = findViewById(R.id.toolbar_activity_browsing_stories_plus_stories_chooser);

        initGui();
        initListeners();
    }

    private void initGui() {
        User user = StoryflowApplication.account().getUser();
        userNameTextView.setText(String.format("@%s", user.getUsername()));
        fullUserNameTextView.setText(user.getFullUserName());
        Glide
                .with(getContext())
                .load(user.getProfileImage().getImageUrl())
                .bitmapTransform(new CropCircleTransformation(getContext()))
                .dontAnimate()
                .into(avatarImageView);
    }

    private void initListeners() {
        periodFilterImageView.setOnClickListener(v -> {
            if (actions != null) actions.onChangePeriodClicked();
        });
        sizeFilterImageVie.setOnClickListener(v -> {
            if (actions != null) actions.onChangeSizeClicked();
        });
        authorsFilterImageView.setOnClickListener(v -> {
            if (actions != null) actions.onChangeAuthorsClicked();
        });
        SpringAnimation.initVisibleAfterClick(periodFilterImageView);
        SpringAnimation.initVisibleAfterClick(sizeFilterImageVie);
        SpringAnimation.initVisibleAfterClick(authorsFilterImageView);
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public void onNewItemWidthSelected(PeriodsAdapter.ItemType itemType) {
        sizeFilterImageVie.setImageResource(toolbarSizeImage.get(itemType.ordinal()));
    }

    private static final Map<Integer, Integer> toolbarSizeImage;
    static {
        Map<Integer, Integer> map = new HashMap<>();
        map.put(PeriodsAdapter.ItemType.Small.ordinal(), R.drawable.ic_view_2);
        map.put(PeriodsAdapter.ItemType.Large.ordinal(), R.drawable.ic_view_1);
        map.put(PeriodsAdapter.ItemType.Smallest.ordinal(), R.drawable.ic_view_3);
        toolbarSizeImage = Collections.unmodifiableMap(map);
    }
}
