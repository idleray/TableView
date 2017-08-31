package com.rayject.table.util;

import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;

import com.rayject.table.model.style.BorderLineStyle;

public class BorderLineUtil {
    public static void setBorderlinePaint(BorderLineStyle borderlineStyle, Paint paint) {
        float DASHLENGTH = 9f;
        float DOTLENGTH = 3f;
        float SPACELENGTH = 3f;
//		if(uc != null) {
//			DASHLENGTH = uc.getZoomedValue(DASHLENGTH);
//			DOTLENGTH = uc.getZoomedValue(DOTLENGTH);
//			SPACELENGTH = uc.getZoomedValue(SPACELENGTH);
//		}
        PathEffect pe = null;

        int type = borderlineStyle.getType();
        int color = borderlineStyle.getColor();
        float lineWidth = borderlineStyle.getWidth();

        switch (type) {
            case BorderLineStyle.BORDER_DASH:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DOT:
                pe = new DashPathEffect(new float[]{DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DASH_DOT:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DASH_DOT_DOT:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_HAIRLINE:
                lineWidth = 0;
                break;
            default:
                break;
        }

//		if(uc != null) {
//			lineWidth = uc.getZoomedValue(lineWidth);
//		}
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(color);
        paint.setPathEffect(pe);
        paint.setDither(true);
        paint.setAntiAlias(true);
    }
}
