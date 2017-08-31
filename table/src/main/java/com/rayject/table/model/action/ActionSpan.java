package com.rayject.table.model.action;


import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.rayject.table.model.ICellData;

public class ActionSpan extends ClickableSpan{
    private Action action;
    private ICellData cell;

    public ActionSpan(ICellData cell, Action action) {
        this.action = action;
        this.cell = cell;
    }
    @Override
    public void onClick(View widget) {
        if(action != null) {
            action.onAction(cell);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(false);
    }
}
