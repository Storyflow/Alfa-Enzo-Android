package com.thirdandloom.storyflow.fragments;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.StoryPreviewActivity;
import com.thirdandloom.storyflow.adapters.ReadStoriesAdapter;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.FooterHiderScrollListener;
import com.thirdandloom.storyflow.utils.event.StoryCreationFailedEvent;
import com.thirdandloom.storyflow.utils.event.StoryDeletePendingEvent;
import com.thirdandloom.storyflow.views.recyclerview.EndlessRecyclerOnScrollListener;
import com.thirdandloom.storyflow.views.recyclerview.RecyclerItemClickListener;
import com.thirdandloom.storyflow.views.recyclerview.decoration.DividerDecoration;
import com.thirdandloom.storyflow.views.recyclerview.decoration.GradientOnTopStickyHeaderDecoration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.functions.Action0;
import rx.functions.Action1;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Calendar;

public class ReadingStoriesFragment extends BaseFragment {
    private static final int MAX_SCALE = 100;
    private static final int FINISH_POSITION = 0;
    private static final int FINISH_ALPHA = 1;
    private static final int START_ALPHA = 0;
    private static final int MAX_PRESENT_ANIMATION_DURATION_MS = 500;
    private static final int MIN_PRESENT_ANIMATION_DURATION_MS = 0;
    private static final int PRESENT_ANIMATION_DURATION_MS = 300;

    public interface IStoryDetailFragmentDataSource {
        void setTakeScrollDelta(Action1<Float> takeScroll);
        View getBottomBar();
        void onReadingStoriesDismissed();
    }

    private RecyclerView recyclerView;
    private View viewContainer;
    private View backgroundView;

    private boolean canResize;
    private boolean didDraw;
    private boolean startPresentAnimationAfterDraw;
    private float featureWidth;
    private float featureHeight;
    private float firstStartWidth;
    private float firstStartHeight;
    private int firstStartX;
    private int firstStartY;
    private int absoluteScroll;

