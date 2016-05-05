package com.thirdandloom.storyflow.utils;

import android.text.Layout;
import android.text.Selection;
import android.widget.EditText;

public class EditTextUtils extends BaseUtils {
    public static int getCurrentCursorLine(EditText editText) {
        int selectionStart = Selection.getSelectionStart(editText.getText());
        Layout layout = editText.getLayout();

        if (selectionStart != -1) {
            return layout.getLineForOffset(selectionStart);
        }
        return -1;
    }

}
