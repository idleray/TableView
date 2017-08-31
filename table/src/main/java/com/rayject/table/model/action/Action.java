package com.rayject.table.model.action;

import com.rayject.table.model.ICellData;

public interface Action {
    boolean onAction(ICellData cell);
}
