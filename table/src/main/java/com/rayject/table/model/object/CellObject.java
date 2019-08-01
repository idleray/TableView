package com.rayject.table.model.object;


import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.rayject.table.model.ICellData;
import com.rayject.table.model.style.TableConst;
import com.rayject.table.util.DrawableStateWrapper;

public abstract class CellObject {
    private ICellData cell;
    private int alignment;
    private int verticalAlignment;
    private int leftPadding, topPadding, rightPadding, BottomPadding;
    private Drawable background;
    private boolean selected;

    private OnClickListener onClickListener;

    public interface OnClickListener {
        boolean onClick(CellObject cellObject);
    }

    public CellObject(ICellData cell) {
        this.cell = cell;
    }

    public ICellData getCell() {
        return cell;
    }

    public void setCell(ICellData cell) {
        this.cell = cell;
    }

    public int getAlignment() {
        return alignment;
    }

    public void setAlignment(int alignment) {
        this.alignment = alignment;
    }

    public int getVerticalAlignment() {
        return verticalAlignment;
    }

    public void setVerticalAlignment(int verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    public int getLeftPadding() {
        return leftPadding;
    }

    public void setLeftPadding(int leftPadding) {
        this.leftPadding = leftPadding;
    }

    public int getTopPadding() {
        return topPadding;
    }

    public void setTopPadding(int topPadding) {
        this.topPadding = topPadding;
    }

    public int getRightPadding() {
        return rightPadding;
    }

    public void setRightPadding(int rightPadding) {
        this.rightPadding = rightPadding;
    }

    public int getBottomPadding() {
        return BottomPadding;
    }

    public void setBottomPadding(int bottomPadding) {
        BottomPadding = bottomPadding;
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public Drawable getBackground() {
        return background;
    }

    public void setBackground(Drawable background) {
        this.background = background;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
        onStateChanged();
    }

    protected void onStateChanged() {
        int[] state = getDrawableState();
        if(background != null && background.isStateful()) {
            background.setState(state);
        }

        onDrawableStateChanged(state);
    }

    public final int[] getDrawableState() {
        int flags = 0;
        if(selected) {
            flags |= DrawableStateWrapper.VIEW_STATE_SELECTED;
        }
        return DrawableStateWrapper.getDrawableState(flags);
    }

    public boolean onClick() {
        boolean handled = false;
        if(onClickListener != null) {
             handled = onClickListener.onClick(this);
        }

        return handled;
    }

    public abstract int getWidth();
    public abstract int getHeight();
    public abstract void onDraw(Canvas canvas);
    protected abstract void onDrawableStateChanged(int[] state);

    public void draw(Canvas canvas) {
        drawBackground(canvas);

        onDraw(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if(background == null) {
            return;
        }
        setBackgroundBounds();

        background.draw(canvas);
    }

    private void setBackgroundBounds() {
        if(background != null) {
            background.setBounds(0, 0, getWidth(), getHeight());
        }
    }

    public static Point getPositionInRect(CellObject dObject, int rectWidth, int rectHeight) {
        int alignment = dObject.getAlignment();
        int left = dObject.getLeftPadding();
        switch(alignment) {
            case TableConst.ALIGNMENT_CENTER:
                left = (rectWidth - dObject.getWidth()) / 2 + dObject.getLeftPadding();
                break;
            case TableConst.ALIGNMENT_RIGHT:
                left = rectWidth - dObject.getWidth() - dObject.getRightPadding();
                break;
        }

        int vAlignment = dObject.getVerticalAlignment();
        int top = dObject.getTopPadding();
        switch (vAlignment) {
            case TableConst.VERTICAL_ALIGNMENT_CENTRE:
                top = (rectHeight - dObject.getHeight()) / 2 + dObject.getTopPadding();
                break;
            case TableConst.VERTICAL_ALIGNMENT_BOTTOM:
                top = rectHeight - dObject.getHeight() - dObject.getBottomPadding();
                break;
        }

        Point point = new Point(left, top);

        return point;
    }
}
