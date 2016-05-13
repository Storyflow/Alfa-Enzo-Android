package com.thirdandloom.storyflow.utils.concurrent;

import android.os.Process;

public class BaseRunnable implements Runnable {
    public ThreadUtils.Priority priority = ThreadUtils.Priority.Default;

    public BaseRunnable(ThreadUtils.Priority priority) {
        this.priority = priority;
    }

    @Override
    public void run() {
        switch (priority) {
            case Default:
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
                break;
            case Lowest:
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_LOWEST);
                break;
            case Background:
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
                break;
            case Foreground:
                android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_FOREGROUND);
                break;
        }
    }
}
