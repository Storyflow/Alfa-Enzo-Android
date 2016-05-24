package com.thirdandloom.storyflow.fragments;


import com.thirdandloom.storyflow.utils.event.HideProgressEvent;
import com.thirdandloom.storyflow.utils.event.ShowErrorEvent;
import com.thirdandloom.storyflow.utils.event.ShowProgressEvent;
import org.greenrobot.eventbus.EventBus;

import android.support.annotation.UiThread;
import android.support.v4.app.Fragment;

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

}
