package com.thirdandloom.storyflow.models;

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
}
