package com.thirdandloom.storyflow.views.alert;

import com.thirdandloom.storyflow.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QuickAlertView extends FrameLayout {

    public enum Type {
        WARNING,
        ERROR
    }

    private static final Map<Type, Integer> backgroundColors;
    static {
        Map<Type, Integer> map = new HashMap<>();
        map.put(Type.WARNING, R.color.blue);
        map.put(Type.ERROR, R.color.red);
        backgroundColors = Collections.unmodifiableMap(map);
    }

    private TextView mMessageTextView;

    public QuickAlertView(Context context) {
        this(context, null);
    }

    public QuickAlertView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickAlertView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    public QuickAlertView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        initGui();
    }

    private void init() {
        inflate(getContext(), R.layout.quick_alert_view, this);
        mMessageTextView = (TextView) findViewById(R.id.alert_text);
    }

    private void initGui() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        int spacingSmall = getResources().getDimensionPixelSize(R.dimen.sizeTiny);
        int spacingTiny = getResources().getDimensionPixelSize(R.dimen.sizeSmall);
        setPadding(spacingSmall, spacingTiny, spacingSmall, spacingTiny);
    }

    public boolean isOnScreen() {
        return getParent() != null;
    }

    public void setText(String resourceId, Type messageType) {
        setBackgroundColor(getColor(messageType));
        mMessageTextView.setText(resourceId);
    }

    @Override
    public boolean hasOverlappingRendering() {
        return false;
    }

    private int getColor(Type type) {
        return getResources().getColor(backgroundColors.get(type));
    }
}
