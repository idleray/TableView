package com.rayject.table.model;


import com.rayject.table.model.style.Font;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FontManager {
//    private static FontManager instance;

    private List<Font> fonts;

//    public static FontManager getInstance() {
//        if(instance == null) {
//            instance = new FontManager();
//        }
//        return instance;
//    }

    public FontManager() {
        fonts = new ArrayList<>();
    }

    public int addFont(Font font) {
        int index = -1;
        for(int i = 0; i < fonts.size(); i++) {
            Font f = fonts.get(i);
            if(f.equals(font)) {
                index = i;
                break;
            }
        }
        if(index != -1) {
            return index;
        } else {
            fonts.add(font);
            return fonts.size()-1;
        }
    }

    public Font getFont(int index) {
        Font font = null;
        try {
            font = fonts.get(index);
        } catch (IndexOutOfBoundsException e) {

        }

        return font;
    }
}
