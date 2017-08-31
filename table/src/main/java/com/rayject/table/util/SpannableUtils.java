package com.rayject.table.util;


import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com.rayject.table.model.ICellData;
import com.rayject.table.model.action.Action;
import com.rayject.table.model.action.ActionSpan;
import com.rayject.table.model.style.Font;

public class SpannableUtils {

    public static void convertFont(Font runFont, int runStartPos,
                                    int runEndPos, SpannableStringBuilder spannableText) {
        if(runFont == null) {
            return;
        }

        String fontName = runFont.getFontName();
        if(!TextUtils.isEmpty(fontName)) {
            TypefaceSpan typefaceSpan = new TypefaceSpan(fontName);
            spannableText.setSpan(typefaceSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int style = runFont.getStyle();
        if(style != Typeface.NORMAL) {
            StyleSpan styleSpan = new StyleSpan(style);
            spannableText.setSpan(styleSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int size = runFont.getFontSize();
        if(size != Font.FONTSIZE_INVALIDE) {
            AbsoluteSizeSpan absSizeSpan = new AbsoluteSizeSpan(size);
            spannableText.setSpan(absSizeSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int color = runFont.getColor();
        //color = 0xff000000;
        if(color != 0) {
            ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(color);
            spannableText.setSpan(foregroundColorSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if(runFont.isUnderLine()){
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableText.setSpan(underlineSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if(runFont.isStrikeLine()) {
            StrikethroughSpan strikethroughSpan = new StrikethroughSpan();
            spannableText.setSpan(strikethroughSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if (runFont.getTypeOffset() == Font.SS_SUPER) {
            SuperscriptSpan superscriptSpan = new SuperscriptSpan();
            spannableText.setSpan(superscriptSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        if(runFont.getTypeOffset() == Font.SS_SUB) {
            SubscriptSpan subscriptSpan = new SubscriptSpan();
            spannableText.setSpan(subscriptSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void convertBackground(int color, int runStartPos, int runEndPos, SpannableStringBuilder spannableText) {
        if(color != 0) {
            BackgroundColorSpan bgColorSpan = new BackgroundColorSpan(color);
            spannableText.setSpan(bgColorSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
    }

    public static void convertAction(ICellData cell, Action action, int runStartPos, int runEndPos, SpannableStringBuilder spannableText) {
        if(action == null) {
            return;
        }

        ActionSpan actionSpan = new ActionSpan(cell, action);
        spannableText.setSpan(actionSpan, runStartPos, runEndPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
    }
}
