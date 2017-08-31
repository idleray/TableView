package com.rayject.table.util;

public class ExcelUtils {
    public final static String TAG = "ExcelUtils";

    public static String columnToString(int colIndex) {
        StringBuffer buf = new StringBuffer();
        char c;

        if (colIndex < 26) {
            c = (char) ('A' + colIndex);
            buf.append(c);
        } else {
            int a = colIndex / 26;
            int b = colIndex % 26;
            c = (char) ('A' + (a - 1));
            buf.append(c);
            c = (char) ('A' + b);
            buf.append(c);
        }

        return buf.toString();
    }

    public static int bounds(int min, int value, int max) {
        int retValue = Math.max(min, value);
        retValue = Math.min(retValue, max);

        return retValue;
    }

    public static float bounds(float min, float value, float max) {
        float retValue = Math.max(min, value);
        retValue = Math.min(retValue, max);

        return retValue;
    }
}
