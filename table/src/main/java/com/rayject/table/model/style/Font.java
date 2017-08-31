package com.rayject.table.model.style;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;

import com.rayject.table.util.TextHelper;

public class Font {
    public final static int FONTSIZE_INVALIDE = -1;
    public final static int SS_NONE = 0;
    public final static int SS_SUPER = 1;
    public final static int SS_SUB = 2;

    private String fontName;
    private int fontSize = FONTSIZE_INVALIDE;
    private int color;
    private int style; //Typeface.NORMAL, Typeface.BOLD ...
    private boolean underLine;
    private boolean strikeLine;
    private int typeOffset;

    public static Font createDefault(Context context) {
        Font font = new Font();
        font.setStyle(Typeface.NORMAL);
        font.setTypeOffset(SS_NONE);

        TextHelper textHelper = new TextHelper(context);
        font.setColor(textHelper.getTextColor());
        font.setFontSize(textHelper.getTextSize());

        return font;
    }

    public String getFontName() {
        return fontName;
    }

    public void setFontName(String fontName) {
        this.fontName = fontName;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getStyle() {
        return style;
    }

    public void setStyle(int style) {
        this.style = style;
    }

    public boolean isUnderLine() {
        return underLine;
    }

    public void setUnderLine(boolean underLine) {
        this.underLine = underLine;
    }

    public boolean isStrikeLine() {
        return strikeLine;
    }

    public void setStrikeLine(boolean strikeLine) {
        this.strikeLine = strikeLine;
    }

    public int getTypeOffset() {
        return typeOffset;
    }

    public void setTypeOffset(int typeOffset) {
        this.typeOffset = typeOffset;
    }

    public Font cloneObject() {
        Font clone = new Font();
        if(fontName != null) {
            clone.fontName = fontName;
        }
        clone.fontSize = fontSize;
        clone.color = color;
        clone.style = style;
        clone.underLine = underLine;
        clone.strikeLine = strikeLine;
        clone.typeOffset = typeOffset;

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Font)) {
            return false;
        }
        Font font = (Font)o;
        if(TextUtils.equals(fontName, font.fontName)
                && fontSize == font.fontSize
                && color == font.color
                && style == font.style
                && underLine == font.underLine
                && strikeLine == font.strikeLine
                && typeOffset == font.typeOffset) {
            return true;
        }

        return false;
    }
}
