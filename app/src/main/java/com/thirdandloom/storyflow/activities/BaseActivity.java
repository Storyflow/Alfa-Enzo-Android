package com.thirdandloom.storyflow.activities;

import com.thirdandloom.storyflow.R;
import com.thirdandloom.storyflow.Theme;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.connectivity.NetworkReceiver;
import com.thirdandloom.storyflow.utils.event.HideProgressEvent;
import com.thirdandloom.storyflow.utils.event.ShowProgressEvent;
import com.thirdandloom.storyflow.utils.event.ShowWarningEvent;
import com.thirdandloom.storyflow.views.alert.QuickAlertController;
import com.thirdandloom.storyflow.views.alert.QuickAlertView;
import com.thirdandloom.storyflow.views.progress.ProgressBarController;
import com.thirdandloom.storyflow.views.toolbar.BaseToolBar;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import rx.functions.Action1;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
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
    private BaseToolBar toolbar;
    private NetworkReceiver networkReceiver;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Theme.loadRecources(this);

        DeviceUtils.updateStatusBarColor(getWindow(), getResources().getColor(getStatusBarColorResourceId()));
        initQuickAlertController();
        initProgressBar();
        initNetworkReceiver();
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

    protected <T extends Serializable> void restoreState(Class<T> type, Bundle savedInstanceState, Action1<T> restore, Action1<T> init) {
        if (savedInstanceState != null && savedInstanceState.containsKey(SAVED_INSTANCE_KEY) ) {
            restore.call(type.cast(savedInstanceState.getSerializable(SAVED_INSTANCE_KEY)));
        } else {
            Serializable initIntentSerializable = getIntent().getSerializableExtra(SAVED_INSTANCE_KEY);
            Serializable initState = getInitState();

            if (initIntentSerializable != null) {
                init.call(type.cast(initIntentSerializable));
            } else if (initState != null) {
                init.call(type.cast(initState));
            } else {
                throw new UnsupportedOperationException("You are using intent with null Serializable Extra, "
                        + "if you don't put SavedState, please override getInitState(){new SavedState();}");
            }
        }
    }

    protected Serializable getInitState() {
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
    public abstract int getStatusBarColorResourceId();

    private void initNetworkReceiver() {
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        networkReceiver = new NetworkReceiver();
        registerReceiver(networkReceiver, filter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        quickAlert.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (networkReceiver != null) {
            this.unregisterReceiver(networkReceiver);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShowProgressEvent event) {
        showProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(HideProgressEvent event) {
        hideProgress();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ShowWarningEvent event) {
        showWarning(event.getMessageResId());
    }

    protected void initQuickAlertController() {
        quickAlert = new QuickAlertController(getWindow(), getStatusBarColorResourceId());
    }

    private void findToolBar() {
        if (hasToolBar()) {
            toolbar = (BaseToolBar) findViewById(R.id.toolbar);
            if (toolbar == null) throw new UnsupportedOperationException("If activity has toolbar, u have to include layout_toolbar_simple in content view");
            setSupportActionBar(toolbar);
            toolbar.setNavigationIcon(null);
            toolbar.onUpButtonClicked(this::onUpButtonClicked);
        }
    }

    protected Toolbar getToolbar() {
        return toolbar;
    }

    @Override
    public void setTitle(int titleId) {
        if (hasToolBar() && toolbar instanceof BaseToolBar) {
            (toolbar).setTitleText(titleId);
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
