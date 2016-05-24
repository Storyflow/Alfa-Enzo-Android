package com.thirdandloom.storyflow.fragments;


import com.thirdandloom.storyflow.activities.BaseActivity;
import com.thirdandloom.storyflow.utils.DeviceUtils;
import com.thirdandloom.storyflow.utils.event.HideProgressEvent;
import com.thirdandloom.storyflow.utils.event.ShowErrorEvent;
import com.thirdandloom.storyflow.utils.event.ShowProgressEvent;
import org.greenrobot.eventbus.EventBus;

import android.content.Context;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public abstract class BaseFragment extends Fragment {

    protected void showProgress() {
        EventBus.getDefault().post(new ShowProgressEvent());
    }

    protected void hideProgress() {
        EventBus.getDefault().post(new HideProgressEvent());
    }

    protected void showError(String errorMessage) {
        EventBus.getDefault().post(new ShowErrorEvent(errorMessage));
    }

    @ColorRes
    @Nullable
    protected Integer getStatusBarColorResourceId() {
        return null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        FragmentActivity activity = getActivity();
        if (!(activity instanceof BaseActivity)) {
            throw new UnsupportedOperationException("Activity should extend from BaseActivity");
        }
        Integer statusBarColorResId = getStatusBarColorResourceId();
        if (statusBarColorResId != null) {
            BaseActivity baseActivity = (BaseActivity)activity;
            DeviceUtils.updateStatusBarColor(baseActivity.getWindow(), getResources().getColor(statusBarColorResId));
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Integer statusBarColorResId = getStatusBarColorResourceId();
        if (statusBarColorResId != null) {
            BaseActivity activity = (BaseActivity)getActivity();
            DeviceUtils.updateStatusBarColor(activity.getWindow(), getResources().getColor(activity.getStatusBarColorResourceId()));
        }
    }
}
