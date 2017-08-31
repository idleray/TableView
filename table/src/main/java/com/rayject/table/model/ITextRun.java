package com.rayject.table.model;


import com.rayject.table.model.action.Action;

public interface ITextRun {
    int getStartPos();
    int getLength();
    int getFontIndex();
    int getBackgroundColor();
    Action getAction();
}
