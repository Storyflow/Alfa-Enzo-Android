package com.thirdandloom.storyflow.utils.connectivity;

import com.thirdandloom.storyflow.utils.BaseUtils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectivityUtils extends BaseUtils {

    public static NetworkType getNetworkType(NetworkInfo activeNetwork) {
        if ((activeNetwork == null) || !activeNetwork.isConnectedOrConnecting()) {
            return NetworkType.NONE;
        }

        if (activeNetwork.isRoaming()) {
            return NetworkType.ROAMING;
        }

        switch (activeNetwork.getType()) {
            case ConnectivityManager.TYPE_WIFI:
                return NetworkType.WIFI;

            case ConnectivityManager.TYPE_MOBILE:
                return NetworkType.MOBILE;

            default:
                return NetworkType.UNKNOWN;
        }
    }
}
