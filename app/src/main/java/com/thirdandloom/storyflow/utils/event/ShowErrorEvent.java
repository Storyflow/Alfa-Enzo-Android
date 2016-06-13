package com.thirdandloom.storyflow.utils.event;

public class ShowErrorEvent {
    private String errorMessage;

    public ShowErrorEvent(String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}