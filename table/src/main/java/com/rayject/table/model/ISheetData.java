package com.rayject.table.model;

import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.Font;

public interface ISheetData {
    boolean isFreeze();

    int getFreezedRowCount();

    int getFreezedColCount();

    int getHorizontalSplitTopRow();

    int getVerticalSplitLeftColumn();

    int getFirstVisibleRow();

    int getFirstVisibleColumn();

    int getDefaultRowHeight();

    void setDefaultRowHeight(int rowHeight);

    int getRowHeight(int rowIndex);

    void setRowHeight(int rowIndex, int rowHeight);

    int getDefaultColumnWidth();

    void setDefaultColumnWidth(int columnWidth);

    int getColumnWidth(int colIndex);

    void setColumnWidth(int colIndex, int columnWidth);

    boolean isRowHidden(int rowIndex);

    boolean isColumnHidden(int colIndex);

    ICellData getCellData(int rowIndex, int colIndex);

    Range inMergedRange(int rowIndex, int colIndex, boolean includeFirstCell);

    int getMergedRangeCount();

    boolean addMergedRange(Range range);

    void removeMergedRangeAt(int index);

    boolean isBlankCell(int rowIndex, int colIndex);

    int getLastRow();

    int getLastColumn();

    int getLastColumn(int rowIndex);

    int getMaxRowCount();

    int getMaxColumnCount();

    int getMaxColumnCount(int rowIndex);

    void setSheetStyleIndex(int index);
    int getSheetStyleIndex();

    FontManager getFontManager();

    CellStyleManager getCellStyleManager();

    int getGridLineColor();

    void updateData();
    boolean isEmpty();
}
