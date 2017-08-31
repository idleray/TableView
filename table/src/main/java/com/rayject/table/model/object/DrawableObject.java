package com.rayject.table.model.object;


import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

import com.rayject.table.model.ICellData;

public class DrawableObject extends CellObject {
    private Drawable drawable;

    public DrawableObject(ICellData cell, Drawable bd) {
        super(cell);
        drawable = bd;
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
    }
    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    @Override
    public void onDraw(Canvas canvas) {
        drawable.draw(canvas);
    }

    @Override
    protected void onDrawableStateChanged(int[] state) {
        if (drawable != null && drawable.isStateful()) {
            drawable.setState(state);
        }
    }
}
