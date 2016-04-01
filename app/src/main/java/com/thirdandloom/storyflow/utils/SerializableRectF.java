package com.thirdandloom.storyflow.utils;

import android.graphics.RectF;

import java.io.IOException;
import java.io.Serializable;

public class SerializableRectF implements Serializable {
    private static final long serialVersionUID = 1L;

    private RectF rectF;

    public SerializableRectF(RectF rectF) {
        this.rectF = rectF;
    }

    public RectF getRectF() {
        return rectF;
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        float left = rectF.left;
        float top = rectF.top;
        float right = rectF.right;
        float bottom = rectF.bottom;

        out.writeFloat(left);
        out.writeFloat(top);
        out.writeFloat(right);
        out.writeFloat(bottom);
    }

    private void readObject(java.io.ObjectInputStream in) throws IOException,
            ClassNotFoundException {
        float left = in.readFloat();
        float top = in.readFloat();
        float right = in.readFloat();
        float bottom = in.readFloat();

        this.rectF = new RectF(left, top, right, bottom);
    }
}
