package com.rayject.table.util;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class UnitsConverter {
    public final static String TAG = "UnitsConverter";
    public final static int DEFAULTTEXTSIZE = 10;
    public final static int DEFAULT_ZOOMINMAX = 150;
    public final static int DEFAULT_ZOOMOUTMIN = 10;
    public final static int DEFAULT_ZOOMSCALE = 20;
    DisplayMetrics mMetrics;
    float mZoom;
    float mZoomMin, mZoomMax;
    int mDefaultCharWidth = -1;

    public UnitsConverter(Context context) {
        this(context, DEFAULT_ZOOMOUTMIN, DEFAULT_ZOOMINMAX);
    }

    public UnitsConverter(Context context, float zoomMin, float zoomMax) {
        mZoomMin = zoomMin;
        mZoomMax = zoomMax;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        Display display = wm.getDefaultDisplay();
        display.getMetrics(metrics);
        mMetrics = metrics;
        setZoom(100f);
    }

    public float twipsToPixelsWithZoom(float twips) {
        return pointsToPixelsWithZoom(twips) / 20f;
    }

    public float pointsToPixelsWithZoom(float point) {
        return pointsToPixels(point) * mZoom / 100.0f;
    }

    public float twipsToPixels(float twips) {
        return pointsToPixels(twips) / 20f;
    }

    public float pointsToPixels(float point) {
        // 1 point = 1/72 inch
        float f1 = point * mMetrics.xdpi / 72;
        return f1;
    }


    public float pixelsToTwipsWithZoom(float pixels) {
        return pixelsToPointsWithZoom(pixels) * 20;
    }

    public float pixelsToPointsWithZoom(float pixels) {
        return pixelsToPoints(pixels) * 100.0f / mZoom;
    }

    public float pixelsToTwips(float pixels) {
        return pixelsToPoints(pixels) * 20;
    }

    public float pixelsToPoints(float pixels) {
        float f1 = pixels * 72 / mMetrics.xdpi + 0.5f;
        return f1;
    }

    // 1 twips = 1/20 point
    public int twipsToPixelsWithZoom(int twips) {
        return (int) (pointsToPixelsWithZoom(twips) / 20f + 0.5);
    }

    public int pointsToPixelsWithZoom(int point) {
        return (int) (pointsToPixels(point) * mZoom / 100.0f);
    }

    public int twipsToPixels(int twips) {
        return (int) (pointsToPixels(twips) / 20f + 0.5);
    }

    public int pointsToPixels(int point) {
        float f1 = point * mMetrics.xdpi / 72 + 0.5f;
        return (int) f1;
    }


    public int pixelsToTwipsWithZoom(int pixels) {
        return pixelsToPointsWithZoom(pixels) * 20;
    }

    public int pixelsToPointsWithZoom(int pixels) {
        return (int) (pixelsToPoints(pixels) * 100.0f / mZoom);
    }

    public int pixelsToTwips(int pixels) {
        return pixelsToPoints(pixels) * 20;
    }

    public int pixelsToPoints(int pixels) {
        float f1 = pixels * 72 / mMetrics.xdpi + 0.5f;
        return (int) f1;
    }

    public void setZoom(float zoom) {
        mZoom = zoom;
        if (mZoom >= mZoomMax) {
            mZoom = mZoomMax;
        }
        if (mZoom <= mZoomMin) {
            mZoom = mZoomMin;
        }
        mDefaultCharWidth = -1;
        //Log.i(TAG, "zoom="+mZoom);
    }

    public float getZoom() {
        return mZoom;
    }

    public float getZoomMin() {
        return mZoomMin;
    }

    public void setZoomMin(float val) {
        mZoomMin = val;
    }

    public void setZoomMax(float val) {
        mZoomMax = val;
    }

    public float getZoomMax() {
        return mZoomMax;
    }

    public int getZoomedValue(float value) {
        return Math.round(value * mZoom / 100.f);
    }

    public int getOriginValue(float zoomValue) {
        return Math.round(zoomValue * 100 / mZoom);
    }

    public int getDefaultCharWidth() {
        if (mDefaultCharWidth < 0) {
            Paint paint = new Paint();
            paint.setTypeface(Typeface.DEFAULT);
            paint.setTextSize(pointsToPixels(DEFAULTTEXTSIZE));
            mDefaultCharWidth = Math.round(paint.measureText("0"));
        }

        return mDefaultCharWidth;
    }

    public int getDefaultCharWidthWithZoom() {
        int w = getDefaultCharWidth();
        return (int) (w * mZoom / 100.0f);
    }
}
