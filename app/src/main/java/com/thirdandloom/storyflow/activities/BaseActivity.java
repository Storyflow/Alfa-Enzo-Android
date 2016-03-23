package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.views.alert.QuickAlertController;
import com.thirdandloom.storyflow.views.alert.QuickAlertView;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public abstract class BaseActivity extends AppCompatActivity {

    private QuickAlertController quickAlert;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initQuickAlertController();
    }

    @Override
    protected void onStop() {
        super.onStop();
        quickAlert.hide();
    }

    protected void initQuickAlertController() {
        quickAlert = new QuickAlertController(getWindow());
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
        quickAlert.show(message, QuickAlertView.Type.ERROR);
    }

    @UiThread
    public void showError(@StringRes int resId) {
        showError(getResources().getString(resId));
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

}
