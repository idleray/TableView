package com.rayject.table.model;

public class CellPosition extends Range {
    public CellPosition(int row, int col) {
        super(col, row, col, row);
    }

    public int getRowIndex() {
        return getTop();
    }

    public int getColumnIndex() {
        return getLeft();
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof CellPosition) {
            CellPosition cp = (CellPosition) o;
            if (getRowIndex() == cp.getRowIndex() &&
                    getColumnIndex() == cp.getColumnIndex()) {
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public int hashCode() {
        int hash = 0;

        //TODO: How to generate hashcode if row count > 65536?
        hash = getCellPosition(getRowIndex(), getColumnIndex());

        return hash;
    }

    private int getCellPosition(int rowIndex, int colIndex) {
        return (rowIndex << 16) + colIndex;
    }
}
