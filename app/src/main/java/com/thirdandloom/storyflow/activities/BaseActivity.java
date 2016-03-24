package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.views.alert.QuickAlertController;
import com.thirdandloom.storyflow.views.alert.QuickAlertView;
import com.thirdandloom.storyflow.views.progress.ProgressBarController;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.WindowManager;

public abstract class BaseActivity extends AppCompatActivity {

    private QuickAlertController quickAlert;
    private ProgressBarController progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (DeviceUtils.isLollipopOrHigher()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.yellowDark));
        }
        initQuickAlertController();
        initProgressBar();
    }

    @Override
    protected void onStop() {
        super.onStop();
        quickAlert.hide();
    }

    protected void initQuickAlertController() {
        quickAlert = new QuickAlertController(getWindow());
    }

    public void setToolBar(@Nullable Toolbar toolBar) {
        if (toolBar != null) {
            setSupportActionBar(toolBar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolBar.setNavigationOnClickListener(v -> onUpButtonClicked());
        }
    }

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
