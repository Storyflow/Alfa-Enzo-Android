package com.thirdandloom.storyflow.activities.registration;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.activities.BaseActivity;
import com.thirdandloom.storyflow.utils.glide.LogCrashRequestListener;
import com.thirdandloom.storyflow.utils.image.Size;
import com.thirdandloom.storyflow.views.toolbar.ConfigureAvatarToolBar;
import rx.functions.Action4;
import uk.co.senab.photoview.IPhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import java.io.Serializable;

public class ConfigureAvatarRectangleActivity extends BaseActivity {
    private static final float DELTA_SCALE = 0.3f;
    private static final float MIN_SCALE = IPhotoView.DEFAULT_MIN_SCALE - DELTA_SCALE;
    private static final float INITIAL_SCALE = MIN_SCALE + DELTA_SCALE;
    private static final String TAKEN_ACTION_KEY = "taken_action_key";
    private static final String TAKEN_IMAGE_URL_KEY = "taken_image_url_key";
    private static final String AVATAR_RECT_KEY = "avatar_rect_key";
    private static final String AVATAR_CACHED_SIZE_KEY = "avatar_cached_size_key";

    public static Intent newInstance(String imageData, int takenAction) {
        Intent intent = new Intent(StoryflowApplication.applicationContext, ConfigureAvatarRectangleActivity.class);
        SavedState state = new SavedState();
        state.takenAction = takenAction;
        state.imageData = imageData;
        putExtra(intent, state);

        return intent;
    }

    private ImageView profileImageView;
    private PhotoViewAttacher photoViewAttacher;
    private SavedState state;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profile_picture_rect);
        restoreState(SavedState.class, savedInstanceState, restored -> {
            state = restored;
        }, inited -> {
            state = inited;
        });

        profileImageView = (ImageView) findViewById(R.id.activity_set_profile_picture_avatar);
        initGui();
        startLoadingImage();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private void okClicked() {
        setResult(RESULT_OK, getSuccessIntent());
        finish();
    }

    private void initGui() {
        setTitle(R.string.set_profile_picture);
        photoViewAttacher = new PhotoViewAttacher(profileImageView);
        photoViewAttacher.setMinimumScale(MIN_SCALE);
        photoViewAttacher.setScale(INITIAL_SCALE, false);

        ((ConfigureAvatarToolBar)getToolbar()).setOnOkClicked(this::okClicked);
    }

    private Intent getSuccessIntent() {
        Intent data = new Intent();
        data.putExtra(TAKEN_IMAGE_URL_KEY, state.imageData);
        data.putExtra(TAKEN_ACTION_KEY, state.takenAction);

        int loadedImageHeight = (int)((photoViewAttacher.getDisplayRect().bottom - photoViewAttacher.getDisplayRect().top)/photoViewAttacher.getScale());
        int loadedImageWidth = (int)((photoViewAttacher.getDisplayRect().right - photoViewAttacher.getDisplayRect().left)/photoViewAttacher.getScale());
        data.putExtra(AVATAR_CACHED_SIZE_KEY, new com.thirdandloom.storyflow.utils.image.Size(loadedImageWidth, loadedImageHeight));

        float top = Math.abs(photoViewAttacher.getDisplayRect().top/photoViewAttacher.getScale());
        float left = Math.abs(photoViewAttacher.getDisplayRect().left/photoViewAttacher.getScale());
        float squareSize = profileImageView.getWidth()/photoViewAttacher.getScale();
        RectF displayedRect = new RectF(left, top, left + squareSize, top + squareSize);
        data.putExtra(AVATAR_RECT_KEY, displayedRect);

        return data;
    }

    private void startLoadingImage() {
        ChooseAvatarAndNameActivity.createGlideRequest(this, state.takenAction, state.imageData)
                .dontAnimate()
                .listener(new LogCrashRequestListener<>())
                .into(profileImageView);
    }

    @Override
    public int getStatusBarColorResourceId() {
        return R.color.greyXLighter;
    }

    @Override
    protected boolean hasToolBar() {
        return true;
    }

    @Nullable
    @Override
    protected Serializable getSavedState() {
        return state;
    }

    private static class SavedState implements Serializable {
        int takenAction;
        String imageData;
    }

    public static void extractData(Intent data, Action4<String, Integer, RectF, Size> extracted) {
        String url = data.getStringExtra(TAKEN_IMAGE_URL_KEY);
        Integer action = data.getExtras().getInt(TAKEN_ACTION_KEY);
        RectF rect = data.getParcelableExtra(AVATAR_RECT_KEY);
        Size cachedSize = (Size)data.getExtras().getSerializable(AVATAR_CACHED_SIZE_KEY);

        extracted.call(url, action, rect, cachedSize);
    }
}
