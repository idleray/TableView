package com.rayject.table.util;


import android.content.Context;
import android.widget.TextView;

public class TextHelper {
    private TextView textView;

    public TextHelper(Context context) {
        textView = new TextView(context);
    }

    public int getTextSize() {
        return (int) textView.getTextSize();
    }

    public int getTextColor() {
        return textView.getCurrentTextColor();
    }
}
