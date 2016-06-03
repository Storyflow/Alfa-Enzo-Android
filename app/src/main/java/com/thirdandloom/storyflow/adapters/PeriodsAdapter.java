package com.thirdandloom.storyflow.adapters;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.ArrayUtils;
import com.thirdandloom.storyflow.utils.DateUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.views.OnSwipeStartNotifyRefreshLayout;
import com.thirdandloom.storyflow.views.recyclerview.VerticalDragNotifierRecyclerView;
import com.thirdandloom.storyflow.views.recyclerview.DisableScrollLinearLayoutManager;
import rx.functions.Func0;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class PeriodsAdapter extends RecyclerView.Adapter<PeriodsAdapter.BrowsePeriodEmptyContentHolder> {

    public enum ItemType {
        Large, Small, Smallest
    }

    public enum PeriodType {
        Daily, Monthly, Yearly
    }

    private final Handler postponeHandler = new Handler();
    private final StoriesManager storiesManager;

    private ItemType itemType = ItemType.Large;
    private PeriodType periodType = PeriodType.Daily;
    private Context context;
    private int centerPosition;
    private StoryHolder.Actions storyPreviewActions;
    private Func0<Integer> getParentHeightAction;

    public PeriodsAdapter(Context context, @Nullable LinkedHashMap<Calendar, Story.WrapList> store,
            @Nullable List<Integer> fetchedPositions, @Nullable StoriesManager.RequestData requestData) {
        this.context = context;
        this.storiesManager = new StoriesManager(store, fetchedPositions, requestData);
    }

    public void setGetParentHeightAction(Func0<Integer> getParentHeightAction) {
        this.getParentHeightAction = getParentHeightAction;
    }

    public void setStoryPreviewActions(StoryHolder.Actions storyPreviewActions) {
        this.storyPreviewActions = storyPreviewActions;
    }

    public StoriesManager getStoriesManager() {
        return storiesManager;
    }

    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }

    public ItemType getItemType() {
        return itemType;
    }

    public void setCenterPosition(int centerPosition) {
        this.centerPosition = centerPosition;
    }

    public void onNewStoriesFetched(Story.WrapList list, Calendar calendar) {
        onNewStoriesFetched(list, calendar, getPosition(calendar));
    }

    public void onNewStoriesFetched(Story.WrapList list, Calendar calendar, int position) {
        Timber.d("MEMORY LEAK onNewStoriesFetched: %d, calendar: %s", position, calendar.toString());

        storiesManager.storeData(calendar, list);
        updateDataFromLocalStore(position);
    }

    public void onNewStoriesFetchFailed(int position) {
        storiesManager.removeFromFetchedPositions(position);
        updateDataFromLocalStore(position);
    }

    public void updatePeriodType() {
        switch (storiesManager.getRequestData().getPeriodType()) {
            case Yearly:
                periodType = PeriodType.Yearly;
                break;
            case Monthly:
                periodType = PeriodType.Monthly;
                break;
            case Daily:
                periodType = PeriodType.Daily;
                break;
        }
    }

    public void setPeriodType(PeriodType periodType) {
        this.periodType = periodType;
    }

    public void clearData() {
        this.storiesManager.clearStore();
    }

    public PeriodType getPeriodType() {
        return periodType;
    }

    public void changeItemWidth() {
        switch (itemType) {
            case Large:
                itemType = ItemType.Small;
                break;
            case Small:
                itemType = ItemType.Smallest;
                break;
            case Smallest:
                itemType = ItemType.Large;
                break;
        }
    }

    public int getCenterPosition() {
        return centerPosition;
    }

    public int getItemWidthPixel() {
        switch (itemType) {
            case Large:
                return DeviceUtils.getDisplayWidth() - getItemPadding() * 2;
            case Small:
                return DeviceUtils.getDisplayWidth() / 2;
            case Smallest:
                return DeviceUtils.getDisplayWidth() / 3 - getItemPadding() * 2;
        }
        throw new UnsupportedOperationException("unsupported itemWidth is using");
    }

    //@Override
    //public StoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    //    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_main_horizontal, parent, false);
    //    StoryHolder storyHolder = new StoryHolder(v, storyPreviewActions);
    //    storyHolder.recyclerView.setRecycledViewPool(recycledViewPool);
    //    return storyHolder;
    //}

    //@Override
    //public void onBindViewHolder(StoryHolder storyHolder, int position) {
    //    ViewUtils.applyWidth(storyHolder.itemView, getItemWidthPixel());
    //    Calendar storyDate = updateDate(storyHolder, position, centerPosition, periodType);
    //
    //    BrowseStoriesAdapter adapter = (BrowseStoriesAdapter)storyHolder.recyclerView.getAdapter();
    //    if (adapter == null) {
    //        adapter = new BrowseStoriesAdapter(context, storiesManager.getDisplayingStories(storyDate), getItemWidthPixel(), getItemType());
    //        storyHolder.recyclerView.setAdapter(adapter);
    //    } else {
    //        adapter.setData(storiesManager.getDisplayingStories(storyDate), getItemWidthPixel(), getItemType());
    //        adapter.notifyDataSetChanged();
    //    }
    //
    //    switch (itemType) {
    //        case Large:
    //            adapter.setAuthorViewType(BrowseStoriesAdapter.AuthorViewType.Full);
    //            break;
    //        case Small:
    //            adapter.setAuthorViewType(BrowseStoriesAdapter.AuthorViewType.DescriptionOnly);
    //            break;
    //        case Smallest:
    //            adapter.setAuthorViewType(BrowseStoriesAdapter.AuthorViewType.None);
    //            break;
    //    }
    //
    //    ViewUtils.setHidden(storyHolder.progressBar, adapter.getDataType() != BrowseStoriesAdapter.DataType.PendingStories);
    //    storyHolder.updateEmptyView(adapter.getDataType());
    //}

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    public void updateDataFromLocalStore(int position) {
        postponeHandler.post(() -> notifyItemChanged(position));
        //postponeHandler.post(this::notifyDataSetChanged);
    }

    public int getPosition(Calendar storyDateCalendar) {
        Calendar calendar = DateUtils.todayCalendar();

        switch (periodType) {
            case Daily:
                long todayTime = calendar.getTime().getTime();
                long storyDateTime = storyDateCalendar.getTime().getTime();
                long diffDays = TimeUnit.MILLISECONDS.toDays(todayTime - storyDateTime);
                return (int) (centerPosition + diffDays);

            case Monthly:
                int diffYear = calendar.get(Calendar.YEAR) - storyDateCalendar.get(Calendar.YEAR);
                int diffMonth = diffYear * 12 + calendar.get(Calendar.MONTH) - storyDateCalendar.get(Calendar.MONTH);
                return (centerPosition + diffMonth);

            case Yearly:
                return (centerPosition + (calendar.get(Calendar.YEAR) - storyDateCalendar.get(Calendar.YEAR)));

            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
    }

    public static class StoryHolder extends RecyclerView.ViewHolder {
        public interface Actions {
            void onDragStarted();
            void onDragFinished(int velocity);
            void pullToRefreshMotionNotifier(int motionEventAction);
            void onDrag(float scrollAbsolute, float scrollDelta, View scrollingView, Calendar calendar);
            void onClick(View view, Calendar calendar);
            void onPullToRefreshStarted(SwipeRefreshLayout refreshLayout, Calendar calendar, int adapterPosition);
        }

        private TextView dateTextView;
        private TextView boldDateTextView;
        private VerticalDragNotifierRecyclerView recyclerView;
        private View progressBar;
        private View noStoriesView;
        private OnSwipeStartNotifyRefreshLayout refreshLayout;
        private Calendar dateCalendar;

        public StoryHolder(View itemView, Actions actions) {
            super(itemView);
            dateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_main_horizontal_story_text_view);
            recyclerView = (VerticalDragNotifierRecyclerView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_view);
            boldDateTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_horizontal_story_bold_text_view);
            progressBar = itemView.findViewById(R.id.adapter_recycler_item_horizontal_recycler_progress_bar);
            refreshLayout = (OnSwipeStartNotifyRefreshLayout) itemView.findViewById(R.id.adapter_recycler_item_horizontal_refresh_layout);
            noStoriesView = itemView.findViewById(R.id.adapter_recycler_item_horizontal_no_stories_view);

            refreshLayout.setColorSchemeResources(R.color.yellow, R.color.grey);
            refreshLayout.setOnRefreshListener(() -> {
                actions.onPullToRefreshStarted(refreshLayout, dateCalendar, StoryHolder.this.getAdapterPosition());
            });
            refreshLayout.setNotifier(actions::pullToRefreshMotionNotifier);

            DisableScrollLinearLayoutManager manager = new DisableScrollLinearLayoutManager(itemView.getContext());
            manager.setOrientation(LinearLayoutManager.VERTICAL);
            manager.setDisableScroll(true);

            recyclerView.setLayoutManager(manager);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setDragStarted(actions::onDragStarted);
            recyclerView.setDragFinished(actions::onDragFinished);
            recyclerView.setOnDrag((current, delta, view) -> {
                actions.onDrag(current, delta, view, getDateCalendar());
            });
            recyclerView.setOnClick(() -> {
                actions.onClick(recyclerView, getDateCalendar());
            });

            itemView.setOnClickListener(v -> {
                actions.onClick(recyclerView, getDateCalendar());
            });
        }

        public void setDateCalendar(Calendar dateCalendar) {
            this.dateCalendar = dateCalendar;
        }

        public Calendar getDateCalendar() {
            return dateCalendar;
        }

        public void updateEmptyView(BrowseStoriesAdapter.DataType dataType) {
            ViewUtils.setShown(noStoriesView, dataType == BrowseStoriesAdapter.DataType.EmptyStories);
            int backgroundColorId = dataType == BrowseStoriesAdapter.DataType.EmptyStories
                    ? R.color.greyLightest
                    : R.color.transparent;
            recyclerView.setBackgroundColor(StoryflowApplication.resources().getColor(backgroundColorId));
        }

        public void setDateRepresentation(String boldText, String formattedDate) {
            dateTextView.setText(formattedDate);
            boldDateTextView.setText(boldText);
        }
    }

    //@NonNull
    //private static Calendar updateDate(StoryHolder storyHolder, int position, int centerPosition, PeriodType periodType) {
    //    Calendar calendar = getDateCalendar(position, centerPosition, periodType);
    //    switch (periodType) {
    //        case Daily:
    //            DateUtils.getDailyRepresentation(calendar, storyHolder::setDateRepresentation);
    //            break;
    //        case Monthly:
    //            DateUtils.getMonthlyRepresentation(calendar, storyHolder::setDateRepresentation);
    //            break;
    //        case Yearly:
    //            DateUtils.getYearlyRepresentation(calendar, storyHolder::setDateRepresentation);
    //            break;
    //        default:
    //            throw new UnsupportedOperationException("unsupported itemType is using");
    //    }
    //    return calendar;
    //}

    @NonNull
    private static Calendar updateDate(BrowsePeriodEmptyContentHolder holder, int position, int centerPosition, PeriodType periodType) {
        Calendar calendar = getDateCalendar(position, centerPosition, periodType);
        switch (periodType) {
            case Daily:
                DateUtils.getDailyRepresentation(calendar, holder::setDateRepresentation);
                break;
            case Monthly:
                DateUtils.getMonthlyRepresentation(calendar, holder::setDateRepresentation);
                break;
            case Yearly:
                DateUtils.getYearlyRepresentation(calendar, holder::setDateRepresentation);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
        return calendar;
    }

    public static Calendar getDateCalendar(int position, int centerPosition, PeriodType periodType) {
        Calendar calendar = DateUtils.todayCalendar();
        int offset = position - centerPosition;
        switch (periodType) {
            case Daily:
                calendar.add(Calendar.DAY_OF_YEAR, offset);
                break;
            case Monthly:
                calendar.add(Calendar.MONTH, offset);
                break;
            case Yearly:
                calendar.add(Calendar.YEAR, offset);
                break;
            default:
                throw new UnsupportedOperationException("unsupported itemType is using");
        }
        return calendar;
    }

    public static int getItemPadding() {
        return AndroidUtils.dp(14);
    }

    public static int getItemMargin() {
        return StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.sizeTiniest);
    }

    @Override
    public BrowsePeriodEmptyContentHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        BrowsePeriodEmptyContentHolder storyHolder;
        View view;
        switch (viewType) {
            case EMPTY:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_content, parent, false);
                storyHolder = new BrowsePeriodEmptyContentHolder(view, storyPreviewActions);
                return storyHolder;
            case POPULATED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_filled_content, parent, false);
                storyHolder = new BrowsePeriodPopuladedContentHolder(view, storyPreviewActions);
                return storyHolder;
            case PENDING:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_recycler_item_browse_story_pending_content, parent, false);
                storyHolder = new BrowsePeriodPendingContentHolder(view, storyPreviewActions);
                return storyHolder;
            default:
                throw new UnsupportedOperationException("Your are using unsupported item view type");
        }
    }

    @Override
    public void onBindViewHolder(BrowsePeriodEmptyContentHolder holder, int position) {
        ViewUtils.applyWidth(holder.itemView, getItemWidthPixel());
        Calendar storyDate = updateDate(holder, position, centerPosition, periodType);
        switch (getItemViewType(position)) {
            case POPULATED:
                Story.WrapList wrapList = storiesManager.getDisplayingStories(storyDate);
                BrowsePeriodPopuladedContentHolder populatedHolder = (BrowsePeriodPopuladedContentHolder) holder;
                populatedHolder.setStories(wrapList.getStories(), context, getItemWidthPixel(), getItemType(), getParentHeightAction.call());
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Calendar calendar = getDateCalendar(position, centerPosition, periodType);
        Story.WrapList wrapList = storiesManager.getDisplayingStories(calendar);
        switch (getDataType(wrapList)) {
            case EmptyStories:
                return EMPTY;
            case PendingStories:
                return PENDING;
            case PopulatedStories:
                return POPULATED;
            default:
                throw new UnsupportedOperationException("Your are using unsupported item view type");
        }
    }

    enum DataType {
        EmptyStories, PendingStories, PopulatedStories
    }

    private static final int EMPTY = 1;
    private static final int PENDING = EMPTY + 1;
    private static final int POPULATED = PENDING + 1;


    private DataType getDataType(Story.WrapList wrapList) {
        if (wrapList == null) {
            return DataType.PendingStories;
        } else if (wrapList.getStories() == null
                || wrapList.getStories().size() == 0) {
            return DataType.EmptyStories;
        } else {
            return DataType.PopulatedStories;
        }
    }

    public static class BrowsePeriodEmptyContentHolder extends RecyclerView.ViewHolder {

        protected TextView dateTopTextView;
        protected TextView dateBottomTextView;

        public BrowsePeriodEmptyContentHolder(View itemView, StoryHolder.Actions storyPreviewActions) {
            super(itemView);
            findViews();
        }

        private void findViews() {
            dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_date_top_text_view);
            dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_content_date_bottom_text_view);
        }

        public void setDateRepresentation(String topText, String bottomText) {
            dateTopTextView.setText(topText);
            dateBottomTextView.setText(bottomText);
        }
    }

    public static class BrowsePeriodPopuladedContentHolder extends BrowsePeriodEmptyContentHolder {

        private List<ImageView> imageViews;
        private int addedImageViewsHeight;

        public BrowsePeriodPopuladedContentHolder(View itemView, StoryHolder.Actions storyPreviewActions) {
            super(itemView, storyPreviewActions);
            findViews();
        }

        private void findViews() {
            dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_top_text_view);
            dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_filled_content_date_bottom_text_view);

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

        public void setStories(List<Story> stories, Context context, int itemWidthPixels, PeriodsAdapter.ItemType itemType, int parentHeight) {
            this.addedImageViewsHeight = 0;

            ArrayUtils.forEach(imageViews, (imageView, pos) -> {
                boolean settingsStoriesCompleted = addedImageViewsHeight >= (parentHeight - dateTopTextView.getMeasuredHeight() - dateBottomTextView.getMeasuredHeight());
                imageView.setImageDrawable(null);

                if (pos < stories.size() && !settingsStoriesCompleted) {
                    int height = configureImageView(imageView, stories.get(pos), context, itemWidthPixels, itemType);
                    addedImageViewsHeight += height;
                }
            });
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
        public int configureImageView(ImageView imageView, Story story, Context context, int itemWidthPixels, PeriodsAdapter.ItemType itemType) {
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

    public static class BrowsePeriodPendingContentHolder extends BrowsePeriodEmptyContentHolder {

        public BrowsePeriodPendingContentHolder(View itemView, StoryHolder.Actions storyPreviewActions) {
            super(itemView, storyPreviewActions);
            findViews();
        }

        private void findViews() {
            dateTopTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_pendig_content_date_top_text_view);
            dateBottomTextView = (TextView) itemView.findViewById(R.id.adapter_recycler_item_browse_story_pending_content_date_bottom_text_view);
        }
    }
}
