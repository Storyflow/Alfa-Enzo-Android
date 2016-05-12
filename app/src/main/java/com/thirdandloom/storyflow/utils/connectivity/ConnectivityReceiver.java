package com.thirdandloom.storyflow.utils.connectivity;

import com.thirdandloom.storyflow.StoryflowApplication;
import com.thirdandloom.storyflow.utils.BaseUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = BaseUtils.connectivityManager();
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        NetworkType networkType = ConnectivityUtils.getNetworkType(activeNetwork);
        ConnectivityObserver connectivityObserver = StoryflowApplication.connectivityObserver();
        connectivityObserver.onNetworkTypeChanged(networkType);
    }
}
