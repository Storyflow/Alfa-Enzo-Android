package com.thirdandloom.storyflow.utils.connectivity;

import com.thirdandloom.storyflow.utils.BaseUtils;
import com.thirdandloom.storyflow.utils.event.NetworkChangedEvent;
import org.greenrobot.eventbus.EventBus;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityObserver {
    private NetworkType networkType;

    public ConnectivityObserver() {
        networkType = getInitNetworkType();
        stateChanged();
    }

    private static NetworkType getInitNetworkType() {
        ConnectivityManager cm = BaseUtils.connectivityManager();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return ConnectivityUtils.getNetworkType(activeNetwork);
    }

    public void onNetworkTypeChanged(NetworkType networkType) {
        this.networkType = networkType;
        stateChanged();
    }

    public boolean isNetworkAvailable() {
        return networkType != NetworkType.NONE;
    }

    private void stateChanged() {
        NetworkChangedEvent event = new NetworkChangedEvent(networkType, isNetworkAvailable());
        EventBus.getDefault().postSticky(event);
    }
}
