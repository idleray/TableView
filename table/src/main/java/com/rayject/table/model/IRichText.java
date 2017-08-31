package com.rayject.table.model;


public interface IRichText {
    CharSequence getText();
    int getRunCount();
    ITextRun getRun(int index);
}
