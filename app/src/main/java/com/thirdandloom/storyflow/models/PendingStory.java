package com.thirdandloom.storyflow.models;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class PendingStory extends BaseModel {
    private static final long serialVersionUID = 856012311368902178L;

    public enum Status {
        WaitingForSend, ImageUploading, CreatingStory, CreateSucceed, CreateFailed, CreateImpossible
    }

    private Status status = Status.WaitingForSend;
    private String description;
    private String imageUrl;

    public Status getStatus() {
        return status;
    }

    public void setData(@NonNull String description, @Nullable String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
