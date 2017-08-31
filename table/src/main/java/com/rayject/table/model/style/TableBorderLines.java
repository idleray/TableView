package com.rayject.table.model.style;

import com.rayject.table.util.Objects;

public class TableBorderLines {

    public BorderLineStyle leftBorderLine;
    public BorderLineStyle topBorderLine;
    public BorderLineStyle rightBorderLine;
    public BorderLineStyle bottomBorderLine;
//    public BorderLineStyle insideHBorderLine;
//    public BorderLineStyle insideVBorderLine;
//    public BorderLineStyle lt2rbBorderLine;
//    public BorderLineStyle lb2rtBorderLine;

    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        if (leftBorderLine != null) {
            buf.append("leftBorderLine:" + leftBorderLine.getType() + ",");
            buf.append(leftBorderLine.getColor() + ",");
            buf.append(leftBorderLine.getWidth() + ";");
        }
        if (topBorderLine != null) {
            buf.append("topBorderLine:" + topBorderLine.getType() + ",");
            buf.append(topBorderLine.getColor() + ",");
            buf.append(topBorderLine.getWidth() + ";");
        }
        if (rightBorderLine != null) {
            buf.append("rightBorderLine:" + rightBorderLine.getType() + ",");
            buf.append(rightBorderLine.getColor() + ",");
            buf.append(rightBorderLine.getWidth() + ";");
        }
        if (bottomBorderLine != null) {
            buf.append("bottomBorderLine:" + bottomBorderLine.getType() + ",");
            buf.append(bottomBorderLine.getColor() + ",");
            buf.append(bottomBorderLine.getWidth() + ";");
        }
//        if (insideHBorderLine != null) {
//            buf.append("insideHBorderLine:" + insideHBorderLine.getType() + ",");
//            buf.append(insideHBorderLine.getColor() + ",");
//            buf.append(insideHBorderLine.getWidth() + ";");
//        }
//        if (insideVBorderLine != null) {
//            buf.append("insideVBorderLine:" + insideVBorderLine.getType() + ",");
//            buf.append(insideVBorderLine.getColor() + ",");
//            buf.append(insideVBorderLine.getWidth() + ";");
//        }
//        if (lt2rbBorderLine != null) {
//            buf.append("lt2rbBorderLine:" + lt2rbBorderLine.getType() + ",");
//            buf.append(lt2rbBorderLine.getColor() + ",");
//            buf.append(lt2rbBorderLine.getWidth() + ";");
//        }
//        if (lb2rtBorderLine != null) {
//            buf.append("lb2rtBorderLine:" + lb2rtBorderLine.getType() + ",");
//            buf.append(lb2rtBorderLine.getColor() + ",");
//            buf.append(lb2rtBorderLine.getWidth() + ";");
//        }

        return buf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof TableBorderLines)) {
            return false;
        }

        if(this == o) {
            return true;
        }

        TableBorderLines obj = (TableBorderLines) o;
        if(Objects.equals(leftBorderLine, obj.leftBorderLine)
                && Objects.equals(rightBorderLine, obj.rightBorderLine)
                && Objects.equals(topBorderLine, obj.topBorderLine)
                && Objects.equals(bottomBorderLine, obj.bottomBorderLine)
                ) {
            return true;
        }

        return false;
    }
}
