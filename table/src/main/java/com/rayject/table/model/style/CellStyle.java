package com.rayject.table.model.style;

import com.rayject.table.util.Objects;

public class CellStyle {
    private int bgColor;
    private int mVAlignment = TableConst.VERTICAL_ALIGNMENT_TOP;
    private int mAlignment = TableConst.ALIGNMENT_GENERAL;
    private int mIndention = 0;
    private boolean isAutoWrap = true;
    private int fontIndex;

    private TableBorderLines borderLines = new TableBorderLines();

    public int getAlignment() {

        return mAlignment;
    }

    public void setAlignment(int align) {
        mAlignment = align;
    }


    public int getVerticalAlignment() {
        return mVAlignment;
    }

    public void setVerticalAlignment(int value) {
        mVAlignment = value;
    }


    public BorderLineStyle getBorderLineStyle(int which) {
        BorderLineStyle borderLineStyle = null;
        switch (which) {
            case TableConst.LEFTBORDERLINE:
                borderLineStyle = borderLines.leftBorderLine;
                break;
            case TableConst.TOPBORDERLINE:
                borderLineStyle = borderLines.topBorderLine;
                break;
            case TableConst.RIGHTBORDERLINE:
                borderLineStyle = borderLines.rightBorderLine;
                break;
            case TableConst.BOTTOMBORDERLINE:
                borderLineStyle = borderLines.bottomBorderLine;
                break;
//            case TableConst.LT2RBBORDERLINE:
//                borderLineStyle = borderLines.lt2rbBorderLine;
//                break;
//            case TableConst.LB2RTBORDERLINE:
//                borderLineStyle = borderLines.lb2rtBorderLine;
//                break;
//            case TableConst.INSIDEHBORDERLINE:
//                borderLineStyle = borderLines.insideHBorderLine;
//                break;
//            case TableConst.INSIDEVBORDERLINE:
//                borderLineStyle = borderLines.insideVBorderLine;
//                break;
        }

        return borderLineStyle;
    }

    public void setBorderLineStyle(BorderLineStyle style, int which) {
        switch (which) {
            case TableConst.LEFTBORDERLINE:
                borderLines.leftBorderLine = style;
                break;
            case TableConst.TOPBORDERLINE:
                borderLines.topBorderLine = style;
                break;
            case TableConst.RIGHTBORDERLINE:
                borderLines.rightBorderLine = style;
                break;
            case TableConst.BOTTOMBORDERLINE:
                borderLines.bottomBorderLine = style;
                break;
//            case TableConst.LT2RBBORDERLINE:
//                borderLines.lt2rbBorderLine = style;
//                break;
//            case TableConst.LB2RTBORDERLINE:
//                borderLines.lb2rtBorderLine = style;
//                break;
//            case TableConst.INSIDEHBORDERLINE:
//                borderLines.insideHBorderLine = style;
//                break;
//            case TableConst.INSIDEVBORDERLINE:
//                borderLines.insideVBorderLine = style;
//                break;
        }
    }


    public int getBgColor() {
        return bgColor;
    }

    public void setBgColor(int color) {
        bgColor = color;
    }

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("bgColor:" + bgColor + ";");
        if (mVAlignment != -1) {
            buf.append("vAlignment:" + mVAlignment + ";");
        }

        buf.append(borderLines.toString());

        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof CellStyle)) {
            return false;
        }
        CellStyle cellStyle = (CellStyle) o;
        if(bgColor == cellStyle.bgColor
                && mVAlignment == cellStyle.mVAlignment
                && mAlignment == cellStyle.mAlignment
                && mIndention == cellStyle.mIndention
                && isAutoWrap == cellStyle.isAutoWrap
                && fontIndex == cellStyle.fontIndex
                && Objects.equals(borderLines, cellStyle.borderLines)
                ) {
            return true;
        }

        return false;
    }

    public CellStyle cloneObject() {
        CellStyle clone = new CellStyle();
        clone.bgColor = this.bgColor;
        clone.mVAlignment = this.mVAlignment;
        clone.mIndention = mIndention;
        clone.isAutoWrap = isAutoWrap;
        clone.fontIndex = fontIndex;

        if (borderLines.leftBorderLine != null) {
            clone.borderLines.leftBorderLine = borderLines.leftBorderLine.cloneObject();
        }
        if (borderLines.topBorderLine != null) {
            clone.borderLines.topBorderLine = borderLines.topBorderLine.cloneObject();
        }
        if (borderLines.rightBorderLine != null) {
            clone.borderLines.rightBorderLine = borderLines.rightBorderLine.cloneObject();
        }
        if (borderLines.bottomBorderLine != null) {
            clone.borderLines.bottomBorderLine = borderLines.bottomBorderLine.cloneObject();
        }
//        if (borderLines.lt2rbBorderLine != null) {
//            clone.borderLines.lt2rbBorderLine = borderLines.lt2rbBorderLine.cloneObject();
//        }
//        if (borderLines.lb2rtBorderLine != null) {
//            clone.borderLines.lb2rtBorderLine = borderLines.lb2rtBorderLine.cloneObject();
//        }
//        if (borderLines.insideHBorderLine != null) {
//            clone.borderLines.insideHBorderLine = borderLines.insideHBorderLine.cloneObject();
//        }
//        if (borderLines.insideVBorderLine != null) {
//            clone.borderLines.insideVBorderLine = borderLines.insideVBorderLine.cloneObject();
//        }

        return clone;
    }

    public int getIndention() {
        return mIndention;
    }

    public void setIndention(int indention) {
        mIndention = indention;
    }

    public boolean isAutoWrap() {
        return isAutoWrap;
    }

    public void setAutoWrap(boolean autoWrap) {
        isAutoWrap = autoWrap;
    }

    public int getFontIndex() {
        return fontIndex;
    }

    public void setFontIndex(int fontIndex) {
        this.fontIndex = fontIndex;
    }
}
