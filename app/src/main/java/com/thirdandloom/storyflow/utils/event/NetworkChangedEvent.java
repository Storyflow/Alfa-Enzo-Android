package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.utils.connectivity.NetworkType;

public class NetworkChangedEvent {
    private NetworkType networkType;
    private boolean available;

    public NetworkChangedEvent(NetworkType networkType, boolean available) {
        this.networkType = networkType;
        this.available = available;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public boolean available() {
        return available;
    }
}
