package com.thirdandloom.storyflow.utils;

public abstract class CancelableRunnable implements Runnable {
    private boolean mCanceled;

    @Override
    public void run() {
        if (!mCanceled) {
            execute();
        }
    }

    public void cancel() {
        mCanceled = true;
    }

    public boolean isCancelled() {
        return mCanceled;
    }

    public abstract void execute();
}
