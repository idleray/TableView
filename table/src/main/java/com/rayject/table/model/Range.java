package com.rayject.table.model;

public class Range {

    private int top;
    private int left;
    private int bottom;
    private int right;

    public Range() {

    }

    public Range(Range range) {
        if (range != null) {
            set(range.getLeft(), range.getTop(), range.getRight(), range.getBottom());
        }
    }

    public Range(int left, int top, int right, int bottom) {
        set(left, top, right, bottom);
    }

    public void setRange(int left, int top, int right, int bottom) {
        set(left, top, right, bottom);
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getBottom() {
        return bottom;
    }

    public void setBottom(int bottom) {
        this.bottom = bottom;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    public int getWidth() {
        int width = right - left + 1;
        return width;
    }

    public int getHeight() {
        int height = bottom - top + 1;
        return height;
    }

    public void set(int left, int top, int right, int bottom) {
        if (top > bottom) {
            this.top = bottom;
            this.bottom = top;
        } else {
            this.top = top;
            this.bottom = bottom;
        }
        if (left > right) {
            this.left = right;
            this.right = left;
        } else {
            this.left = left;
            this.right = right;
        }
    }

    public boolean isInRange(int row, int col) {
        boolean ret = false;
        if (row >= top && row <= bottom) {
            if (col >= left && col <= right) {
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean equals(Object o) {
        boolean ret = false;
        if (o instanceof Range) {
            Range range = (Range) o;
            if (this.left == range.getLeft() &&
                    this.top == range.getTop() &&
                    this.right == range.getRight() &&
                    this.bottom == range.getBottom()) {
                ret = true;
            }
        }

        return ret;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(32);
        sb.append("Range(");
        sb.append(left + ", ");
        sb.append(top + ", ");
        sb.append(right + ", ");
        sb.append(bottom + ")");

        return sb.toString();
    }

}
