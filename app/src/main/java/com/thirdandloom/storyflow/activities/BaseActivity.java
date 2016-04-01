package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.views.alert.QuickAlertController;
import com.thirdandloom.storyflow.views.alert.QuickAlertView;
import com.thirdandloom.storyflow.views.progress.ProgressBarController;
import rx.functions.Action1;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

import java.io.Serializable;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String SAVED_INSTANCE_KEY = "saved_instance_key";

    private QuickAlertController quickAlert;
    private ProgressBarController progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DeviceUtils.isLollipopOrHigher()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(getStatusBarColor()));
        }
        initQuickAlertController();
        initProgressBar();
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        findToolBar();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Serializable savedState = getSavedState();
        if (savedState != null) {
            outState.putSerializable(SAVED_INSTANCE_KEY, savedState);
        }
    }

    protected void restoreState(Bundle savedInstanceState, Action1<Serializable> onRestore) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_KEY) ) {
            onRestore.call(savedInstanceState.getSerializable(SAVED_INSTANCE_KEY));
        }
    }


    protected Serializable getState() {
        return getIntent().getSerializableExtra(SAVED_INSTANCE_KEY);
    }

    protected static void putExtra(Intent intent, Serializable data) {
        intent.putExtra(SAVED_INSTANCE_KEY, data);
    }

    @Nullable
    protected Serializable getSavedState() {
        return null;
    }

    @ColorRes
    protected abstract int getStatusBarColor();

    @Override
    protected void onStop() {
        super.onStop();
        quickAlert.hide();
    }

    protected void initQuickAlertController() {
        quickAlert = new QuickAlertController(getWindow());
    }

    private void findToolBar() {
        if (hasToolBar()) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar == null) throw new UnsupportedOperationException("If activity has toolbar, u have to include layout_toolbar in content view");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(v -> onUpButtonClicked());
        }
    }

    protected abstract boolean hasToolBar();

    protected void setToolBarTitle(@StringRes int resId) {
        getSupportActionBar().setTitle(resId);
    }

    protected void onUpButtonClicked() {
        finish();
    }

    private void initProgressBar() {
        progressBar = new ProgressBarController(this, getWindow());
    }

    @UiThread
    public void showProgress(int gravity) {
        progressBar.showProgress(gravity);
    }

    @UiThread
    public void showProgress() {
        progressBar.showProgress();
    }

    @UiThread
    public void hideProgress() {
        progressBar.hideProgress();
    }

    @UiThread
    public void showWarning(@StringRes int resId) {
        showWarning(getResources().getString(resId));
    }

    @UiThread
    public void showWarning(String message) {
        quickAlert.show(message, QuickAlertView.Type.WARNING);
    }

    @UiThread
    public void showError(String message) {
        hideProgress();
        quickAlert.show(message, QuickAlertView.Type.ERROR);
    }

    @UiThread
    public void showError(@StringRes int resId) {
        showError(getResources().getString(resId));
    }

}
