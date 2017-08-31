package com.rayject.table.util;

public class ConstVar {

    public final static int MAXROWCOUNT = 32768;
    public final static int MAXCOLUMNCOUNT = 256;

    public final static int HEADERWIDTH = 80;
    public final static int HEADERHEIGHT = 60;

    public final static int DRAWCELL_BG = 0x1;
    public final static int DRAWCELL_BORDER = 0x2;
    public final static int DRAWCELL_TEXT = 0x4;
    public final static int DRAWCELL_OBJECT = 0x8;
    public final static int DRAWCELL_ALL = DRAWCELL_BG | DRAWCELL_BORDER | DRAWCELL_TEXT | DRAWCELL_OBJECT;

    public final static int ZOOMINMAX = 150;
    public final static int ZOOMOUTMIN = 10;
    public final static int ZOOMSTEP = 15;
    public final static int FITWIDTH = -1;
    public final static int FITHEIGHT = -2;

    public final static int DEFAULTTEXTSIZE = 30;

    public final static int HIT_NONE = 0;
    public final static int HIT_RCHEADER = 1;
    public final static int HIT_ROWHEADER = 2;
    public final static int HIT_COLUMNHEADER = 3;
    public final static int HIT_ROWHEADER_RESIZE = 4;
    public final static int HIT_COLUMNHEADER_RESIZE = 5;
    public final static int HIT_TABLE = 6;
    public final static int HIT_SELECTION = 7;

//	public final static int SELECT_NONE = 0;
//	public final static int SELECT_LEFT2RIGHT = 0x1;
//	public final static int SELECT_RIGHT2LEFT = 0x2;
//	public final static int SELECT_TOP2BOTTOM = 0x4;
//	public final static int SELECT_BOTTOM2TOP = 0x8;

    public final static int RESIZE_AREA = 30;
}
