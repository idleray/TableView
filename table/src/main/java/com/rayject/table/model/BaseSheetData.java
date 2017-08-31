package com.rayject.table.model;


public abstract class BaseSheetData implements ISheetData{
    @Override
    public int getHorizontalSplitTopRow() {
        return getFreezedRowCount();
    }

    @Override
    public int getVerticalSplitLeftColumn() {
        return getFreezedColCount();
    }

    @Override
    public int getFirstVisibleRow() {
        return getFreezedRowCount();
    }

    @Override
    public int getFirstVisibleColumn() {
        return getFreezedColCount();
    }

    @Override
    public void updateData() {
        ICellData cell;
        int rowCount = getMaxRowCount();
        int colCount = getMaxColumnCount();
        for(int i = 0; i < rowCount; i++) {
            for(int j = 0; j < colCount; j++) {
                cell = getCellData(i, j);
                if(cell != null) {
                    cell.update();
                }
            }
        }

    }
}
