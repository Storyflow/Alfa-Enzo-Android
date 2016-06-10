package com.thirdandloom.storyflow.views;

import com.facebook.rebound.Spring;
import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.AndroidUtils;
import com.thirdandloom.storyflow.utils.Timber;
import com.thirdandloom.storyflow.utils.ViewUtils;
import com.thirdandloom.storyflow.utils.animations.SpringAnimation;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Arrays;
import java.util.List;

public class TabBar extends LinearLayout {
    public interface Actions {
        void updatesClicked();
        void messagesClicked();
        void postClicked();
        void profileClicked();
        boolean handleHomeClicked();
        void homeLongClicked();
        void homeDraggingFinished();
        Window getWindow();
    }

    private int scrollPosition;
    private int itemWidth;
    private View flipCircleView;
    private OnScrollListener recyclerViewScrollListener = new OnScrollListener();
    private Actions actions;
    private ImageView triangleView;
    private ViewGroup circlesContainer;
    private View flipCircleContainerView;
    private boolean ableToFlipCircle;

    public TabBar(Context context) {
        this(context, null);
    }

    public TabBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_tab_bar, this);
        setOrientation(HORIZONTAL);
        setMinimumHeight(StoryflowApplication.resources().getDimensionPixelOffset(R.dimen.tabBarHeight));
        setPadding(0, 0, 0, getResources().getDimensionPixelOffset(R.dimen.sizeNormal));

        View updatesView = findViewById(R.id.view_tab_bar_updates);
        View messagesView = findViewById(R.id.view_tab_bar_messages);
        View postView = findViewById(R.id.view_tab_bar_post);
        View profileView = findViewById(R.id.view_tab_bar_profile);
        flipCircleContainerView = findViewById(R.id.view_tab_bar_flip_circles_container);
        ableToFlipCircle = true;

        updatesView.setOnClickListener(v -> actions.updatesClicked());
        messagesView.setOnClickListener(v -> actions.messagesClicked());
        postView.setOnClickListener(v -> actions.postClicked());
        profileView.setOnClickListener(v -> actions.profileClicked());

        SpringAnimation.init(postView);
        SpringAnimation.init(profileView);
        SpringAnimation.init(messagesView);
        SpringAnimation.init(updatesView);

        flipCircleView = findViewById(R.id.view_tab_bar_flippable_circle);
        int distance = 8000;
        float scale = getResources().getDisplayMetrics().density * distance;
        flipCircleView.setCameraDistance(scale);

        triangleView = (ImageView)findViewById(R.id.view_tab_bar_triangle_image_view);
        circlesContainer = (ViewGroup)findViewById(R.id.view_tab_bar_circles_container);
        circlesContainer.setOnClickListener(v -> {
            ableToFlipCircle = false;
            flipCirclesWithScrollPosition(scrollPosition);
            ableToFlipCircle = actions.handleHomeClicked();
        });
        circlesContainer.setOnLongClickListener(v -> {
            actions.homeLongClicked();
            return true;
        });

        List<View> animatedViews = Arrays.asList(triangleView, flipCircleView);
        SpringAnimationListener springAnimationListener = new SpringAnimationListener(animatedViews, circlesContainer, true);
        SpringAnimation.init(circlesContainer, springAnimationListener, new OnTouchCirclesListener());
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
        this.scrollPosition = 0;
    }

    public void setActions(Actions actions) {
        this.actions = actions;
    }

    public OnScrollListener getRecyclerViewScrollListener() {
        return recyclerViewScrollListener;
    }

    private class OnScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (recyclerView.getScrollState() == RecyclerView.SCROLL_STATE_IDLE) {
                ableToFlipCircle = true;
                scrollPosition = 0;
            }
            if (!ableToFlipCircle) return;
            scrollPosition += dx;
            flipCirclesWithScrollPosition(scrollPosition);
        }
    }

    private void flipCirclesWithScrollPosition(int pos) {
        int integrate = (int)Math.floor(pos/itemWidth);
        float fraction = (float)pos/itemWidth - integrate;
        float angle = fraction * 100 * 1.8f;
        flipCircleView.setRotationY(angle);
    }

    public class OnTouchCirclesListener extends SpringAnimation.ClickableOnTouchListener {
        private ViewGroup.LayoutParams previousLayoutParams;
        private float previousFlipCircleX;
        private float previousFlipCircleY;
        private boolean moveStarted;
        private int startedX;
        private int startedY;
        private float startScrollPosition;

        @Override
        protected boolean onTouchView(View v, MotionEvent event) {
            super.onTouchView(v, event);
            Timber.d("tab bar onTouchView.get action: %d", event.getAction());
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startScrollPosition = event.getY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float delta = startScrollPosition - event.getY();
                    if (Math.abs(delta) > AndroidUtils.minScrollPx()) {
                        listener.breakAnyClick = true;
                        Rect windowVisibleRect = AndroidUtils.getWindowVisibleRect(actions.getWindow());
                        int triangleX = (int)event.getRawX()-windowVisibleRect.width()/2;
                        int triangleY = (int)event.getRawY()-windowVisibleRect.height()/2 - triangleView.getDrawable().getMinimumHeight()/2;
                        int flipX = triangleX + windowVisibleRect.width()/2 - flipCircleView.getWidth()/2;
                        int flipY = triangleY + windowVisibleRect.height()/2 - flipCircleView.getHeight()/2;

                        if (circlesContainer.indexOfChild(flipCircleContainerView) >= 0) {
                            movingStarted(actions.getWindow(), triangleX, triangleY, flipX, flipY);
                        } else {
                            movingContinue(triangleX, triangleY, flipX, flipY);
                        }
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    moveFinished();
                    break;
            }
            return true;
        }

        private void moveFinished() {
            moveStarted = false;
            if (circlesContainer.indexOfChild(flipCircleContainerView) < 0) {
                triangleView.animate()
                        .translationX(startedX)
                        .translationY(startedY)
                        .setDuration(150)
                        .withEndAction(() -> {
                            ViewUtils.removeFromParent(flipCircleContainerView);
                            flipCircleContainerView.setLayoutParams(previousLayoutParams);
                            circlesContainer.addView(flipCircleContainerView);
                            ViewUtils.callOnPreDraw(circlesContainer, view -> {
                                triangleView.setX(0);
                                triangleView.setY(0);
                                flipCircleView.setX(previousFlipCircleX);
                                flipCircleView.setY(previousFlipCircleY);
                                ViewUtils.callOnPreDraw(flipCircleContainerView, v -> {
                                    actions.homeDraggingFinished();
                                });
                            });
                        }).start();

                flipCircleView.animate()
                        .translationX(startedX)
                        .translationY(startedY)
                        .setDuration(150)
                        .start();
            }
        }

        private void movingContinue(int triangleX, int triangleY, int flipX, int flipY) {
            if (moveStarted) {
                triangleView.setX(triangleX);
                triangleView.setY(triangleY);
                flipCircleView.setX(flipX);
                flipCircleView.setY(flipY);
            }
        }

        private void movingStarted(Window window, int triangleX, int triangleY, int flipX, int flipY) {
            previousFlipCircleY = flipCircleView.getY();
            previousFlipCircleX = flipCircleView.getX();
            previousLayoutParams = flipCircleContainerView.getLayoutParams();

            circlesContainer.removeView(flipCircleContainerView);
            window.addContentView(flipCircleContainerView, ViewUtils.getMatchParentWindowLayoutParams());
            startedX = triangleX;
            startedY = triangleY;

            ViewUtils.callOnPreDraw(flipCircleContainerView, view -> {
                triangleView.setX(triangleX);
                triangleView.setY(triangleY);
                flipCircleView.setX(flipX);
                flipCircleView.setY(flipY);
                moveStarted = true;
            });
        }
    }

    public class SpringAnimationListener extends SpringAnimation.ViewSpringListener {

        private boolean performLongClick;

        public SpringAnimationListener(List<View> animatedViewsList, View clickableView, boolean visibleAfterClick) {
            super(animatedViewsList, clickableView, visibleAfterClick);
        }

        @Override
        public void onSpringUpdate(Spring spring) {
            super.onSpringUpdate(spring);
            if ((userAction == MotionEvent.ACTION_DOWN || userAction == MotionEvent.ACTION_MOVE)
                    && (float) spring.getCurrentValue() >= SpringAnimation.START) {
                if (spring.getEndValue() == SpringAnimation.START) {
                    performLongClick = true;
                    spring.setEndValue(SpringAnimation.END);
                }
            }
        }

        @Override
        protected float getScaleValue(Spring spring) {
            float value = (float) spring.getCurrentValue();
            float scale = performLongClick
                                ? SpringAnimation.START - (value * 1.0f)
                                : SpringAnimation.START + (value * 0.5f);
            return scale;
        }

        @Override
        public void onSpringAtRest(Spring spring) {
            super.onSpringAtRest(spring);

            if (performLongClick && (userAction == MotionEvent.ACTION_DOWN || userAction == MotionEvent.ACTION_MOVE)) {
                performLongClick = false;
                if (!breakAnyClick) clickableView.performLongClick();
            }
        }

        @Override
        public void onSpringEndStateChange(Spring spring) {
            if (!breakAnyClick && spring.getEndValue() == SpringAnimation.END && !performLongClick) {
                clickableView.callOnClick();
            }
        }

        @Override
        protected void reset() {
            super.reset();
            performLongClick = false;
        }
    }
}