    private StoriesManager.RequestData requestData;
    private Calendar dateCalendar;
    private Story.WrapList stories;
    private ReadStoriesAdapter readStoriesAdapter;
    private FooterHiderScrollListener footerHiderScrollListener;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_story_details, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.fragment_story_details_recycler_view);
        viewContainer = view.findViewById(R.id.fragment_story_details_recycler_view_container);
        backgroundView = view.findViewById(R.id.fragment_story_details_background);

        requestData = getState().requestData;
        stories = getState().stories;
        dateCalendar = getState().dateCalendar;

        if (stories != null) {
            initRecyclerView();
        } else {
            loadInitStories();
        }
        takeScrollFromParent(this::onParentScroll);
        ViewUtils.callOnPreDraw(viewContainer, preDrawView -> {

            State state = (State) getArguments().getSerializable(State.KEY);
            featureHeight = preDrawView.getHeight();
            featureWidth = preDrawView.getWidth();
            firstStartHeight = state.height;
            firstStartWidth = state.width;
            firstStartX = (int) state.x;
            firstStartY = (int) state.y - DeviceUtils.getStatusBarHeight();

            preDrawView.setPivotY(0);
            preDrawView.setPivotX(0);
            view.getBackground().setAlpha(0);

            updateViewScale(preDrawView, firstStartWidth/preDrawView.getWidth(), firstStartHeight/preDrawView.getHeight(), firstStartX, firstStartY);
            didDraw = true;
            if (state.startPresentAnimation || startPresentAnimationAfterDraw) {
                present();
            } else {
                canResize = true;
            }
        });

        return view;
    }

    private State getState() {
        return (State) getArguments().getSerializable(State.KEY);
    }

    private void initRecyclerView() {
        final DividerDecoration divider = new DividerDecoration.Builder(this.getActivity())
                .setHeight(R.dimen.sizeNormal)
                .setColorResource(R.color.greyMostLightest)
                .build();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(divider);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        readStoriesAdapter = new ReadStoriesAdapter(stories, dateCalendar, requestData, getActivity());
        GradientOnTopStickyHeaderDecoration decor = new GradientOnTopStickyHeaderDecoration(readStoriesAdapter, true);
        setHasOptionsMenu(true);
        recyclerView.setAdapter(readStoriesAdapter);
        recyclerView.addItemDecoration(decor, 1);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreStories();
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), (viewHolder, position) -> {
            if (readStoriesAdapter.getItemViewType(position) == ReadStoriesAdapter.FILLED_STORY) {
                Intent intent = StoryPreviewActivity.newInstance(readStoriesAdapter.getStory(position),
                        readStoriesAdapter.getFromView(position, viewHolder));
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(0, 0);
            }
        }));
        footerHiderScrollListener = FooterHiderScrollListener.init(recyclerView, getBottomBar());
    }

    private void loadInitStories() {
        showProgress();
        requestData.setDate(dateCalendar.getTime());
        StoryflowApplication.restClient().loadStories(requestData, (Story.WrapList list) -> {
            hideProgress();
            stories = list;
            initRecyclerView();
        }, (errorMessage, type) -> {
            showError(errorMessage);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StoryCreationFailedEvent event) {
        readStoriesAdapter.onStoryCreationFailed(event.getStory());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(StoryDeletePendingEvent event) {
        readStoriesAdapter.onPendingStoryDelete(event.getStory());
    }

    private void loadMoreStories() {
        String nextStoryDate = readStoriesAdapter.getNextStoryDate();
        if (nextStoryDate != null) {
            requestData.setDirection(StoriesManager.RequestData.Direction.Type.Forward);
            requestData.setNextStoryDate(nextStoryDate);
            requestData.setDate(readStoriesAdapter.getCurrentCalendarDate().getTime());
            StoryflowApplication.restClient().loadStories(requestData, (Story.WrapList list) -> {
                readStoriesAdapter.addMoreStories(list);
            }, (errorMessage, type) -> {
                //need to check fail behaviour
                readStoriesAdapter.notifyDataSetChanged();
                Timber.d("onLoadMore error: %s", errorMessage);
            });

        } else {
            Calendar nextCalendar = readStoriesAdapter.getPreviousCalendarDate();
            requestData.setDate(nextCalendar.getTime());
            requestData.setNextStoryDate(null);
            requestData.setDirection(StoriesManager.RequestData.Direction.Type.None);
            StoryflowApplication.restClient().loadStories(requestData, (Story.WrapList list) -> {
                readStoriesAdapter.addNewStories(list, nextCalendar);
                readStoriesAdapter.notifyDataSetChanged();
            }, (errorMessage, type) -> {
                //need to check fail behaviour
                readStoriesAdapter.notifyDataSetChanged();
                Timber.d("onLoadMore error: %s", errorMessage);
            });
        }
    }

    public void onDragFinished(int velocity) {
        if (velocity > AndroidUtils.minVelocityPxPerSecond() || absoluteScroll >= getFinishScrollValue()/2) {
            startFinishPresentAnimation(calculateAnimationDuration());
        } else {
            startFinishDismissAnimation(MAX_PRESENT_ANIMATION_DURATION_MS - calculateAnimationDuration(), null);
        }
    }

    private void present() {
        startFinishPresentAnimation(PRESENT_ANIMATION_DURATION_MS);
    }

    public void dismiss(Action0 complete) {
        startFinishDismissAnimation(PRESENT_ANIMATION_DURATION_MS, complete);
    }

    public void dismiss() {
        startFinishDismissAnimation(PRESENT_ANIMATION_DURATION_MS, null);
    }

    private void startFinishDismissAnimation(int animationDuration, Action0 complete) {
        if (footerHiderScrollListener != null) footerHiderScrollListener.showFooter();
        viewContainer.animate().setDuration(animationDuration)
                .scaleX(firstStartWidth / featureWidth)
                .scaleY(firstStartHeight / featureHeight)
                .translationX(firstStartX)
                .translationY(firstStartY)
                .withEndAction(() -> {
                    FragmentManager manager = getFragmentManager();
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.remove(this);
                    transaction.commit();
                    if (complete != null) complete.call();
                    ((IStoryDetailFragmentDataSource)getActivity()).onReadingStoriesDismissed();
                })
                .start();

        backgroundView.animate().setDuration(animationDuration)
                .alpha(START_ALPHA)
                .start();
    }

    private void startFinishPresentAnimation(int animationDuration) {
        if (!didDraw && !isAdded()) {
            Bundle bundle = getArguments();
            State state = (State) bundle.getSerializable(State.KEY);
            state.startPresentAnimation = true;
            bundle.putSerializable(State.KEY, state);
            setArguments(bundle);
        } else if (isAdded() && !didDraw) {
            startPresentAnimationAfterDraw = true;
        } else {
            viewContainer.animate().setDuration(animationDuration)
                    .scaleX(1)
                    .scaleY(1)
                    .translationX(0)
                    .translationY(0)
                    .start();

            backgroundView.animate().setDuration(animationDuration)
                    .alpha(FINISH_ALPHA)
                    .start();
        }
    }

    private int calculateAnimationDuration() {
        Point start = new Point(firstStartY, MAX_PRESENT_ANIMATION_DURATION_MS);
        Point end = new Point(FINISH_POSITION, MIN_PRESENT_ANIMATION_DURATION_MS);
        int animationDuration = Math.round(MathUtils.getPointY(start, end, getCurrentValue()));
        return animationDuration;
    }

    private int getFinishScrollValue() {
        return Math.round(firstStartY);
    }

    private void updateViewScale(View view, float widthScale, float heightScale, float leftDelta, float topDelta) {
        view.setScaleX(widthScale);
        view.setScaleY(heightScale);
        view.setTranslationX(leftDelta);
        view.setTranslationY(topDelta);
    }

    private void onParentScroll(Float dy) {
        recyclerView.scrollBy(0, Math.round(dy));
        if (!canResize) return;
        absoluteScroll += dy;

        float topDelta = firstStartY;
        float leftDelta = firstStartX;
        float widthScale = firstStartWidth / featureWidth;
        float heightScale = firstStartHeight / featureHeight;

        int largestValueStart = Math.round(topDelta);
        int largestValueFinish = FINISH_POSITION;

        int currentValue = getCurrentValue();
        Point pointEnd = new Point(largestValueFinish, MAX_SCALE); // largest changing value

        Point heightPoint = new Point(largestValueStart, Math.round(heightScale * MAX_SCALE));
        heightScale = MathUtils.getPointY(heightPoint, pointEnd, currentValue) / MAX_SCALE;

        Point widthPoint = new Point(largestValueStart, Math.round(widthScale * MAX_SCALE));
        widthScale = MathUtils.getPointY(widthPoint, pointEnd, currentValue) / MAX_SCALE;

        pointEnd = new Point(FINISH_POSITION, FINISH_POSITION);
        Point leftDeltaPoint = new Point(largestValueStart, Math.round(leftDelta));
        leftDelta = MathUtils.getPointY(leftDeltaPoint, pointEnd, currentValue);

        Point alphaStart = new Point(largestValueStart, START_ALPHA);
        Point alphaEnd = new Point(largestValueFinish, FINISH_ALPHA);
        float currentAlpha = MathUtils.getPointY(alphaStart, alphaEnd, currentValue);

        updateViewScale(viewContainer, widthScale, heightScale, leftDelta, currentValue);
        backgroundView.setAlpha(currentAlpha);
    }

    private int getCurrentValue() {
        int largestValueStart = Math.round(firstStartY);
        return Math.max(0, Math.min(largestValueStart - absoluteScroll, largestValueStart));
    }

    private void takeScrollFromParent(Action1<Float> onScroll) {
        ((IStoryDetailFragmentDataSource)getActivity()).setTakeScrollDelta(onScroll);
    }

    public void onHomeClicked() {
        if (recyclerView.computeVerticalScrollOffset() != 0) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
            linearLayoutManager.smoothScrollToPosition(recyclerView, null, 0);
            StoryflowApplication.runOnUIThread(() -> {
                if (recyclerView.computeVerticalScrollOffset() != 0) {
                    linearLayoutManager.scrollToPosition(0);
                }
            }, 300);
        } else {
            dismiss();
        }
    }

    private View getBottomBar() {
        return ((IStoryDetailFragmentDataSource)getActivity()).getBottomBar();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof IStoryDetailFragmentDataSource)) {
            throw new UnsupportedOperationException("activity should be instanceof IStoryDetailFragmentDataSource");
        }
    }

    @Nullable
    @Override
    protected Integer getStatusBarColorResourceId() {
        return R.color.black;
    }

    public static ReadingStoriesFragment newInstance(View fromView, boolean startPresentAnimation, StoriesManager storiesManager, Calendar calendar) {
        ReadingStoriesFragment fragment = new ReadingStoriesFragment();
        Bundle args = new Bundle();
        State state = new State();
        ViewUtils.getLocationInWindow(fromView, (x, y) -> {
            state.x = x;
            state.y = y;
        });
        state.width = fromView.getWidth();
        state.height = fromView.getHeight();
        state.startPresentAnimation = startPresentAnimation;
        state.requestData = storiesManager.getRequestData();
        state.stories = storiesManager.getStoreStories(calendar);
        state.dateCalendar = calendar;
        args.putSerializable(State.KEY, state);
        fragment.setArguments(args);
        return fragment;
    }

    private static class State implements Serializable {
        private static final long serialVersionUID = -6480302811663263521L;
        private static final String KEY = "saved_instance_state";

        private float x;
        private float y;
        private float width;
        private float height;

        private boolean startPresentAnimation;

        private StoriesManager.RequestData requestData;
        private Story.WrapList stories;
        private Calendar dateCalendar;
    }
}
