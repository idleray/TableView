package com.rayject.table.util;

import android.util.Log;

public class Utils {
    public final static String TAG = "Utils";
    private final static boolean DEBUG = false;

    public static final boolean EVALUATION = false;
    public static final boolean WORD_USECANVASSCALE = true;
    public static final boolean PPT_USECANVASSCALE = true;
    public static final boolean WORD_USECACHEIMAGE = true;

    public static int getCellPosition(int rowIndex, int colIndex) {
        return (rowIndex << 16) + colIndex;
    }

    public static int findWordStart(CharSequence text, int offset) {
        int start = offset;
        if (offset >= text.length()) {
            return offset;
        }

        int cType = Character.getType(text.charAt(offset));
        for (; start > 0; start--) {
            char c = text.charAt(start - 1);
            int type = Character.getType(c);

//			if (c != '\'' && type != Character.UPPERCASE_LETTER
//					&& type != Character.LOWERCASE_LETTER
//					&& type != Character.TITLECASE_LETTER
//					&& type != Character.MODIFIER_LETTER
//					&& type != Character.DECIMAL_DIGIT_NUMBER) {
//				break;
//			}
            if (c == '\n') {
                break;
            }

            if (cType != type) {
                if (!isLetter(cType) || !isLetter(type)) {
                    break;
                }
            }
        }

        if (DEBUG) {
            Log.i(TAG, "findWordStart type=" + Character.getType(text.charAt(offset)));
        }
        return start;
    }

    public static int findWordEnd(CharSequence text, int offset) {
        int len = text.length();
        int end = offset;
        if (offset >= len) {
            return offset;
        }

        int cType = Character.getType(text.charAt(offset));

        for (; end < len; end++) {
            char c = text.charAt(end);
            int type = Character.getType(c);

//			if (c != '\'' && type != Character.UPPERCASE_LETTER
//					&& type != Character.LOWERCASE_LETTER
//					&& type != Character.TITLECASE_LETTER
//					&& type != Character.MODIFIER_LETTER
//					&& type != Character.DECIMAL_DIGIT_NUMBER) {
//				break;
//			}
            if (c == '\n') {
                break;
            }

            if (cType != type) {
                if (!isLetter(cType) || !isLetter(type)) {
                    break;
                }
            }
        }

        if (DEBUG) {
            Log.i(TAG, "findWordEnd type=" + Character.getType(text.charAt(offset)));
        }
        return end;
    }

//	public static void findWord(CharSequence text, int offset) {
//		int start = findWordStart(text, offset);
//		int end = findWordEnd(text, offset);
//	}

    private static boolean isLetter(int charType) {
        boolean ret = true;
        if (charType != Character.UPPERCASE_LETTER
                && charType != Character.LOWERCASE_LETTER
                && charType != Character.TITLECASE_LETTER
                && charType != Character.MODIFIER_LETTER
                && charType != Character.DECIMAL_DIGIT_NUMBER) {
            ret = false;
        }

        return ret;
    }
}
