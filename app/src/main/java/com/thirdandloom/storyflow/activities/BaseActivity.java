package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.views.alert.QuickAlertController;
import com.thirdandloom.storyflow.views.alert.QuickAlertView;
import com.thirdandloom.storyflow.views.progress.ProgressBarController;
import com.thirdandloom.storyflow.views.toolbar.BaseToolBar;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.io.Serializable;

public abstract class BaseActivity extends AppCompatActivity {
    public static final String SAVED_INSTANCE_KEY = "saved_instance_key";

    private QuickAlertController quickAlert;
    private ProgressBarController progressBar;
    private Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DeviceUtils.updateStatusBarColor(getWindow(), getResources().getColor(getStatusBarColorResourceId()));
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
    protected abstract int getStatusBarColorResourceId();

    @Override
    protected void onStop() {
        super.onStop();
        quickAlert.hide();
    }

    protected void initQuickAlertController() {
        quickAlert = new QuickAlertController(getWindow(), getStatusBarColorResourceId());
    }

    private void findToolBar() {
        if (hasToolBar()) {
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar == null) throw new UnsupportedOperationException("If activity has toolbar, u have to include layout_toolbar_simple in content view");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_up);
            toolbar.setNavigationOnClickListener(v -> onUpButtonClicked());
        }
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setTitle(int titleId) {
        if (hasToolBar() && toolbar instanceof BaseToolBar) {
            ((BaseToolBar)toolbar).setTitleText(titleId);
        } else {
            super.setTitle(titleId);
        }
    }

    protected abstract boolean hasToolBar();

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

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
