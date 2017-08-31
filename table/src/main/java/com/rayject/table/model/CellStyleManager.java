package com.rayject.table.model;


import com.rayject.table.model.style.CellStyle;

import java.util.ArrayList;
import java.util.List;

public class CellStyleManager {
//    private static CellStyleManager instance;
    private List<CellStyle> cellStyles;

//    public static CellStyleManager getInstance() {
//        if(instance == null) {
//            instance = new CellStyleManager();
//        }
//
//        return instance;
//    }

    public CellStyleManager() {
        cellStyles = new ArrayList<>();
    }

    public int addCellStyle(CellStyle style) {
        int index = -1;
        for(int i = 0; i < cellStyles.size(); i++) {
            CellStyle f = cellStyles.get(i);
            if(f.equals(style)) {
                index = i;
                break;
            }
        }
        if(index != -1) {
            return index;
        } else {
            cellStyles.add(style);
            return cellStyles.size()-1;
        }
    }

    public CellStyle getCellStyle(int index) {
        try {
            return cellStyles.get(index);
        } catch (IndexOutOfBoundsException e) {

        }

        return null;
    }
}
