package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.utils.connectivity.NetworkReceiver;

public class NetworkChangedEvent {
    private NetworkReceiver.NetworkType networkType;
    private boolean available;

    public NetworkChangedEvent(NetworkReceiver.NetworkType networkType, boolean available) {
        this.networkType = networkType;
        this.available = available;
    }

    public NetworkReceiver.NetworkType getNetworkType() {
        return networkType;
    }

    public boolean available() {
        return available;
    }
}
