package com.thirdandloom.storyflow.fragments;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.StoryPreviewActivity;
import com.thirdandloom.storyflow.adapters.ReadStoriesAdapter;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesBaseViewHolder;
import com.thirdandloom.storyflow.adapters.holder.ReadStoriesPopulatedViewHolder;
import com.thirdandloom.storyflow.managers.StoriesManager;
import com.thirdandloom.storyflow.models.Likes;
import com.thirdandloom.storyflow.models.Story;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.MathUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.FooterHiderScrollListener;
import com.thirdandloom.storyflow.utils.animations.SimpleAnimatorListener;
import com.thirdandloom.storyflow.utils.event.StoryCreationFailedEvent;
import com.thirdandloom.storyflow.utils.event.StoryDeletePendingEvent;
import com.thirdandloom.storyflow.views.recyclerview.EndlessRecyclerOnScrollListener;
import com.thirdandloom.storyflow.views.recyclerview.animator.ChangeLikesContainerHeightAnimator;
import com.thirdandloom.storyflow.views.recyclerview.decoration.GradientOnTopStickyHeaderDecoration;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.functions.Action0;
import rx.functions.Action1;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.Serializable;
import java.util.Calendar;
import java.util.HashMap;

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

    enum LikeAction {
        Like, Dislike;
    }
    private HashMap<Story, LikeAction> storiesLikesActions = new HashMap<>();

    private RecyclerView recyclerView;
    private ViewGroup viewContainer;
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
        viewContainer = (ViewGroup) view.findViewById(R.id.fragment_story_details_recycler_view_container);
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

            updateRecyclerScale(firstStartWidth/preDrawView.getWidth(), firstStartY);
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new ChangeLikesContainerHeightAnimator());

        readStoriesAdapter = new ReadStoriesAdapter(getActivity(), stories, dateCalendar, requestData, readStoriesActions);
        GradientOnTopStickyHeaderDecoration decor = new GradientOnTopStickyHeaderDecoration(readStoriesAdapter, true);
        setHasOptionsMenu(true);
        recyclerView.setAdapter(readStoriesAdapter);
        recyclerView.addItemDecoration(decor);
        recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore() {
                loadMoreStories();
            }
        });
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
            startFinishDismissAnimation(MAX_PRESENT_ANIMATION_DURATION_MS - calculateAnimationDuration(), null, getCurrentValue());
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
        startFinishDismissAnimation(animationDuration, complete, 0);
    }

    private void startFinishDismissAnimation(int animationDuration, Action0 complete, int currentValue) {
        if (footerHiderScrollListener != null) footerHiderScrollListener.showFooter();

        ValueAnimator animator = createRecyclerViewAnimator(true, currentValue);
        animator.addListener(new SimpleAnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (complete != null) complete.call();
                FragmentManager manager = getFragmentManager();
                if (manager != null) {
                    FragmentTransaction transaction = manager.beginTransaction();
                    transaction.remove(ReadingStoriesFragment.this);
                    transaction.commit();
                }
                ((IStoryDetailFragmentDataSource)getActivity()).onReadingStoriesDismissed();
            }
        });
        animator.setDuration(animationDuration);
        animator.start();
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
            ValueAnimator animator = createRecyclerViewAnimator(false, getCurrentValue());
            animator.setDuration(animationDuration);
            animator.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    recyclerView.requestLayout();
                }
            });
            animator.start();
            backgroundView.animate().setDuration(animationDuration)
                    .alpha(FINISH_ALPHA)
                    .start();
        }
    }

    private ValueAnimator createRecyclerViewAnimator(boolean dismiss, int currentValue) {
        int finishPosition = dismiss
                                ? firstStartY
                                : FINISH_POSITION;
        ValueAnimator animator = ValueAnimator.ofInt(currentValue, finishPosition);
        animator.addUpdateListener(animation -> {
            int animatedValue = (Integer)animation.getAnimatedValue();
            updateRecyclerScale(calculateWidthScale(animatedValue), animatedValue);
        });
        return animator;
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

    private void updateRecyclerScale(float widthScale, int currentValue) {
        float previousItemBottomPoint = 0;
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View childView = recyclerView.getChildAt(i);
            int newWidthAfterScale = (int)(childView.getWidth()*widthScale);
            float scaleHeight = newWidthAfterScale/(float)childView.getWidth();
            int newHeightAfterScale = (int)(childView.getHeight()*scaleHeight);
            int currentTop = childView.getTop()+(childView.getHeight()-newHeightAfterScale)/2;

            if (i == 0) {
                float defaultPivotY = childView.getPivotY();
                float deltaPivotY = defaultPivotY - (float)currentValue;
                childView.setTranslationY(newHeightAfterScale/2 - deltaPivotY);
            } else {
                childView.setTranslationY((int)previousItemBottomPoint - currentTop);
            }
            previousItemBottomPoint = currentTop + newHeightAfterScale + childView.getTranslationY();
            previousItemBottomPoint -= calculateNextItemOverhead(recyclerView.getChildViewHolder(childView), scaleHeight, newHeightAfterScale, currentValue);

            childView.setScaleX(widthScale);
            childView.setScaleY(scaleHeight);
        }
    }

    private float calculateNextItemOverhead(RecyclerView.ViewHolder childViewHolder, float scaleHeight, int newHeightAfterScale, int currentValue) {
        ReadStoriesBaseViewHolder holder = (ReadStoriesBaseViewHolder)childViewHolder;
        if (holder instanceof ReadStoriesPopulatedViewHolder) {
            ReadStoriesPopulatedViewHolder populatedViewHolder = (ReadStoriesPopulatedViewHolder) holder;
            float startDelta = newHeightAfterScale - populatedViewHolder.imageView.getHeight()*scaleHeight;
            startDelta -= AndroidUtils.getDimensionPixelSize(R.dimen.sizeTiniest);
            Point start = new Point(firstStartY, (int)startDelta);
            Point end = new Point(FINISH_POSITION, 0);
            return MathUtils.getPointY(start, end, currentValue);
        } else {
            return 0;
        }
    }

    private void onParentScroll(Float dy) {
        if (!canResize) return;
        absoluteScroll += dy;

        int currentValue = getCurrentValue();
        float widthScale = calculateWidthScale(currentValue);
        if (widthScale*MAX_SCALE == MAX_SCALE || recyclerView.computeVerticalScrollOffset() != 0 ) {
            recyclerView.scrollBy(0, Math.round(dy));
            ViewUtils.resetChildScale(recyclerView);
            recyclerView.requestLayout();
        } else {
            updateRecyclerScale(widthScale, currentValue);
            backgroundView.setAlpha(calculateCurrentAlpha(currentValue));
        }
    }

    private float calculateCurrentAlpha(int currentValue) {
        float topDelta = firstStartY;

        int largestValueStart = Math.round(topDelta);
        int largestValueFinish = FINISH_POSITION;

        Point alphaStart = new Point(largestValueStart, START_ALPHA);
        Point alphaEnd = new Point(largestValueFinish, FINISH_ALPHA);
        float currentAlpha = MathUtils.getPointY(alphaStart, alphaEnd, currentValue);
        return currentAlpha;
    }

    private float calculateWidthScale(int currentValue) {
        float topDelta = firstStartY;
        float widthScale = firstStartWidth / featureWidth;

        int largestValueStart = Math.round(topDelta);
        int largestValueFinish = FINISH_POSITION;

        Point pointEnd = new Point(largestValueFinish, MAX_SCALE); // largest changing value
        Point widthPoint = new Point(largestValueStart, Math.round(widthScale * MAX_SCALE));
        widthScale = MathUtils.getPointY(widthPoint, pointEnd, currentValue) / MAX_SCALE;
        return widthScale;
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

    private final ReadStoriesAdapter.Actions readStoriesActions = new ReadStoriesAdapter.Actions() {
        @Override
        public void startPreview(Story story, View fromView) {
            Intent intent = StoryPreviewActivity.newInstance(story, fromView);
            getActivity().startActivity(intent);
            getActivity().overridePendingTransition(0, 0);
        }

        @Override
        public void likeClicked(Story story, Likes likes, ReadStoriesPopulatedViewHolder holder) {
            LikeAction likeAction = likes.containsCurrentUserLike()
                                            ? LikeAction.Like
                                            : LikeAction.Dislike;

            if (!storiesLikesActions.containsKey(story)) {
                storiesLikesActions.put(story, likeAction);
                if (likeAction == LikeAction.Like) {
                    likeStory(story, (errorMessage) -> {
                        showError(errorMessage);
                        readStoriesAdapter.likeActionFailed(story, holder);
                    });
                } else {
                    dislikeStory(story, (errorMessage) ->  {
                        showError(errorMessage);
                        readStoriesAdapter.likeActionFailed(story, holder);
                    });
                }
            } else {
                storiesLikesActions.put(story, likeAction);
            }
        }
    };

    private void likeStory(Story story, Action1<String> actionFailed) {
        StoryflowApplication.restClient().likeStory(story.getId(), (response) -> {
            lastUserLikeAction(story, () -> {
                storiesLikesActions.remove(story);
            }, () -> dislikeStory(story, actionFailed));
        },((errorMessage, errorType) -> {
            lastUserLikeAction(story,() -> actionFailed.call(errorMessage) , () -> {
                //do nothing, last users action was dislike story
            });
            storiesLikesActions.remove(story);
        }));
    }

    private void dislikeStory(Story story, Action1<String> actionFailed) {
        StoryflowApplication.restClient().dislikeStory(story.getId(), (response) -> {
            lastUserLikeAction(story, () -> {
                likeStory(story, actionFailed);
            } , () -> storiesLikesActions.remove(story));
        },((errorMessage, errorType) -> {
            lastUserLikeAction(story, () -> {
                //do nothing, last users action was like story
            }, () -> actionFailed.call(errorMessage));
            storiesLikesActions.remove(story);
        }));
    }

    private void lastUserLikeAction(Story story, Action0 likeAction, Action0 dislikeAction) {
        LikeAction lastUserLikeAction = storiesLikesActions.get(story);
        if (lastUserLikeAction == LikeAction.Like) likeAction.call();
        if (lastUserLikeAction == LikeAction.Dislike) dislikeAction.call();
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
