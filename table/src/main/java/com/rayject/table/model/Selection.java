package com.rayject.table.model;

public class Selection {
    private int mArea;
    private ISelection owner;
    private int mStart;
    private int mEnd;
    private int mStartPage;
//	private int mEndPage;

    //Only used in table
    private Range range;
    private int startInCell;
    private int endInCell;

    private static Selection selection;

    public static Selection getSelection() {
        if (selection == null) {
            selection = new Selection();
        }

        return selection;
    }

    public static void clearSelection() {
        selection = null;
    }

    private Selection() {
//		setmStart(-1);
        setEnd(-1);
    }

    public void setArea(int area) {
        mArea = area;
    }

    public int getArea() {
        return mArea;
    }

    public void setOwner(ISelection data) {
        if (owner != null) {
            owner.setSelection(null);
        }

        owner = data;
        if (owner != null) {
            owner.setSelection(this);
        }
    }

    public ISelection getOwner() {
        return owner;
    }

    public void setStartOffset(int mStart) {
        this.mStart = mStart;
    }

    public int getStartOffset() {
        return mStart;
    }

    public void setEnd(int mEnd) {
        this.mEnd = mEnd;
    }

    public int getEnd() {
        return mEnd;
    }

    public void setStartPage(int mStartPage) {
        this.mStartPage = mStartPage;
    }

    public int getStartPage() {
        return mStartPage;
    }
//
//	public void setEndPage(int mEndPage) {
//		this.mEndPage = mEndPage;
//	}
//
//	public int getEndPage() {
//		return mEndPage;
//	}

    public void setRange(Range range) {
        this.range = range;
    }

    public Range getRange() {
        return range;
    }

    public void setStartInCell(int startInCell) {
        this.startInCell = startInCell;
    }

    public int getStartInCell() {
        return startInCell;
    }

    public void setEndInCell(int endInCell) {
        this.endInCell = endInCell;
    }

    public int getEndInCell() {
        return endInCell;
    }

    public void reset() {
        if (owner != null) {
            owner.setSelection(null);
        }
        mArea = 0;
        mStart = -1;
        mEnd = -1;
        mStartPage = -1;
//		mEndPage = -1;
        range = null;
        startInCell = -1;
        endInCell = -1;
    }

    String separatorStr = ",";

    public String serialize() {
        String temps = "";
        if (selection != null) {
            if (range != null) {
                temps = "" + mArea + separatorStr + mStart + separatorStr + mEnd + separatorStr + mStartPage + separatorStr
                        + range.getLeft() + separatorStr + range.getTop() + separatorStr + range.getRight() + separatorStr + range.getBottom()
                        + separatorStr + startInCell + separatorStr + endInCell;
            } else {
                temps = "" + mArea + separatorStr + mStart + separatorStr + mEnd + separatorStr + mStartPage + separatorStr
                        + "-1" + separatorStr + "-1" + separatorStr + "-1" + separatorStr + "-1"
                        + separatorStr + startInCell + separatorStr + endInCell;
            }
        }
        return temps;
    }

    public Selection deserialize(String temps) {
        if (temps == null || temps.length() < 1) return null;
        String[] datas = temps.split(separatorStr);
        if (datas.length == 10) {
            Selection s = Selection.getSelection();
            s.reset();
            s.mArea = Integer.parseInt(datas[0]);
            s.mStart = Integer.parseInt(datas[1]);
            s.mEnd = Integer.parseInt(datas[2]);
            s.mStartPage = Integer.parseInt(datas[3]);
            int rLeft = Integer.parseInt(datas[4]);
            if (rLeft != -1) {
                int rTop = Integer.parseInt(datas[5]);
                int rRight = Integer.parseInt(datas[6]);
                int rBottom = Integer.parseInt(datas[7]);
                s.range = new Range(rLeft, rTop, rRight, rBottom);
            }
            s.startInCell = Integer.parseInt(datas[8]);
            s.endInCell = Integer.parseInt(datas[9]);
        }

        return null;
    }
}
