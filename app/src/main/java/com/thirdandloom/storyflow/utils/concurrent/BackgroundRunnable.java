package com.thirdandloom.storyflow.utils.concurrent;


public class BackgroundRunnable extends BaseRunnable {

    public BackgroundRunnable() {
        super(ThreadUtils.Priority.Background);
    }
}
