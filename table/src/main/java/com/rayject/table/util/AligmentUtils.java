package com.rayject.table.util;


import android.text.Layout;
import android.text.Layout.Alignment;

import com.rayject.table.model.style.TableConst;

public class AligmentUtils {

    public static Alignment getAligment(int tableAlignment) {
        Alignment alignment;
        switch (tableAlignment) {
            case TableConst.ALIGNMENT_GENERAL:
                alignment = Layout.Alignment.ALIGN_NORMAL;
                break;
            case TableConst.ALIGNMENT_LEFT:
                alignment = Layout.Alignment.ALIGN_NORMAL;
                break;
            case TableConst.ALIGNMENT_CENTER:
                alignment = Layout.Alignment.ALIGN_CENTER;
                break;
            case TableConst.ALIGNMENT_RIGHT:
                alignment = Layout.Alignment.ALIGN_OPPOSITE;
                break;
            case TableConst.ALIGNMENT_FILL:
            case TableConst.ALIGNMENT_JUSTIFY:
            default:
                alignment = Layout.Alignment.ALIGN_NORMAL;
                break;
        }

        return alignment;
    }
}
