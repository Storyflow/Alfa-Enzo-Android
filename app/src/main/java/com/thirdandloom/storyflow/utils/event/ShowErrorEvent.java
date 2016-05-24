package com.thirdandloom.storyflow.utils.event;

import com.thirdandloom.storyflow.rest.ErrorHandler;

public class ShowErrorEvent {
    private String errorMessage;

    public ShowErrorEvent(String message) {
        this.errorMessage = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}