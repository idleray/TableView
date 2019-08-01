package com.rayject.table.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Scroller;

import com.rayject.table.model.CellPosition;
import com.rayject.table.model.ICellData;
import com.rayject.table.model.ISheetData;
import com.rayject.table.model.Range;
import com.rayject.table.model.action.Action;
import com.rayject.table.model.object.CellObject;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.TableConst;
import com.rayject.table.util.Colors;
import com.rayject.table.util.ConstVar;
import com.rayject.table.util.DrawableStateWrapper;
import com.rayject.table.util.ExcelUtils;
import com.rayject.table.util.TableViewConfigure;
import com.rayject.table.util.UnitsConverter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TableView extends View
        implements GestureDetector.OnGestureListener, GestureDetector.OnDoubleTapListener,
        ScaleGestureDetector.OnScaleGestureListener {
    public final static String TAG = "TableView";
    public final static boolean DEBUG = false;

    private String mTitle;
    //	private Workbook mWorkbook;
    //private Sheet mActiveSheet;
    private int mFirstVisibleRow, mFirstVisibleCol; // exclude freezed row/column
    private int mLastVisibleRow = -1, mLastVisibleCol = -1;
    private int mColumnScrollX, mRowScrollY;
    private int mScrollX, mScrollY;
    private ISheetData mSheetData;
    private UnitsConverter mUnitsConverter;
//    private int mCharacterWidth = 0;
    private int mHeaderWidth = 0;
    private int mHeaderHeight = 0;
    private Scroller mScroller;
    private int mLastFlingX, mLastFlingY;
    private int mFreezePositionX = -1, mFreezePositionY = -1;
    private int mHitArea = ConstVar.HIT_NONE;
    private float mX, mY;
    private float g_dx, g_dy;
    private int mOldWidth, mNewWidth;
    private int mOldHeight, mNewHeight;
    private boolean mCanScroll = true;
//    private boolean mIsShowHeaders = true;
    private int mCurRowIndex = -1, mCurColIndex = -1;
    private Range mSelection;
    private CellPosition mCurrentSelectedCell;
    private boolean mInSelected;
    private Paint mHighlightPaint;
    private int[] mCacheRowHeights; // pixel with zoom
    private int[] mCacheColumnWidths; // pixel with zoom
    private int mCacheTableHeight = -1;
    private int mCacheTableWidth = -1;

    private GestureDetector mGestureDetector;
    private ScaleGestureDetector mScaleGestureDetector;
    private boolean mInScaleGesture = false;
    private TableViewConfigure configure;
    private LayoutChagneListener layoutChagneListener;
    private ScrollListener scrollListener;
    private Rect clipBounds;

    public interface LayoutChagneListener {
        void onLayoutChange(View v, boolean changed, int left, int top, int right, int bottom);
    }

    public interface ScrollListener {
        void onScroll();
    }

    public TableView(Context context) {
        this(context, null);
    }

    public TableView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TableView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void setLayoutChagneListener(LayoutChagneListener listener) {
        layoutChagneListener = listener;
    }

    public void setScrollListener(ScrollListener listener) {
        scrollListener = listener;
    }

    private void init() {
        clipBounds = new Rect();
        configure = new TableViewConfigure();

        Context context = getContext();
        mUnitsConverter = new UnitsConverter(context);
        mGestureDetector = new GestureDetector(context, this);
        mGestureDetector.setIsLongpressEnabled(false);
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        if (mCanScroll) {
            mScroller = new Scroller(context);
        }
        mHighlightPaint = new Paint();
        calcZoomedValues();
    }

    private void calcZoomedValues() {
//        mCharacterWidth = mUnitsConverter.getDefaultCharWidthWithZoom();
        if (configure.isShowHeaders()) {
            mHeaderWidth = mUnitsConverter.getZoomedValue(ConstVar.HEADERWIDTH);
            mHeaderHeight = mUnitsConverter.getZoomedValue(ConstVar.HEADERHEIGHT);
        } else {
            mHeaderWidth = 0;
            mHeaderHeight = 0;
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        DrawableStateWrapper.init(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        DrawableStateWrapper.destroy();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if(DEBUG) {
            Log.i(TAG, "onLayout: changed = " + changed);
        }
        super.onLayout(changed, left, top, right, bottom);
        if(layoutChagneListener != null) {
            layoutChagneListener.onLayoutChange(this, changed, left, top, right, bottom);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = getTableWidth();
        int measureHeight = getTableHeight();
        setMeasuredDimension(measureWidth, measureHeight);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if(DEBUG) {
            Log.i(TAG, "onMeasure widthMode = " + getMeasureModeString(widthMode) + ", widthSize = " + widthSize);
            Log.i(TAG, "onMeasure heightMode = " + getMeasureModeString(heightMode) + ", heightSize = " + heightSize);
        }

        int width = 0;
        switch (widthMode) {
            case MeasureSpec.UNSPECIFIED:
                width = getTableWidth();
                break;
            case MeasureSpec.AT_MOST:
                width = Math.min(getTableWidth(), widthSize);
                break;
            case MeasureSpec.EXACTLY:
                width = widthSize;
                break;
        }

        int height = 0;
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                height = getTableHeight();
                break;
            case MeasureSpec.AT_MOST:
                height = Math.min(getTableHeight(), heightSize);
                break;
            case MeasureSpec.EXACTLY:
                height = heightSize;
                break;
        }

        setMeasuredDimension(width, height);
    }

    private String getMeasureModeString(int mode) {
        String modeString = "";
        switch (mode) {
            case MeasureSpec.UNSPECIFIED:
                modeString = "UNSPECIFIED";
                break;
            case MeasureSpec.AT_MOST:
                modeString = "AT_MOST";
                break;
            case MeasureSpec.EXACTLY:
                modeString = "EXACTLY";
                break;
        }
        return modeString;
    }

    @Override
    public void onDraw(Canvas canvas) {
        canvas.getClipBounds(clipBounds);
        if(DEBUG) {
            Log.i(TAG, "onDraw");
            Log.i(TAG, "onDraw clipbounds: " + clipBounds.toShortString());
        }

        super.onDraw(canvas);

        if(mSheetData == null || mSheetData.isEmpty()) {
            return;
        }
        Range drawRange = getRangeFromRect(clipBounds);
        drawBackground(canvas);
        drawGridLines(canvas, drawRange);
        drawCells(canvas, ConstVar.DRAWCELL_BG, drawRange);
        drawCells(canvas, ConstVar.DRAWCELL_BORDER | ConstVar.DRAWCELL_TEXT | ConstVar.DRAWCELL_OBJECT, drawRange);
        if (configure.isShowHeaders()) {
            drawHeaders(canvas, drawRange);
        }
        if(configure.isShowFreezeLines()) {
            drawFreezeLines(canvas);
        }
        if(configure.isEnableSelection()) {
            drawSelection(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean ret = true;
        int action = event.getAction();
        if (DEBUG) {
            Log.i(TAG, "onTouchEvent:" + action);
        }
        float x = event.getX();
        float y = event.getY();
        if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
            mHitArea = hitTest((int) x, (int) y);
            if(DEBUG) {
                Log.i(TAG, "hitTest=" + mHitArea + ", row=" + mCurRowIndex + ", col=" + mCurColIndex);
            }
        }

        if (mHitArea == ConstVar.HIT_ROWHEADER_RESIZE) {
            resizeRow(event);
            invalidate();
            return true;
        }

        if (mHitArea == ConstVar.HIT_COLUMNHEADER_RESIZE) {
            resizeColumn(event);
            invalidate();
            return true;
        }

        if ((action & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN
                && mHitArea == ConstVar.HIT_SELECTION
                && mSelection instanceof CellPosition) {
            mInSelected = true;
        }

        if(configure.isEnableZoom()) {
            mScaleGestureDetector.onTouchEvent(event);
        }

        if (mInScaleGesture) {
            return true;
        }

        ret = mGestureDetector.onTouchEvent(event);
        if (ret) {
            return ret;
        }


        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if(configure.isEnableSelection()) {
                    if (mInSelected) {
                        selectRange(event);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                mHitArea = ConstVar.HIT_NONE;
                mInSelected = false;
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_CANCEL:
            default:
                mHitArea = ConstVar.HIT_NONE;
                mInSelected = false;
                break;
        }

        super.onTouchEvent(event);
        return true;
    }

    private boolean selectRange(MotionEvent event) {
        if (mSelection == null) {
            return false;
        }

        float x = event.getX();
        float y = event.getY();

        int endRow = findRowByPosition((int) y);
        int endCol = findColumnByPosition((int) x);
        int startRow = mCurrentSelectedCell.getRowIndex();
        int startCol = mCurrentSelectedCell.getColumnIndex();

        if (startRow > endRow) {
            int temp = startRow;
            startRow = endRow;
            endRow = temp;
        }
        if (startCol > endCol) {
            int temp = startCol;
            startCol = endCol;
            endCol = temp;
        }

        //Get real edge of the range if there are mereged cells.
        int top, left, right, bottom;
        top = startRow;
        left = startCol;
        bottom = endRow;
        right = endCol;

        for (int j = startCol; j <= endCol; j++) {
            Range range = mSheetData.inMergedRange(startRow, j, true);
            if (range != null) {
                top = Math.min(top, range.getTop());
            }
            if (startRow == endRow) {
                if (range != null) {
                    bottom = Math.max(bottom, range.getBottom());
                }
            } else {
                range = mSheetData.inMergedRange(endRow, j, true);
                if (range != null) {
                    bottom = Math.max(bottom, range.getBottom());
                }
            }
        }

        for (int i = startRow; i <= endRow; i++) {
            Range range = mSheetData.inMergedRange(i, startCol, true);
            if (range != null) {
                left = Math.min(left, range.getLeft());
            }
            if (startCol == endCol) {
                if (range != null) {
                    right = Math.max(right, range.getRight());
                }
            } else {
                range = mSheetData.inMergedRange(i, endCol, true);
                if (range != null) {
                    right = Math.max(right, range.getRight());
                }
            }
        }

        //Invalidate old selection
        invalidateRange(mSelection);
        if (left == right && top == bottom) {
            mSelection = new CellPosition(top, left);
        } else {
            mSelection = new Range(left, top, right, bottom);
        }

        Log.i(TAG, "selectRange: " + mSelection.toString());
        //Invalidate new selection
        invalidateRange(mSelection);

        return true;
    }

    private void invalidateRange(Range range) {
        Rect rect = getRectFromRange(range);
        rect.set(rect.left, rect.top, rect.right + 1, rect.bottom + 1);
        invalidate(rect);
    }

    @Override
    public void computeScroll() {
        if (mScroller != null) {
            if(DEBUG) {
                Log.d(TAG, "computeScroll ");
            }
            if (mScroller.computeScrollOffset()) {
                if(DEBUG) {
                    Log.d(TAG, "computeScroll computeScrollOffset ");
                }
                int x = mScroller.getCurrX();
                int y = mScroller.getCurrY();
                int deltaX = mLastFlingX - x;
                int deltaY = mLastFlingY - y;
                doScroll(deltaX, deltaY);
                mLastFlingX = x;
                mLastFlingY = y;
            }
        }
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setSheetData(ISheetData data) {
        mSheetData = data;
        getSheetInfo();
    }

    public ISheetData getSheet() {
        return mSheetData;
    }

    private void getSheetInfo() {
        if (mSheetData.isFreeze()) {
            mFirstVisibleRow = mSheetData.getHorizontalSplitTopRow();
            if (mFirstVisibleRow == 0) {
                mFirstVisibleRow = mSheetData.getFirstVisibleRow();
            }
            mFirstVisibleCol = mSheetData.getVerticalSplitLeftColumn();
            if (mFirstVisibleCol == 0) {
                mFirstVisibleCol = mSheetData.getFirstVisibleColumn();
            }
        } else {
            mFirstVisibleRow = mSheetData.getFirstVisibleRow();
            mFirstVisibleCol = mSheetData.getFirstVisibleColumn();
        }
    }

    public void refreshData() {
        getSheetInfo();
        clearCacheData();
    }

    private void drawBackground(Canvas canvas) {
        canvas.save();
        int right = getTableWidth() + mHeaderWidth;
        int bottom = getTableHeight() + mHeaderHeight;
        canvas.clipRect(new Rect(0, 0, right, bottom));
        Paint paint = new Paint();
        paint.setColor(Color.WHITE);
        canvas.drawPaint(paint);
        canvas.restore();
    }

    private void drawGridLines(Canvas canvas, Range drawRange) {
        Paint gridlinePaint = new Paint();
        gridlinePaint.setColor(mSheetData.getGridLineColor());

        int top = mHeaderHeight;
        int left = mHeaderWidth;
        int right = 0;
        int bottom = 0;
        int freezeX = mHeaderWidth, freezeY = mHeaderHeight;
        int freezedRowCount = 0;
        int freezedColCount = 0;

        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            //Draw horizontal line
            right = getTableWidth();
            bottom = top;

            freezedRowCount = mSheetData.getFreezedRowCount();
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                //Draw freezed horizontal grid line
                if (i >= drawRange.getTop() && i <= drawRange.getBottom()) {
                    canvas.drawLine(left, top, right, bottom, gridlinePaint);
                }

                top += getRowHeight(i);
                bottom = top;
            }
            if(!configure.isShowFreezeLines()) {
                canvas.drawLine(left, top, right, bottom, gridlinePaint);
            }
            freezeY = top;

            //Draw vertical line
            top = mHeaderHeight;
            left = mHeaderWidth;
            right = left;
            bottom = getTableHeight();
            freezedColCount = mSheetData.getFreezedColCount();
            for (int j = 0; j < freezedColCount; j++) {
                if (mSheetData.isColumnHidden(j)) {
                    continue;
                }

                //Draw freezed vertical grid line
                if (j >= drawRange.getLeft() && j <= drawRange.getRight()) {
                    canvas.drawLine(left, top, right, bottom, gridlinePaint);
                }

                left += getColumnWidth(j);
                right = left;
            }
            if(!configure.isShowFreezeLines()) {
                canvas.drawLine(left, top, right, bottom, gridlinePaint);
            }
            freezeX = left;
        }

        int firstRow;
        firstRow = mFirstVisibleRow;
        int startPosY = freezeY - mRowScrollY;

        int endPosY = getHeight();
        left = mHeaderWidth;
        top = startPosY;
        right = getTableWidth() + left;
        bottom = top;
        int rowIndex = firstRow;

        int saveCount = canvas.save();
        canvas.clipRect(new Rect(0, freezeY, getWidth(), endPosY));

        while (endPosY >= top) {
            int maxRowCount = mSheetData.getMaxRowCount();
            if (rowIndex >= maxRowCount) {
                //Draw last line
                if(rowIndex == maxRowCount) {
//                    top -= 1;
                    bottom = top;
                    canvas.drawLine(left, top, right, bottom, gridlinePaint);
                }
                break;
            }

            if (mSheetData.isRowHidden(rowIndex)) {
                rowIndex++;
                continue;
            }

            //Draw normal horizontal grid line
            if (rowIndex >= drawRange.getTop() && rowIndex <= drawRange.getBottom()) {
                canvas.drawLine(left, top, right, bottom, gridlinePaint);
            }

            bottom += getRowHeight(rowIndex);
            top = bottom;

            rowIndex++;
        }

        canvas.restoreToCount(saveCount);

        int firstColumn;
        firstColumn = mFirstVisibleCol;
        int startPosX = freezeX - mColumnScrollX;
        int endPosX = getWidth();
        left = startPosX;
        top = mHeaderHeight;
        right = left;
        bottom = getTableHeight() + top;
        int colIndex = firstColumn;

        saveCount = canvas.save();
        canvas.clipRect(new Rect(freezeX, 0, endPosX, bottom));

        while (endPosX >= left) {
            int maxColCount = mSheetData.getMaxColumnCount();
            if (colIndex >= maxColCount) {
                //Draw last line
                if(colIndex == maxColCount) {
//                    left -= 1;
                    right = left;
                    canvas.drawLine(left, top, right, bottom, gridlinePaint);
                }
                break;
            }

            if (mSheetData.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            //Draw vertical normal grid line
            if (colIndex >= drawRange.getLeft() && colIndex <= drawRange.getRight()) {
                canvas.drawLine(left, top, right, bottom, gridlinePaint);
            }

            right += getColumnWidth(colIndex);
            left = right;
            colIndex++;
        }

        canvas.restoreToCount(saveCount);

    }

    private void drawHeaders(Canvas canvas, Range drawRange) {
        Paint headerPaint = new Paint();
        headerPaint.setTextSize(ConstVar.DEFAULTTEXTSIZE);

        int top = mHeaderHeight;
        int left = mHeaderWidth;
        int right = 0;
        int bottom = 0;
        int freezeX = mHeaderWidth, freezeY = mHeaderHeight;
        int freezedRowCount = 0;
        int freezedColCount = 0;

        Rect headerRect = new Rect();

        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            freezedRowCount = mSheetData.getFreezedRowCount();
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                //Draw row header
                if (i >= drawRange.getTop() && i <= drawRange.getBottom()) {
                    headerRect.set(0, top, mHeaderWidth, top + getRowHeight(i));
                    drawHeader(canvas, Integer.toString(i + 1), headerRect, headerPaint);
                }

                top += getRowHeight(i);
            }
            freezeY = top;

            left = mHeaderWidth;
            freezedColCount = mSheetData.getFreezedColCount();
            for (int j = 0; j < freezedColCount; j++) {
                if (mSheetData.isColumnHidden(j)) {
                    continue;
                }

                //Draw column header
                if (j >= drawRange.getLeft() && j <= drawRange.getRight()) {
                    headerRect.set(left, 0, left + getColumnWidth(j), mHeaderHeight);
                    String str = ExcelUtils.columnToString(j);
                    drawHeader(canvas, str, headerRect, headerPaint);
                }

                left += getColumnWidth(j);
            }
            freezeX = left;
        }

        int firstRow;
        firstRow = mFirstVisibleRow;
        int startPosY = freezeY - mRowScrollY;

        int endPosY = getHeight();
        top = startPosY;
        right = getWidth();
        bottom = top;
        int rowIndex = firstRow;

        int saveCount = canvas.save();
        canvas.clipRect(new Rect(0, freezeY, right, endPosY));

        int maxRowCount = mSheetData.getMaxRowCount();
        while (endPosY > top) {
            if (rowIndex >= maxRowCount) {
                break;
            }

            if (mSheetData.isRowHidden(rowIndex)) {
                rowIndex++;
                continue;
            }

            //Draw row header
            if (rowIndex >= drawRange.getTop() && rowIndex <= drawRange.getBottom()) {
                headerRect.set(0, top, mHeaderWidth, top + getRowHeight(rowIndex));
                drawHeader(canvas, Integer.toString(rowIndex + 1), headerRect, headerPaint);
            }

            bottom += getRowHeight(rowIndex);
            top = bottom;

            rowIndex++;
        }

        canvas.restoreToCount(saveCount);

        int firstColumn;
        firstColumn = mFirstVisibleCol;
        int startPosX = freezeX - mColumnScrollX;
        int endPosX = getWidth();
        left = startPosX;
        right = left;
        bottom = getTableHeight();
        int colIndex = firstColumn;

        saveCount = canvas.save();
        canvas.clipRect(new Rect(freezeX, 0, endPosX, bottom));

        int maxColCount = mSheetData.getMaxColumnCount();
        while (endPosX > left) {
            if (colIndex >= maxColCount) {
                break;
            }

            if (mSheetData.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            //Draw column header
            if (colIndex >= drawRange.getLeft() && colIndex <= drawRange.getRight()) {
                headerRect.set(left, 0, left + getColumnWidth(colIndex), mHeaderHeight);
                String str = ExcelUtils.columnToString(colIndex);
                drawHeader(canvas, str, headerRect, headerPaint);
            }

            right += getColumnWidth(colIndex);
            left = right;
            colIndex++;
        }

        canvas.restoreToCount(saveCount);

        //Draw left-top header
        drawHeader(canvas, null, new Rect(0, 0, mHeaderWidth, mHeaderHeight), headerPaint);
    }

    private void drawFreezeLines(Canvas canvas) {
        if (mSheetData.isFreeze()) {
            Paint paint = new Paint();
            paint.setStrokeWidth(mUnitsConverter.getZoomedValue(1));
            paint.setColor(Color.BLACK);

            int freezedRowCount = mSheetData.getFreezedRowCount();
            if (freezedRowCount > 0) {
                int y = getPositionYAfterFreeze();
                canvas.drawLine(0, y, getTableWidth(), y, paint);
            }

            int freezeColCount = mSheetData.getFreezedColCount();
            if (freezeColCount > 0) {
                int x = getPositionXAfterFreeze();
                canvas.drawLine(x, 0, x, getTableHeight(), paint);
            }
        }
    }

    private void drawCells(Canvas canvas, int drawType, Range drawRange) {
        int top = mHeaderHeight;
        int left = mHeaderWidth;
        int right = getWidth();
        int freezeY = mHeaderHeight;
        int freezedRowCount = 0;
        Point rowStartPos = new Point();
        Paint paint = new Paint();

        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int saveCount = canvas.save();
            canvas.clipRect(new Rect(0, freezeY + 1, right, getHeight()));

            freezedRowCount = mSheetData.getFreezedRowCount();
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                boolean drawMerge = false;
                if (i == 0) {
                    drawMerge = true;
                }
                //bottom = top + getRowHeight(i);
                rowStartPos.set(left, top);
                if (i >= drawRange.getTop() && i <= drawRange.getBottom()) {
                    drawRow(canvas, paint, drawType, i, rowStartPos, drawMerge, drawRange);
                }
                top += getRowHeight(i);
            }
            freezeY = top;

            canvas.restoreToCount(saveCount);
        }

        int firstRow;
        firstRow = mFirstVisibleRow;
        int startPosY = freezeY - mRowScrollY;
        int endPosY = getHeight();
        top = startPosY;
        int rowIndex = firstRow;
        if (DEBUG) {
            Log.i(TAG, "start draw row");
            Log.i(TAG, "firstRow=" + firstRow + ", startPosY=" + startPosY);
        }
        int saveCount = canvas.save();
        //Rect r = canvas.getClipBounds();
        canvas.clipRect(new Rect(0, freezeY + 1, right, endPosY));
        //Rect r1 = canvas.getClipBounds();

        while (endPosY > top) {
            int maxRowCount = mSheetData.getMaxRowCount();
            if (rowIndex >= maxRowCount) {
                break;
            }

            if (mSheetData.isRowHidden(rowIndex)) {
                rowIndex++;
                continue;
            }

            boolean drawMerge = false;
            if (rowIndex == firstRow) {
                drawMerge = true;
            }
            rowStartPos.set(left, top);
            if (false) {
                Log.i(TAG, "start drawRow(" + rowIndex + ")");
            }
            if (rowIndex >= drawRange.getTop() && rowIndex <= drawRange.getBottom()) {
                drawRow(canvas, paint, drawType, rowIndex, rowStartPos, drawMerge, drawRange);
            }
            top += getRowHeight(rowIndex);
            rowIndex++;
        }

        canvas.restoreToCount(saveCount);
    }

    private void drawRow(Canvas canvas, Paint paint, int drawType, int rowId, Point startPoint, boolean rowDrawMerge, Range drawRange) {
        Rect cellRect = new Rect();
        boolean isFreeze = mSheetData.isFreeze();
        int left = startPoint.x;
        int top = startPoint.y;
        int right = left;
        int bottom = top + getRowHeight(rowId);
        int freezeX = mHeaderWidth;
        if (isFreeze) {
            int freezedColCount = mSheetData.getFreezedColCount();
            for (int i = 0; i < freezedColCount; i++) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }

                right += getColumnWidth(i);
                cellRect.set(left, top, right, bottom);

                boolean colDrawMerge = false;
                if (i == 0) {
                    colDrawMerge = true;
                }
                if (i >= drawRange.getLeft() && i <= drawRange.getRight()) {
                    drawCell(canvas, paint, drawType, rowId, i, cellRect, rowDrawMerge | colDrawMerge);
                }
                left = right;
            }
            freezeX = left;
        }

        int firstColumn;
        firstColumn = mFirstVisibleCol;
        int startPosX = freezeX - mColumnScrollX;
        int endPosX = getWidth();
        left = startPosX;
        right = left;
        int colIndex = firstColumn;

        int saveCount = canvas.save();
        //Rect r = canvas.getClipBounds();
        int clipTop = 0;
        int clipBottom = clipTop + getHeight();
        canvas.clipRect(new Rect(freezeX + 1, clipTop, endPosX, clipBottom));
        //Rect r1 = canvas.getClipBounds();

        while (endPosX > left) {
            int maxColCount = mSheetData.getMaxColumnCount();
            if (colIndex >= maxColCount) {
                break;
            }

            if (mSheetData.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            right += getColumnWidth(colIndex);
            cellRect.set(left, top, right, bottom);

            boolean colDrawMerge = false;
            if (colIndex == firstColumn) {
                colDrawMerge = true;
            }
            if (colIndex >= drawRange.getLeft() && colIndex <= drawRange.getRight()) {
                drawCell(canvas, paint, drawType, rowId, colIndex, cellRect, rowDrawMerge | colDrawMerge);
            }
            left = right;
            colIndex++;
        }

        canvas.restoreToCount(saveCount);
    }

    private void drawCell(Canvas canvas, Paint paint, int drawType, int rowId, int colId, Rect cellRect, boolean drawMerge) {
        //Skip merged cell
        int mergedRowId = rowId;
        int mergedColId = colId;
        Range range = mSheetData.inMergedRange(rowId, colId, false);
        if (range != null) {
            if (drawMerge) {
                if (range.getTop() < mFirstVisibleRow ||
                        range.getLeft() < mFirstVisibleCol) {
                    mergedRowId = range.getTop();
                    mergedColId = range.getLeft();
                } else {
                    return;
                }
            } else {
                return;
            }
        }

        ICellData cellData = mSheetData.getCellData(mergedRowId, mergedColId);
        if (cellData != null) {
            if (cellData.isMerged()) {
                Range r = cellData.getMergedRange();
                int left = r.getLeft();
                int top = r.getTop();
                int right = r.getRight();
                int bottom = r.getBottom();

                int cellLeft = cellRect.left;
                for (int i = mergedColId; i < colId; i++) {
                    if (!mSheetData.isColumnHidden(i)) {
                        cellLeft -= getColumnWidth(i);
                    }
                }

                int cellTop = cellRect.top;
                for (int j = mergedRowId; j < rowId; j++) {
                    if (!mSheetData.isRowHidden(j)) {
                        cellTop -= getRowHeight(j);
                    }
                }

                int width = 0;
                for (int i = left; i <= right; i++) {
                    if (!mSheetData.isColumnHidden(i)) {
                        width += getColumnWidth(i);
                    }
                }

                int height = 0;
                for (int j = top; j <= bottom; j++) {
                    if (!mSheetData.isRowHidden(j)) {
                        height += getRowHeight(j);
                    }
                }
                cellRect.set(cellLeft, cellTop, cellLeft + width, cellTop + height);
            }

            int somePadding = 1;
            Rect bounds = new Rect();
            if (!cellData.getCellStyle().isAutoWrap()) {
                int clipLeft = cellRect.left;
                int clipRight = cellRect.right;

                for (int i = colId - 1; i >= mFirstVisibleCol; i--) {
                    if (mSheetData.isColumnHidden(i)) {
                        continue;
                    }

                    if (!mSheetData.isBlankCell(rowId, i) ||
                            range != null) {
                        break;
                    }

                    clipLeft -= getColumnWidth(i);
                }


                boolean hasClip = canvas.getClipBounds(bounds);
                int maxColumnCount = mSheetData.getMaxColumnCount();
                for (int j = colId + 1; j < maxColumnCount; j++) {
                    if (mSheetData.isColumnHidden(j)) {
                        continue;
                    }

                    if (!mSheetData.isBlankCell(rowId, j)) {
                        break;
                    }

                    clipRight += getColumnWidth(j);
                    if (hasClip && clipRight > bounds.right) {
                        break;
                    }
                }
                bounds.set(clipLeft, bounds.top, clipRight + somePadding, bounds.bottom + somePadding);
            } else {
                bounds.set(cellRect.left, cellRect.top, cellRect.right + somePadding, cellRect.bottom + somePadding);
            }

            int saveCount = canvas.save();
            canvas.clipRect(bounds);

            cellData.draw(canvas, paint, cellRect, drawType, mUnitsConverter);

            canvas.restoreToCount(saveCount);
        }
    }

    private void drawHeader(Canvas canvas, String text, Rect rect, Paint paint) {
        paint.setAntiAlias(false);
        paint.setStyle(Style.FILL);
        paint.setColor(Color.LTGRAY);
        canvas.drawRect(rect, paint); //use fill to draw background

        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        rect.set(rect.left, rect.top, rect.right, rect.bottom/*+1*/);
        canvas.drawRect(rect, paint); //use stroke to draw border

        int x = rect.centerX();
        int y = rect.centerY();
        paint.setStyle(Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        if (text != null && text.length() > 0) {
            int textWidth = Math.round(paint.measureText(text));
            x -= textWidth / 2;
            x = Math.max(x, rect.left);
            int textHeight = Math.round(paint.getTextSize());
            y += textHeight / 2;
            y = Math.min(y, rect.bottom);

            int saveCount = canvas.save();
            canvas.clipRect(rect);

            canvas.drawText(text, x, y, paint); //draw text at center

            canvas.restoreToCount(saveCount);
        }
    }

    private void drawSelection(Canvas canvas) {
        if (mSelection != null) {
            int leftCol = mSelection.getLeft();
            int topRow = mSelection.getTop();
            int saveCount = canvas.save();

            Rect clipRect = new Rect();
            canvas.getClipBounds(clipRect);

            int freezeX = getPositionXAfterFreeze();
            int freezeY = getPositionYAfterFreeze();
            boolean inFreezeRow = isInFreezeRow(topRow);
            boolean inFreezeColumn = isInFreezeColumn(leftCol);
            if (inFreezeRow && inFreezeColumn) {
                //Do nothing
            } else if (inFreezeRow) {
                clipRect.left = freezeX;
            } else if (inFreezeColumn) {
                clipRect.top = freezeY;
            } else {
                clipRect.left = freezeX;
                clipRect.top = freezeY;
            }

            canvas.clipRect(clipRect);

            Rect rect = getRectFromRange(mSelection);
            //The selection cross freezed column
            if (!isInFreezeColumn(mSelection.getRight()) && rect.right < freezeX) {
                rect.right = freezeX;
            }
            //The selection cross freezed row
            if (!isInFreezeRow(mSelection.getBottom()) && rect.bottom < freezeY) {
                rect.bottom = freezeY;
            }

            mHighlightPaint.setColor(Colors.SELCTION_COLOR);
            mHighlightPaint.setStyle(Style.FILL);
            canvas.drawRect(rect, mHighlightPaint);

            mHighlightPaint.setColor(Color.BLACK);
            mHighlightPaint.setStyle(Style.STROKE);
            canvas.drawRect(rect, mHighlightPaint);

            canvas.restoreToCount(saveCount);
        }
    }

    @Override
    public boolean onDown(MotionEvent e) {
        if (mScroller != null) {
            if (!mScroller.isFinished()) {
                mScroller.forceFinished(true);
            }
        }

        return false;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                           float velocityY) {

        if (mInSelected) {
            return false;
        }

        if (DEBUG) {
            Log.i(TAG, "onFling velocityX=" + velocityX + ", velocityY=" + velocityY);
        }

        doFling(velocityX, velocityY, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);

        return true;
    }

    private void doFling(float velocityX, float velocityY, int minX, int maxX, int minY, int maxY) {
        if (mCanScroll) {
            int initialX = velocityX < 0 ? Integer.MAX_VALUE : 0;
            int initialY = velocityY < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mLastFlingY = initialY;
            if (mScroller != null) {
                mScroller.fling(initialX, initialY, (int) velocityX, (int) velocityY, minX, maxX, minY, maxY);
            }
        }
    }

    @Override
    public void onLongPress(MotionEvent e) {


    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        if (mInSelected) {
            return false;
        }

        if (mCanScroll) {
            doScroll(distanceX, distanceY);
        }

        return true;
    }

    public void smoothScrollTo(int x, int y) {
        if(mCanScroll) {
            int dx = getTableScrollX() - x;
            int dy = getTableScrollY() - y;
            mLastFlingX = 0;
            mLastFlingY = 0;
            mScroller.startScroll(0, 0, dx, dy);
            invalidate();
        }
    }

    public void smoothScrollToX(int x) {
        smoothScrollTo(x, getTableScrollY());
    }

    public void smoothScrollToY(int y) {
        smoothScrollTo(getTableScrollX(), y);
    }

    private void doScroll(float distanceX, float distanceY) {
        if (DEBUG) {
            Log.i(TAG, "doScroll distanceX=" + distanceX + ", distanceY=" + distanceY);
        }

        try {
            scrollX(distanceX);
            scrollY(distanceY);
            if(scrollListener != null) {
                scrollListener.onScroll();
            }
            invalidate();
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    public int getTableScrollX() {
        return mScrollX;
    }

    public int getTableScrollY() {
        return mScrollY;
    }


    private void scrollX(float distanceX) {
        if(getTableWidth() <= getWidth()) {
            return;
        }

        int colIndex = mFirstVisibleCol;
        int dx = Math.round(distanceX);
        if (distanceX > 0) {
            //Scroll right
            int lastVisibleColId = findLastVisibleColumn();
            int lastColPositionX = getColumnPositionX(lastVisibleColId);
            int lastColWidth = getColumnWidth(lastVisibleColId);
            int lastColRemainW = lastColPositionX + lastColWidth - getWidth();
            if(DEBUG) {
                Log.i(TAG, "mFirstVisibleCol = " + mFirstVisibleCol + ", lastVisibleColId = " + lastVisibleColId + ", lastColPositionX = " + lastColPositionX + ", lastColRemainW = " + lastColRemainW);
            }

            int i;
            int scrollDelta = lastColRemainW;
            int maxColCount = mSheetData.getMaxColumnCount();
            //Check if scroll to the end row of sheet
            for (i = lastVisibleColId; i < maxColCount; i++) {
                if (dx < scrollDelta) {
                    break;
                }
                if( i != maxColCount - 1) {
                    scrollDelta += getColumnWidth(i+1);
                }
            }
            if (i == maxColCount) {
                if (DEBUG) {
                    Log.i(TAG, "Can't scrollX anymore. scrollDelta=" + scrollDelta + ", dx=" + dx);
                }
                dx = scrollDelta;
            }
            mScrollX += dx;

            int nextX = getColumnWidth(colIndex) - mColumnScrollX;
            while (dx - nextX > 0) {
                mColumnScrollX = 0;
                dx -= nextX;
                nextX = getColumnWidth(++colIndex);
            }
            mColumnScrollX = dx + mColumnScrollX;
            mFirstVisibleCol = colIndex;
            //TODO: where to reset it?
            mLastVisibleCol = -1;
        } else {
            mScrollX +=dx;
            if(mScrollX < 0) {
                mScrollX = 0;
            }
            //Scroll left
            int previousX = -mColumnScrollX;
            int freezeColIndex = 0;
            if (mSheetData.isFreeze()) {
                freezeColIndex = mSheetData.getFreezedColCount();
            }
            while (dx - previousX < 0) {
                mColumnScrollX = 0;
                if (colIndex == freezeColIndex) {
                    dx = previousX;
                } else {
                    dx -= previousX;
                    previousX = -getColumnWidth(--colIndex);
                }
            }
            mColumnScrollX = Math.abs(dx - previousX);
            mFirstVisibleCol = colIndex;
            //TODO: where to reset it?
            mLastVisibleCol = -1;
        }
        if (DEBUG) {
            Log.i(TAG, "after scrollX. mFirstVisibleCol=" + mFirstVisibleCol + ", mScrollX=" + mColumnScrollX);
        }
    }

    private void scrollY(float distanceY) {
        if(getTableHeight() <= getHeight()) {
            return;
        }

        int rowIndex = mFirstVisibleRow;
        int dy = Math.round(distanceY);
        if (distanceY > 0) {
            //Scroll up
            int lastVisibleRowId = findLastVisibleRow();
            int lastRowPositionY = getRowPositionY(lastVisibleRowId);
            int lastRowHeight = getRowHeight(lastVisibleRowId);
            int lastRowRemainHeight = lastRowPositionY + lastRowHeight - getHeight();

            int i;
            int scrollDelta = lastRowRemainHeight;
            int maxRowCount = mSheetData.getMaxRowCount();
            //Check if scroll to the end row of sheet
            for (i = lastVisibleRowId; i < maxRowCount; i++) {
                if (dy < scrollDelta) {
                    break;
                }
                if(i != maxRowCount - 1) {
                    scrollDelta += getRowHeight(i);
                }
            }
            if (i == maxRowCount) {
                if (DEBUG) {
                    Log.i(TAG, "Can't scrollY anymore. scrollDelta=" + scrollDelta + ", dy=" + dy);
                }
                dy = scrollDelta;
            }
            mScrollY += dy;

            int nextY = getRowHeight(rowIndex) - mRowScrollY;
            while (dy - nextY > 0) {
                mRowScrollY = 0;
                dy -= nextY;

                nextY = getRowHeight(++rowIndex);

            }
            mRowScrollY = dy + mRowScrollY;
            mFirstVisibleRow = rowIndex;
            //TODO: where to reset it?
            mLastVisibleRow = -1;
        } else {
            mScrollY += dy;
            if(mScrollY < 0) {
                mScrollY = 0;
            }
            //Scroll down
            int previousY = -mRowScrollY;
            while (dy - previousY < 0) {
                mRowScrollY = 0;
                int freezeRowIndex = 0;
                if (mSheetData.isFreeze()) {
                    freezeRowIndex = mSheetData.getFreezedRowCount();
                }
                if (rowIndex == freezeRowIndex) {
                    dy = previousY;
                } else {
                    dy -= previousY;
                    previousY = -getRowHeight(--rowIndex);
                }
            }
            mRowScrollY = Math.abs(dy - previousY);
            mFirstVisibleRow = rowIndex;
            //TODO: where to reset it?
            mLastVisibleRow = -1;
        }
        if (DEBUG) {
            Log.i(TAG, "after scrollY. mFirstVisibleRow=" + mFirstVisibleRow + ", mScrollY=" + mRowScrollY);
        }
    }

    @Override
    public boolean canScrollVertically(int direction) {
        boolean ret = true;
        if(direction < 0) {
            if(mRowScrollY == 0 &&
                    mFirstVisibleRow == mSheetData.getFirstVisibleRow()) {
                ret = false;
            }
        } else {
            int lastVisibleRowId = findLastVisibleRow();
            if(lastVisibleRowId == mSheetData.getMaxRowCount() - 1) {
                int lastRowPositionY = getRowPositionY(lastVisibleRowId);
                int lastRowHeight = getRowHeight(lastVisibleRowId);
                int lastRowRemainHeight = lastRowPositionY + lastRowHeight - getHeight();
                if(lastRowRemainHeight <= 0) {
                    ret = false;
                }
            }
        }

        if(DEBUG) {
            Log.d(TAG, "canScrollVertically direction=" + direction + ", ret = " + ret);
        }
        return ret;
    }

    @Override
    public boolean canScrollHorizontally(int direction) {

        boolean ret = true;
        if(direction < 0) {
            if(mColumnScrollX == 0 &&
                    mFirstVisibleCol == mSheetData.getFirstVisibleColumn()) {
                ret = false;
            }
        } else {
            int lastVisibleColId = findLastVisibleColumn();
            if(lastVisibleColId == mSheetData.getMaxColumnCount() - 1) {
                int lastColPositionX = getColumnPositionX(lastVisibleColId);
                int lastColWidth = getColumnWidth(lastVisibleColId);
                int lastColRemainW = lastColPositionX + lastColWidth - getWidth();
                if(lastColRemainW <= 0) {
                    ret = false;
                }
            }
        }

        if(DEBUG) {
            Log.d(TAG, "canScrollHorizontally direction=" + direction + ", ret = " + ret);
        }

        return ret;
    }

    @Override
    public void onShowPress(MotionEvent e) {


    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {

        return false;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        if(DEBUG) {
            Log.i(TAG, "onSingleTapConfirmed. mCurRowIndex=" + mCurRowIndex + ", mCurColIndex=" + mCurColIndex);
        }

        selectCell(mCurRowIndex, mCurColIndex);
        if (mCurrentSelectedCell != null) {
            actionOnCell(e, mCurrentSelectedCell.getTop(), mCurrentSelectedCell.getLeft());
        }
        return true;
    }

    private void actionOnCell(MotionEvent event, int rowId, int colId) {
        ICellData cellData = mSheetData.getCellData(rowId, colId);
        if(cellData == null) {
            return;
        }

        if(actionOnCellObject(event, rowId, colId)) {
            return;
        }

        if(actionOnCellText(event, rowId, colId)) {
            return;
        }

        Action action = cellData.getAction();
        if(action != null) {
            action.onAction(cellData);
        }
    }

    private boolean actionOnCellText(MotionEvent event, int rowId, int colId) {
        ICellData cellData = mSheetData.getCellData(rowId, colId);
        if(cellData == null) {
            return false;
        }

        if (cellData.getLayout() != null && cellData.getTextValue() != null && (cellData.getTextValue() instanceof Spannable)) {
            Layout layout = cellData.getLayout();
            Spannable buffer = (Spannable) cellData.getTextValue();
            int x = 0, y = 0; //Position of the layout in cell
            int textWidth = (int) Layout.getDesiredWidth(layout.getText(), layout.getPaint());
            int textHeight = layout.getHeight();

            //Get the y position of the layout in cell
            CellStyle style = cellData.getCellStyle();
            int cellHeight = getCellHeight(rowId, colId, true);
            int vAlignment = (style == null ? TableConst.VERTICAL_ALIGNMENT_CENTRE : style.getVerticalAlignment());
            switch (vAlignment) {
                case TableConst.VERTICAL_ALIGNMENT_TOP:
                    break;
                case TableConst.VERTICAL_ALIGNMENT_CENTRE:
                    y = (cellHeight - textHeight) / 2;
                    break;
                case TableConst.VERTICAL_ALIGNMENT_BOTTOM:
                    y = cellHeight - textHeight;
                    break;
                case TableConst.VERTICAL_ALIGNMENT_JUSTIFY:
                    break;
                default:
                    break;
            }

            //Get the x position of the layout in cell
            int cellWidth = getCellWidth(rowId, colId, true);
            Layout.Alignment align = layout.getAlignment();
            if (align == Layout.Alignment.ALIGN_NORMAL) {

            } else if (align == Layout.Alignment.ALIGN_CENTER) {
                x = (cellWidth - textWidth) / 2;
            } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                x = cellWidth - textWidth;
            }

            //Check if the touch point is in the text area
            int xInCell = (int) event.getX() - getColumnPositionX(colId);
            int yInCell = (int) event.getY() - getRowPositionY(rowId);
            if (yInCell < y || yInCell > (y + textHeight)) {
                return false;
            }
            if (xInCell < x || xInCell > (x + textWidth)) {
                return false;
            }

            int yInLayout = yInCell - y;
            //Horizontal aligment was handled by Layout.Alignment
//            int xInLayout = xInCell - x;
            int xInLayout = xInCell;
            int line = layout.getLineForVertical(yInLayout);
            int off = layout.getOffsetForHorizontal(line, xInLayout);
            ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                link[0].onClick(this);
            }
        }

        return false;
    }

    private boolean actionOnCellObject(MotionEvent event, int rowId, int colId) {
        ICellData cell = mSheetData.getCellData(rowId, colId);
        if(cell == null) {
            return false;
        }
        int cellWidth = getCellWidth(rowId, colId, true);
        int cellHeight = getCellHeight(rowId, colId, true);
        int xInCell = (int) event.getX() - getColumnPositionX(colId);
        int yInCell = (int) event.getY() - getRowPositionY(rowId);

        int count = cell.getObjectCount();
        for(int i = count - 1; i >= 0; i--) {
            CellObject dObject = cell.getObject(i);

            Point point = CellObject.getPositionInRect(dObject, cellWidth, cellHeight);
            int left = point.x;
            int top = point.y;

            Rect rect = new Rect(left, top, left+dObject.getWidth(), top + dObject.getHeight());
            if(rect.contains(xInCell, yInCell)) {
                if(dObject.onClick()) {
                    return true;
                }
            }
        }

        return false;
    }

    public void zoomBy(float percent) {
        float newZoom = mUnitsConverter.getZoom() + percent;
        setZoom(newZoom);
    }

    public void setZoom(float zoom) {
        if(!configure.isEnableZoom()) {
            return;
        }

        float zoomValue = zoom;
        if (zoomValue == ConstVar.FITWIDTH) {
            zoomValue = getFitWidthZoom(getWidth());
        } else if (zoomValue == ConstVar.FITHEIGHT) {
            zoomValue = getFitHeightZoom(getHeight());
        }

        float oldZoom = mUnitsConverter.getZoom();
        if (oldZoom != zoomValue) {
            //Scroll some distance to make zoom looks more natural
            int w = this.getMeasuredWidth();
            int h = this.getMeasuredHeight();
            float distanceX = w * (zoomValue - oldZoom) / 100 / 2;
            float distanceY = h * (zoomValue - oldZoom) / 100 / 2;
            doScroll(distanceX, distanceY);

            mUnitsConverter.setZoom(zoomValue);

            clearCacheData();
            mSheetData.updateData();
            calcZoomedValues();
            invalidate();
        }
    }

    private float getFitWidthZoom(int viewWidth) {
        //int viewWidth = getWidth();
        int columnWidths = 0;//getColumnPositionX(mSheetData.getLastColumn());

        int lastColumn = mSheetData.getLastColumn();
        for (int i = 0; i <= lastColumn; i++) {
            columnWidths += mSheetData.getColumnWidth(i);
        }
        float zoom = 100;
        if (columnWidths > 0) {
            columnWidths = mUnitsConverter.getOriginValue(columnWidths + mHeaderWidth);
            zoom = viewWidth * 100.0f / columnWidths;
        }

        return zoom;
    }

    private float getFitHeightZoom(int viewHeight) {
        int rowHeights = 0;
        int lastRow = mSheetData.getLastRow();
        for (int i = 0; i <= lastRow; i++) {
            rowHeights += mSheetData.getRowHeight(i);
        }

        float zoom = 100;
        if (rowHeights > 0) {
            rowHeights =  mUnitsConverter.getOriginValue(rowHeights + mHeaderHeight);
            zoom = viewHeight * 100.0f / rowHeights;
        }
        return zoom;
    }

    public float getZoom() {
        return mUnitsConverter.getZoom();
    }

    private int findLastVisibleRow() {
        if (mLastVisibleRow < 0) {
            int top = mHeaderHeight;
            int freezeY = getPositionYAfterFreeze();

            int firstRow;
            firstRow = mFirstVisibleRow;
            int startPosY = freezeY - mRowScrollY;
            int endPosY = getHeight();
            top = startPosY;
            int rowIndex = firstRow;

            while (true) {
                int maxRowCount = mSheetData.getMaxRowCount();
                if (rowIndex >= maxRowCount) {
                    rowIndex = maxRowCount - 1;
                    break;
                }

                if (mSheetData.isRowHidden(rowIndex)) {
                    rowIndex++;
                    continue;
                }

                top += getRowHeight(rowIndex);
                if (endPosY > top) {
                    rowIndex++;
                } else {
                    break;
                }
            }
            mLastVisibleRow = rowIndex;
        }

        if (DEBUG) {
            Log.i(TAG, "mLastVisibleRow=" + mLastVisibleRow);
        }
        return mLastVisibleRow;
    }

    private int findLastVisibleColumn() {
        if (mLastVisibleCol < 0) {
            int left = mHeaderWidth;
            int freezeX = getPositionXAfterFreeze();

            int firstColumn;
            firstColumn = mFirstVisibleCol;
            int startPosX = freezeX - mColumnScrollX;
            int endPosX = getWidth();
            left = startPosX;
            int colIndex = firstColumn;

            while (true) {
                int maxColCount = mSheetData.getMaxColumnCount();
                if (colIndex >= maxColCount) {
                    colIndex = maxColCount - 1;
                    break;
                }

                if (mSheetData.isColumnHidden(colIndex)) {
                    colIndex++;
                    continue;
                }

                left += getColumnWidth(colIndex);
                if (endPosX > left) {
                    colIndex++;
                } else {
                    break;
                }

            }
            mLastVisibleCol = colIndex;
        }

        return mLastVisibleCol;
    }

    public int getRowPositionY(int rowId) {

        int top = mHeaderHeight;
        boolean isFreeze = mSheetData.isFreeze();
        int freezedRowCount = mSheetData.getFreezedRowCount();
        if (isFreeze && rowId < freezedRowCount) {
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                if (i == rowId) {
                    break;
                } else {
                    top += getRowHeight(i);
                }
            }
            return top;
        }

        int freezeY = getPositionYAfterFreeze();
        int positionY = freezeY - mRowScrollY;
        if (rowId < mFirstVisibleRow) {
            for (int i = mFirstVisibleRow - 1; i >= rowId; i--) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                positionY -= getRowHeight(i);
            }
        } else {
            for (int i = mFirstVisibleRow; i < rowId; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                positionY += getRowHeight(i);
            }
        }

        return positionY;
    }

    public int getColumnPositionX(int colId) {
        colId = ExcelUtils.bounds(0, colId, mSheetData.getMaxColumnCount());

        int left = mHeaderWidth;
        boolean isFreeze = mSheetData.isFreeze();
        int freezedColCount = mSheetData.getFreezedColCount();
        if (isFreeze && colId < freezedColCount) {
            for (int i = 0; i < freezedColCount; i++) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }

                if (i == colId) {
                    break;
                } else {
                    left += getColumnWidth(i);
                }
            }
            return left;
        }

        int freezeX = getPositionXAfterFreeze();
        int positionX = freezeX - mColumnScrollX;
        if (colId < mFirstVisibleCol) {
            for (int i = mFirstVisibleCol - 1; i >= colId; i--) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }
                positionX -= getColumnWidth(i);
            }
        } else {
            for (int i = mFirstVisibleCol; i < colId; i++) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }
                positionX += getColumnWidth(i);
            }
        }

        return positionX;
    }

    /**
     * Get the Y position after freezed row
     *
     * @return The Y position
     */
    private int getPositionYAfterFreeze() {
        if (mFreezePositionY < 0) {
            int top = mHeaderHeight;
            boolean isFreeze = mSheetData.isFreeze();
            if (isFreeze) {
                int freezedRowCount = mSheetData.getFreezedRowCount();
                for (int i = 0; i < freezedRowCount; i++) {
                    if (mSheetData.isRowHidden(i)) {
                        continue;
                    }

                    top += getRowHeight(i);
                }
            }
            mFreezePositionY = top;
        }

        return mFreezePositionY;
    }

    /**
     * Get the X position after freezed column
     *
     * @return The X position
     */
    private int getPositionXAfterFreeze() {
        if (mFreezePositionX < 0) {
            int left = mHeaderWidth;
            boolean isFreeze = mSheetData.isFreeze();
            if (isFreeze) {
                int freezedColCount = mSheetData.getFreezedColCount();
                for (int i = 0; i < freezedColCount; i++) {
                    if (mSheetData.isColumnHidden(i)) {
                        continue;
                    }

                    left += getColumnWidth(i);
                }
            }
            mFreezePositionX = left;
        }

        return mFreezePositionX;
    }

    private int findRowByPosition(int posY) {
        int top = mHeaderHeight;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int freezedRowCount = mSheetData.getFreezedRowCount();
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                top += getRowHeight(i);
                if (posY <= top) {
                    //Log.i(TAG, "findRowByPosition row="+i);
                    return i;
                }
            }
        }

        int rowIndex = mFirstVisibleRow;
        top -= mRowScrollY;

        while (true) {
            int maxRowCount = mSheetData.getMaxRowCount();
            if (rowIndex >= maxRowCount) {
                rowIndex = maxRowCount - 1;
                break;
            }

            if (mSheetData.isRowHidden(rowIndex)) {
                rowIndex++;
                continue;
            }

            top += getRowHeight(rowIndex);
            if (posY <= top) {
                break;
            } else {
                rowIndex++;
            }
        }
        //Log.i(TAG, "findRowByPosition row="+rowIndex);
        return rowIndex;
    }

    private int findColumnByPosition(int posX) {
        int left = mHeaderWidth;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int freezedColCount = mSheetData.getFreezedColCount();
            for (int i = 0; i < freezedColCount; i++) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }

                left += getColumnWidth(i);
                if (posX <= left) {
                    //Log.i(TAG, "findRowByPosition row="+i);
                    return i;
                }
            }
        }

        int colIndex = mFirstVisibleCol;
        left -= mColumnScrollX;

        while (true) {
            int maxColCount = mSheetData.getMaxColumnCount();
            if (colIndex >= maxColCount) {
                colIndex = maxColCount - 1;
                break;
            }

            if (mSheetData.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            left += getColumnWidth(colIndex);
            if (posX <= left) {
                break;
            } else {
                colIndex++;
            }
        }
        //Log.i(TAG, "findRowByPosition row="+rowIndex);
        return colIndex;
    }

    private int hitTest(int x, int y) {
        int hitArea = ConstVar.HIT_NONE;
        do {
            //TODO: improve later
            mCurRowIndex = -1;
            mCurColIndex = -1;
            if (x < 0 || y < 0) {
                hitArea = ConstVar.HIT_NONE;
                break;
            }

            if (x < mHeaderWidth && y < mHeaderHeight) {
                hitArea = ConstVar.HIT_RCHEADER;
                break;
            }

            if (x < mHeaderWidth) {
                hitArea = hitTestRowHeader(y);
                break;
            }

            if (y < mHeaderHeight) {
                hitArea = hitTestColumnHeader(x);
                break;
            }
            hitArea = hitTestTable(x, y);
        } while (false);

        return hitArea;
    }

    private int hitTestRowHeader(int posY) {
        int top = mHeaderHeight;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int freezedRowCount = mSheetData.getFreezedRowCount();
            for (int i = 0; i < freezedRowCount; i++) {
                if (mSheetData.isRowHidden(i)) {
                    continue;
                }

                top += getRowHeight(i);
                if (posY <= top) {
                    mCurRowIndex = i;
                    if ((top - posY) <= ConstVar.RESIZE_AREA) {
                        if(configure.isEnableResizeRow()) {
                            return ConstVar.HIT_ROWHEADER_RESIZE;
                        } else {
                            return ConstVar.HIT_ROWHEADER;
                        }
                    } else {
                        return ConstVar.HIT_ROWHEADER;
                    }
                }
            }
        }

        int rowIndex = mFirstVisibleRow;
        top -= mRowScrollY;

        while (true) {
            int maxRowCount = mSheetData.getMaxRowCount();
            if (rowIndex >= maxRowCount) {
                break;
            }

            if (mSheetData.isRowHidden(rowIndex)) {
                rowIndex++;
                continue;
            }

            top += getRowHeight(rowIndex);
            if (posY <= top) {
                mCurRowIndex = rowIndex;
                if ((top - posY) <= ConstVar.RESIZE_AREA) {
                    if(configure.isEnableResizeRow()) {
                        return ConstVar.HIT_ROWHEADER_RESIZE;
                    } else {
                        return ConstVar.HIT_ROWHEADER;
                    }
                } else {
                    return ConstVar.HIT_ROWHEADER;
                }
            } else {
                rowIndex++;
            }
        }
        //Log.i(TAG, "findRowByPosition row="+rowIndex);
        return ConstVar.HIT_NONE;
    }

    private int hitTestColumnHeader(int posX) {
        int left = mHeaderWidth;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int freezedColCount = mSheetData.getFreezedColCount();
            for (int i = 0; i < freezedColCount; i++) {
                if (mSheetData.isColumnHidden(i)) {
                    continue;
                }

                left += getColumnWidth(i);
                if (posX <= left) {
                    mCurColIndex = i;
                    if ((left - posX) <= ConstVar.RESIZE_AREA) {
                        if(configure.isEnableResizeColumn()) {
                            return ConstVar.HIT_COLUMNHEADER_RESIZE;
                        } else {
                            return ConstVar.HIT_COLUMNHEADER;
                        }
                    } else {
                        return ConstVar.HIT_COLUMNHEADER;
                    }
                }
            }
        }

        int colIndex = mFirstVisibleCol;
        left -= mColumnScrollX;

        while (true) {
            int maxColCount = mSheetData.getMaxColumnCount();
            if (colIndex >= maxColCount) {
                break;
            }

            if (mSheetData.isColumnHidden(colIndex)) {
                colIndex++;
                continue;
            }

            left += getColumnWidth(colIndex);
            if (posX <= left) {
                mCurColIndex = colIndex;
                if ((left - posX) <= ConstVar.RESIZE_AREA) {
                    if(configure.isEnableResizeColumn()) {
                        return ConstVar.HIT_COLUMNHEADER_RESIZE;
                    } else {
                        return ConstVar.HIT_COLUMNHEADER;
                    }
                } else {
                    return ConstVar.HIT_COLUMNHEADER;
                }
            } else {
                colIndex++;
            }
        }
        //Log.i(TAG, "findRowByPosition row="+rowIndex);
        return ConstVar.HIT_NONE;
    }

    private int hitTestTable(int x, int y) {
        hitTestColumnHeader(x);
        hitTestRowHeader(y);
        if(configure.isEnableSelection()) {
            if (mSelection != null && mSelection.isInRange(mCurRowIndex, mCurColIndex)) {
                return ConstVar.HIT_SELECTION;
            }
        }
        return ConstVar.HIT_TABLE;
    }

    private boolean resizeRow(MotionEvent event) {
        int action = event.getAction();
        float y = event.getRawY();

        final int minHeight = 15;// = tableView.getHighestText(mIndex);
        if (DEBUG) {
            Log.i(TAG, "minHeight = " + minHeight);
        }

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mY = y;
                mOldHeight = getRowHeight(mCurRowIndex);//model.getRowHeight(mIndex);
                mNewHeight = mOldHeight;
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mY;
                g_dy += dy;
                mY = y;
                int tempHeight = (int) (mOldHeight + g_dy + 0.5);
                if (DEBUG) {
                    Log.i(TAG, "(ACTION_MOVE). tempHeight = " + tempHeight + ".mNewHeight = " + mNewHeight);
                }
                if (mNewHeight > minHeight && tempHeight <= minHeight) {
                    if (DEBUG) {
                        Log.i(TAG, "move begain minus");
                    }
                    modifyRowHeightBy(mCurRowIndex, -(mNewHeight - minHeight));
                } else if (mNewHeight < minHeight && tempHeight >= minHeight) {
                    if (DEBUG) {
                        Log.i(TAG, "move begain plus");
                    }
                    modifyRowHeightBy(mCurRowIndex, (tempHeight - minHeight));
                } else if (tempHeight >= minHeight) {
                    if (DEBUG) {
                        Log.i(TAG, "move normally");
                    }
                    modifyRowHeightBy(mCurRowIndex, (int) dy);
                } else {
                    if (DEBUG) {
                        Log.i(TAG, "Don't move");
                    }
                }
                mNewHeight = tempHeight;
                break;
            case MotionEvent.ACTION_UP:
                mY = 0;
                g_dy = 0;
                mHitArea = ConstVar.HIT_NONE;
                break;
            default:
                mHitArea = ConstVar.HIT_NONE;
                break;
        }

        return true;
    }

    private boolean resizeColumn(MotionEvent event) {
        int action = event.getAction();
        float x = event.getRawX();

        final int minWidth = 22;

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mX = x;
                mOldWidth = getColumnWidth(mCurColIndex);//model.getColWidth(mIndex);
                mNewWidth = mOldWidth;
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = x - mX;
                g_dx += dx;
                mX = x;
                int tempWidth = (int) (mOldWidth + g_dx + 0.5);
                if (DEBUG) {
                    Log.i(TAG, "onTouchMoveColumn(ACTION_MOVE). tempWidth = " + tempWidth + ".mNewWidth = " + mNewWidth);
                }
                if (mNewWidth > minWidth && tempWidth <= minWidth) {
                    if (DEBUG) {
                        Log.i(TAG, "move begin minus");
                    }
                    modifyColumnWidthBy(mCurColIndex, -(mNewWidth - minWidth));
                } else if (mNewWidth < minWidth && tempWidth >= minWidth) {
                    if (DEBUG) {
                        Log.i(TAG, "move begin plus");
                    }
                    modifyColumnWidthBy(mCurColIndex, (tempWidth - minWidth));
                } else if (tempWidth >= minWidth) {
                    if (DEBUG) {
                        Log.i(TAG, "move normally");
                    }
                    modifyColumnWidthBy(mCurColIndex, (int) dx);
                } else {
                    if (DEBUG) {
                        Log.i(TAG, "Don't move");
                    }
                }
                mNewWidth = tempWidth;
                break;
            case MotionEvent.ACTION_UP:
                mX = 0;
                g_dx = 0;
                mHitArea = ConstVar.HIT_NONE;
                break;
            default:
                mX = 0;
                g_dx = 0;
                mHitArea = ConstVar.HIT_NONE;
                break;
        }

        return true;
    }

    private void modifyRowHeightBy(int rowIndex, int dy) {
        int newHeight = getRowHeight(rowIndex) + dy;
        if (mCacheRowHeights != null) {
            mCacheRowHeights[rowIndex] = newHeight;
        }
        mSheetData.setRowHeight(rowIndex, mUnitsConverter.getOriginValue(newHeight));
        clearCacheTableHeight();
    }

    private void modifyColumnWidthBy(int colIndex, int dx) {
        float width = getColumnWidth(colIndex) + dx;
        if (mCacheColumnWidths != null) {
            mCacheColumnWidths[colIndex] = Math.round(width);
        }
        width = mUnitsConverter.getOriginValue(width);
        int w = Math.round(width);
        mSheetData.setColumnWidth(colIndex, w);
        clearCacheTableWidth();
    }

    public void setEnableScroll(boolean canScroll) {
        mCanScroll = canScroll;
    }

    public void setConfigure(TableViewConfigure tableViewConfigure) {
        if(tableViewConfigure != null) {
            configure = tableViewConfigure;
            calcZoomedValues();
        }
    }

    private Rect getRectFromRange(Range range) {
        if (!checkRange(range)) {
            return null;
        }

        int left = getColumnPositionX(range.getLeft());
        int top = getRowPositionY(range.getTop());
        int right = getColumnPositionX(range.getRight()) + getColumnWidth(range.getRight());
        int bottom = getRowPositionY(range.getBottom()) + getRowHeight(range.getBottom());
        Rect rect = new Rect(left, top, right, bottom);

        if (DEBUG) {
            Log.i(TAG, "getRangeRect : " + rect.toShortString());
        }
        return rect;
    }

    public void selectCell(CellPosition cellPos) {
        if (cellPos != null) {
            selectCell(cellPos.getTop(), cellPos.getLeft());
        }
    }

    public void selectCell(int rowIndex, int colIndex) {
        if (rowIndex != -1 && colIndex != -1) {
            int left = colIndex;
            int top = rowIndex;
            int right = colIndex;
            int bottom = rowIndex;
            Range range = mSheetData.inMergedRange(rowIndex, colIndex, true);
            if (range != null) {
                left = range.getLeft();
                top = range.getTop();
                right = range.getRight();
                bottom = range.getBottom();
            }

            //Range newSelection = new Range(left, top, right, bottom);
            CellPosition newSelection = new CellPosition(top, left);
            // Set right row and bottom column here, because the cell may be merged.
            newSelection.setRight(right);
            newSelection.setBottom(bottom);
            if (!newSelection.equals(mSelection)) {
                clearSelection();
                mSelection = newSelection;
                mCurrentSelectedCell = new CellPosition(top, left);
//				Rect rect = getRangeRect(mSelection);
//				rect.set(rect.left, rect.top, rect.right+1, rect.bottom+1);
//				invalidate(rect);
                int startRow = newSelection.getTop();
                int startCol = newSelection.getLeft();
                if (isVisibleCell(startRow, startCol)) {
                    invalidateRange(mSelection);
                } else {
                    showStartFromCell(startRow, startCol);
                    invalidate();
                }
            }
        } else {
            mSelection = null;
        }
    }

    public Range getSelection() {
        return mSelection;
    }

    private void clearSelection() {
        if (mSelection != null) {
            invalidateRange(mSelection);
            mSelection = null;
        }
    }

    public CellPosition getCurrentCell() {
        return mCurrentSelectedCell;
    }

    private Range getVisibleRange() {
        int lastCol = findLastVisibleColumn();
        int lastRow = findLastVisibleRow();
        Range range = new Range(mFirstVisibleCol, mFirstVisibleRow, lastCol, lastRow);
        return range;
    }

    private Range getRangeFromRect(Rect rect) {
        if (rect == null)
            return null;

        int top = findRowByPosition(rect.top);
        int bottom = findRowByPosition(rect.bottom);
        int left = findColumnByPosition(rect.left);
        int right = findColumnByPosition(rect.right);
        Range range = new Range(left, top, right, bottom);
        if(DEBUG) {
            Log.i(TAG, "getRangeFromRect: " + range.toString());
        }
        return range;
    }


    /**
     * the cell will be display as the first visible cell in sheet
     *
     * @param rowIndex
     * @param colIndex
     */
    private void showStartFromCell(int rowIndex, int colIndex) {
        int cellRow = rowIndex;
        int cellCol = colIndex;

        int startRow = Math.min(cellRow, mFirstVisibleRow);
        int endRow = Math.max(cellRow, mFirstVisibleRow);
        int distanceY = 0;
        for (int i = startRow; i < endRow; i++) {
            distanceY += getRowHeight(i);
        }
        if (cellRow < mFirstVisibleRow) {
            distanceY = -distanceY;
        }
        distanceY -= mRowScrollY;

        int startCol = Math.min(cellCol, mFirstVisibleCol);
        int endCol = Math.max(cellCol, mFirstVisibleCol);
        int distanceX = 0;
        for (int j = startCol; j < endCol; j++) {
            distanceX += getColumnWidth(j);
        }
        if (cellCol < mFirstVisibleCol) {
            distanceX = -distanceX;
        }
        distanceX -= mColumnScrollX;

        if (false) {
            float velocityX = 1000;
            if (distanceX > 0) {
                velocityX = -velocityX;
            }
            float velocityY = 1000;
            if (distanceY > 0) {
                velocityY = -velocityY;
            }
            int initialX = velocityX < 0 ? Integer.MAX_VALUE : 0;
            int initialY = velocityY < 0 ? Integer.MAX_VALUE : 0;
            mLastFlingX = initialX;
            mLastFlingY = initialY;
            if (mScroller != null) {
                mScroller.fling(initialX, initialY, (int) velocityX, (int) velocityY,
                        0, Math.abs(distanceX), 0, Math.abs(distanceY));
            }
        } else {
            doScroll(distanceX, distanceY);
        }
    }

    private boolean isVisibleCell(int rowIndex, int colIndex) {
        boolean ret = false;
        int cellRow = rowIndex;
        int cellCol = colIndex;
        Range range = getVisibleRange();
        if (range.isInRange(cellRow, cellCol)) {
            ret = true;
        } else if (isInFreezeRow(cellRow)) {
            ret = true;
        } else if (isInFreezeColumn(cellCol)) {
            ret = true;
        }
        return ret;
    }

    private boolean isInFreezeRow(int rowIndex) {
        boolean ret = false;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int splitRow = mSheetData.getHorizontalSplitTopRow();
            if (splitRow > 0 && rowIndex < splitRow) {
                ret = true;
            }
        }

        return ret;
    }

    private boolean isInFreezeColumn(int colIndex) {
        boolean ret = false;
        boolean isFreeze = mSheetData.isFreeze();
        if (isFreeze) {
            int splitCol = mSheetData.getVerticalSplitLeftColumn();
            if (splitCol > 0 && colIndex < splitCol) {
                ret = true;
            }
        }

        return ret;
    }

    private boolean checkRange(Range range) {
        boolean ret = false;
        if (range != null) {
            if (0 <= range.getLeft() && range.getLeft() < mSheetData.getMaxColumnCount() &&
                    0 <= range.getRight() && range.getRight() < mSheetData.getMaxColumnCount() &&
                    0 <= range.getTop() && range.getTop() < mSheetData.getMaxRowCount() &&
                    0 <= range.getBottom() && range.getBottom() < mSheetData.getMaxRowCount()) {
                ret = true;
            }
        }

        return ret;
    }

    public int getRowHeight(int rowIndex) {
        if (mCacheRowHeights == null) {
            int maxRowCount = mSheetData.getMaxRowCount();
            mCacheRowHeights = new int[maxRowCount];
            for (int i = 0; i < maxRowCount; i++) {
                mCacheRowHeights[i] = mUnitsConverter.getZoomedValue(mSheetData.getRowHeight(i));
            }
        }

        int rowHeight = 0;
        if(!mSheetData.isRowHidden(rowIndex)) {
            rowHeight = mCacheRowHeights[rowIndex];
        }
        return rowHeight;
    }

    public int getColumnWidth(int colIndex) {
        if (mCacheColumnWidths == null) {
            int maxColumnCount = mSheetData.getMaxColumnCount();
            mCacheColumnWidths = new int[maxColumnCount];
            for (int j = 0; j < maxColumnCount; j++) {
                mCacheColumnWidths[j] = Math.round(mUnitsConverter.getZoomedValue(mSheetData.getColumnWidth(j)));
            }
        }

        int colWidth = 0;
        if(!mSheetData.isColumnHidden(colIndex)) {
            colWidth = mCacheColumnWidths[colIndex];
        }
        return colWidth;
    }

    private int getTableHeight() {
        if(mCacheTableHeight == -1) {
            int height = 0;
            for (int i = 0; i < mSheetData.getMaxRowCount(); i++) {
                height += getRowHeight(i);
            }
            mCacheTableHeight = height;
        }
        return mCacheTableHeight;
    }

    private int getTableWidth() {
        if(mCacheTableWidth == -1) {
            int width = 0;
            for (int i = 0; i < mSheetData.getMaxColumnCount(); i++) {
                width += getColumnWidth(i);
            }
            mCacheTableWidth = width;
        }
        return mCacheTableWidth;
    }

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float zoom = mUnitsConverter.getZoom();
        zoom *= detector.getScaleFactor();
        zoom = ExcelUtils.bounds(ConstVar.ZOOMOUTMIN, zoom, ConstVar.ZOOMINMAX);
        setZoom(zoom);
        return true;
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        mInScaleGesture = true;
        return true;
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

        mInScaleGesture = false;
    }

    public Bitmap getThumbnail(int w, int h) {
        float curZoom = getZoom();

        float zoomValue = getFitHeightZoom(h);
        if (zoomValue > 60f) {
            zoomValue = 60f;
        }
        setZoom(zoomValue);

        this.clearFocus();
        this.setPressed(false);
        boolean willNotCache = this.willNotCacheDrawing();
        this.setWillNotCacheDrawing(false);
        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = this.getDrawingCacheBackgroundColor();
        this.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            this.destroyDrawingCache();
        }
        this.buildDrawingCache();
        Bitmap cacheBitmap = this.getDrawingCache();
        if (cacheBitmap == null) {
            // Log.e("", "failed getViewBitmap(" + view + ")", new
            // RuntimeException());
            return null;
        }

        Bitmap bitmap;
        if (false) {
            float height = cacheBitmap.getHeight();
            float width = cacheBitmap.getWidth();
            float desiredScaleX = w / width;
            float desiredScaleY = h / height;
            Matrix matrix = new Matrix();
            matrix.postScale(desiredScaleX, desiredScaleY);
            bitmap = Bitmap.createBitmap(cacheBitmap, 0, 0, (int) width, (int) height, matrix, true);
        } else {
            bitmap = Bitmap.createScaledBitmap(cacheBitmap, w, h, true);
        }
