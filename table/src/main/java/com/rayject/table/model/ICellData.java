package com.rayject.table.model;


import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;

import com.rayject.table.model.action.Action;
import com.rayject.table.model.object.CellObject;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.util.UnitsConverter;

public interface ICellData extends ISelection {
    void setCellValue(IRichText value);

    CharSequence getTextValue();
    IRichText getRichTextValue();

    boolean isMerged();

    Range getMergedRange();

    void setMergedRange(Range range);

    void draw(Canvas canvas, Paint paint, Rect rect, int drawType, UnitsConverter uc);

//    public boolean getWrapText();

    void update();

    void setStyleIndex(int index);

    int getStyleIndex();

    CellStyle getCellStyle();

    Layout getLayout();

    void clearLayout();

    int getPositionXInCell(int charOffset);

    ISheetData getSheet();

    void addObject(CellObject d);

    void removeObject(CellObject d);
    int getObjectCount();
    CellObject getObject(int index);
    void setAction(Action action);
    Action getAction();
    int calcTextHeightByWidth(int width);
}
