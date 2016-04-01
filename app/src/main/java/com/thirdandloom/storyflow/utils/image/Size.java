package com.thirdandloom.storyflow.utils.image;

import java.io.Serializable;

public class Size implements Serializable {
    private static final long serialVersionUID = -7520925276406368555L;

    private final int width;
    private final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }
}