//		if(false) {
//			String name = mWorkbook.getSheetName(mWorkbook.getActiveSheetIndex());
//			savePic(cacheBitmap, "/sdcard/pic/"+name+"_orig.png");
//			savePic(bitmap, "/sdcard/pic/"+name+"_scale.png");
//		}
        // Restore the view
        this.destroyDrawingCache();
        this.setWillNotCacheDrawing(willNotCache);
        this.setDrawingCacheBackgroundColor(color);
        setZoom(curZoom);

        return bitmap;
    }

    public int getHeaderWidth() {
        return mHeaderWidth;
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public int getCellWidth(int rowId, int colId, boolean includeMerged) {
        int width = 0;
        if (includeMerged) {
            Range range = mSheetData.inMergedRange(rowId, colId, true);
            if (range != null) {
                for (int i = range.getLeft(); i <= range.getRight(); i++) {
                    width += getColumnWidth(i);
                }
            } else {
                width = getColumnWidth(colId);
            }
        } else {
            width = getColumnWidth(colId);
        }
        return width;
    }

    public int getCellHeight(int rowId, int colId, boolean includeMerged) {
        int height = 0;
        if (includeMerged) {
            Range range = mSheetData.inMergedRange(rowId, colId, true);
            if (range != null) {
                for (int i = range.getTop(); i <= range.getBottom(); i++) {
                    height += getRowHeight(i);
                }
            } else {
                height = getRowHeight(rowId);
            }
        } else {
            height = getRowHeight(rowId);
        }
        return height;
    }

    public void refreshUI() {
        clearCacheData();
        requestLayout();
    }

    public void clearCacheData() {
        mCacheRowHeights = null;
        mCacheColumnWidths = null;
        mFreezePositionX = -1;
        mFreezePositionY = -1;
        mLastVisibleRow = -1;
        mLastVisibleCol = -1;
        clearCacheTableHeight();
        clearCacheTableWidth();
        invalidate();
    }

    private void clearCacheTableHeight() {
        mCacheTableHeight = -1;
    }

    private void clearCacheTableWidth() {
        mCacheTableWidth = -1;
    }

    public static void savePic(Bitmap b, String strFileName) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(strFileName);
            if (null != fos) {
                b.compress(Bitmap.CompressFormat.PNG, 90, fos);
                fos.flush();
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
