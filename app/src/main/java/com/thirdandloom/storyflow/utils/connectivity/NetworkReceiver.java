package com.thirdandloom.storyflow.utils.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkReceiver extends BroadcastReceiver {

    public enum NetworkType {
        NONE, WIFI, MOBILE, ROAMING, UNKNOWN,
    }

    private NetworkType networkType;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connMgr =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        networkType = getNetworkType(networkInfo);
    }

    public boolean isNetworkAvailable() {
        return networkType != NetworkType.NONE;
    }

    public static NetworkType getNetworkType(NetworkInfo networkInfo) {
        if ((networkInfo == null) || !networkInfo.isConnectedOrConnecting()) {
            return NetworkType.NONE;
        }
        if (networkInfo.isRoaming()) {
            return NetworkType.ROAMING;
        }
        switch (networkInfo.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return NetworkType.WIFI;
            case ConnectivityManager.TYPE_MOBILE:
                return NetworkType.MOBILE;
            default:
                return NetworkType.UNKNOWN;
        }
    }
}
