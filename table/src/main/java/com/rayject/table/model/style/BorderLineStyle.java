package com.rayject.table.model.style;

/**
 * The border line style
 */
public class BorderLineStyle {
    public final static int BORDER_THINMWIDTH = 2;
    public final static int BORDER_MEDIUMWIDTH = 4;
    public final static int BORDER_THICKWIDTH = 6;

    public final static int BORDER_NONE = 0;
    public final static int BORDER_SOLID = 1;
    public final static int BORDER_DOT = 2;
    public final static int BORDER_DASH = 3;
    public final static int BORDER_HAIRLINE = 4;
    public final static int BORDER_DASH_DOT_DOT = 5;
    public final static int BORDER_DASH_DOT = 6;
    public final static int BORDER_DOUBLE = 7;
    public final static int BORDER_SQUAREDOT = 8;
    public final static int BORDER_CIRCLEDOT = 9;
    public final static int BORDER_LONGDASH = 10;
    public final static int BORDER_LONGDASH_DOT = 11;
    public final static int BORDER_LONGDASH_DOT_DOT = 12;

    private int type = BorderLineStyle.BORDER_SOLID;
    private int color = 0xFF000000;
    private int width = BorderLineStyle.BORDER_THINMWIDTH;

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }

    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }

    /**
     * @param width the width to set
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    public BorderLineStyle cloneObject() {
        BorderLineStyle clone = new BorderLineStyle();
        clone.type = this.type;
        clone.color = this.color;
        clone.width = this.width;

        return clone;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof BorderLineStyle)) {
            return false;
        }
        if(this == o) {
            return true;
        }
        BorderLineStyle obj = (BorderLineStyle) o;
        if(type == obj.type
                && color == obj.color
                && width == obj.width) {
            return true;
        }

        return false;
    }
}
