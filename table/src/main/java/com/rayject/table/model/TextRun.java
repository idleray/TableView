package com.rayject.table.model;


import com.rayject.table.model.action.Action;

public class TextRun implements ITextRun{
    private int index;
    private int length;
    private int fontIndex = -1;
    private int backgroundColor;
    private Action action;

    @Override
    public int getStartPos() {
        return index;
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public int getFontIndex() {
        return fontIndex;
    }

    @Override
    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setStartPos(int pos) {
        index = pos;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public void setFontIndex(int fontIndex) {
        this.fontIndex = fontIndex;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    @Override
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }
}
