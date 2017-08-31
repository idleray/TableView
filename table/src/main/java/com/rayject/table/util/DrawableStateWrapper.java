package com.rayject.table.util;


import android.content.Context;
import android.view.View;

public class DrawableStateWrapper {
    public static final int VIEW_STATE_SELECTED = 1 << 1;

    private static View view;

    public static void init(Context context) {
        view = new View(context);
    }

    public static int[] getDrawableState(int flags) {
        setFlags(flags);
        int[] state = view.getDrawableState();

//        int[] drawableState = new int[state.length];
//        System.arraycopy(state, 0, drawableState, 0, state.length);

        return state;
    }

    private static void setFlags(int flags) {
        if((flags & VIEW_STATE_SELECTED) != 0) {
            view.setSelected(true);
        } else {
            view.setSelected(false);
        }
    }

    public static void destroy() {
        view = null;
    }
}
