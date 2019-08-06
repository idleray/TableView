package com.rayject.table.model;

import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Region;
import android.os.Build;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

import com.rayject.table.model.action.Action;
import com.rayject.table.model.object.CellObject;
import com.rayject.table.model.style.BorderLineStyle;
import com.rayject.table.model.style.CellStyle;
import com.rayject.table.model.style.Font;
import com.rayject.table.model.style.TableConst;
import com.rayject.table.util.AligmentUtils;
import com.rayject.table.util.Colors;
import com.rayject.table.util.ConstVar;
import com.rayject.table.util.SpannableUtils;
import com.rayject.table.util.UnitsConverter;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseCellData implements ICellData {
    public final static String TAG = "BaseCellData";
    private final static boolean DEBUG = false;
    protected Layout mLayout;
    protected TextPaint mPaint;
    protected CharSequence mText;
    protected Range mMergedRange;
    protected int mCellStyleId;
    protected CellStyle mCellStyle;
    protected boolean mIsExcel = true;

    protected Path mHighlightPath;
    protected Paint mHighlightPaint;
    protected Selection mSelection;
    private int mTextLeftPadding = 6, mTextRightPadding = 6;
    private ISheetData mSheet;
    private IRichText richText;
    private List<CellObject> objects;
    private Action action;

    public BaseCellData(ISheetData sheet) {
        mSheet = sheet;

        mPaint = new TextPaint();
        mPaint.setColor(android.graphics.Color.BLACK);
        mPaint.linkColor = android.graphics.Color.BLUE;
        mPaint.setAntiAlias(true);
        mPaint.setSubpixelText(true);

        mHighlightPath = new Path();

        mHighlightPaint = new Paint();
        int mHighlightColor = Colors.SELCTION_COLOR;
        mHighlightPaint.setColor(mHighlightColor);
        mHighlightPaint.setStyle(Style.FILL);

        setStyleIndex(mSheet.getSheetStyleIndex());

        objects = new ArrayList<>();
    }

    @Override
    public void draw(Canvas canvas, Paint paint, Rect rect, int drawType, UnitsConverter uc) {
        if (DEBUG) {
            Rect bounds = canvas.getClipBounds();
//			Log.i(TAG, "ClipBounds:");
//			Log.i(TAG, "left="+bounds.left+", top="+bounds.top+", right="+bounds.right+", bottom="+bounds.bottom);
//			Log.i(TAG, "Cell Rect:");
//			Log.i(TAG, "left="+rect.left+", top="+rect.top+", right="+rect.right+", bottom="+rect.bottom);
        }
        if ((drawType & ConstVar.DRAWCELL_BG) == ConstVar.DRAWCELL_BG) {
            drawBackground(canvas, paint, rect);

        }

        if ((drawType & ConstVar.DRAWCELL_TEXT) == ConstVar.DRAWCELL_TEXT) {
            drawText(canvas, rect);
        }

        if ((drawType & ConstVar.DRAWCELL_BORDER) == ConstVar.DRAWCELL_BORDER) {
            drawBorderline(canvas, paint, rect, uc);
        }

        if ((drawType & ConstVar.DRAWCELL_OBJECT) == ConstVar.DRAWCELL_OBJECT) {
            drawObject(canvas, rect);
        }
    }

    private void drawBorderline(Canvas canvas, Paint paint, Rect rect, UnitsConverter uc) {
        if (mCellStyle == null)
            return;

        //Expand clip to draw border
        Rect clipBounds = canvas.getClipBounds();
        clipBounds.inset(-10, -10);

        int saveCount = canvas.save();
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            canvas.clipRect(clipBounds, Region.Op.REPLACE);
        } else {
            //TODO: Maybe we should expand the parent clip rect
        }
        //int offsetH = 0;
        //int offsetV = 0;
        int innerOffset = 2;
        if (uc != null) {
            innerOffset = uc.getZoomedValue(innerOffset);
        }

        Path path = new Path();
        BorderLineStyle borderline = mCellStyle.getBorderLineStyle(TableConst.LEFTBORDERLINE);
        if (borderline != null && borderline.getType() != BorderLineStyle.BORDER_NONE) {
            setBorderlinePaint(borderline, paint, uc);
            path.reset();
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.left, rect.bottom);
            canvas.drawPath(path, paint);
            if (borderline.getType() == BorderLineStyle.BORDER_DOUBLE) {
                int lineSpace1, lineSpace2;
                int lineStart = (int) (paint.getStrokeWidth() + innerOffset);
                int space = lineStart;
                space -= paint.getStrokeWidth() / 2;
                lineSpace1 = Math.max(2, space);
                lineSpace2 = Math.max(1, space);
                path.reset();
                path.moveTo(rect.left + lineStart, rect.top + lineSpace1);
                path.lineTo(rect.left + lineStart, rect.bottom - lineSpace2);
                canvas.drawPath(path, paint);
            }

        }

        borderline = mCellStyle.getBorderLineStyle(TableConst.TOPBORDERLINE);
        if (borderline != null && borderline.getType() != BorderLineStyle.BORDER_NONE) {
            setBorderlinePaint(borderline, paint, uc);
            path.reset();
            path.moveTo(rect.left, rect.top);
            path.lineTo(rect.right, rect.top);
            canvas.drawPath(path, paint);
            if (borderline.getType() == BorderLineStyle.BORDER_DOUBLE) {
                int lineSpace1, lineSpace2;
                int lineStart = (int) (paint.getStrokeWidth() + innerOffset);
                int space = lineStart;
                space -= paint.getStrokeWidth() / 2;
                lineSpace1 = Math.max(2, space);
                lineSpace2 = Math.max(1, space);
                path.reset();
                path.moveTo(rect.left + lineSpace1, rect.top + lineStart);
                path.lineTo(rect.right - lineSpace2, rect.top + lineStart);
                canvas.drawPath(path, paint);
            }
        }

        borderline = mCellStyle.getBorderLineStyle(TableConst.RIGHTBORDERLINE);
        if (borderline != null && borderline.getType() != BorderLineStyle.BORDER_NONE) {
            setBorderlinePaint(borderline, paint, uc);
            path.reset();
            path.moveTo(rect.right, rect.top);
            path.lineTo(rect.right, rect.bottom);
            canvas.drawPath(path, paint);
            if (borderline.getType() == BorderLineStyle.BORDER_DOUBLE) {
                int lineSpace1, lineSpace2;
                int lineStart = (int) (paint.getStrokeWidth() + innerOffset);
                int space = lineStart;
                space -= paint.getStrokeWidth() / 2;
                lineSpace1 = Math.max(2, space);
                lineSpace2 = Math.max(1, space);
                path.reset();
                path.moveTo(rect.right - lineStart, rect.top + lineSpace1);
                path.lineTo(rect.right - lineStart, rect.bottom - lineSpace2);
                canvas.drawPath(path, paint);
            }
        }

        borderline = mCellStyle.getBorderLineStyle(TableConst.BOTTOMBORDERLINE);
        if (borderline != null && borderline.getType() != BorderLineStyle.BORDER_NONE) {
            setBorderlinePaint(borderline, paint, uc);
            path.reset();
            path.moveTo(rect.left, rect.bottom);
            path.lineTo(rect.right, rect.bottom);
            canvas.drawPath(path, paint);
            if (borderline.getType() == BorderLineStyle.BORDER_DOUBLE) {
                int lineSpace1, lineSpace2;
                int lineStart = (int) (paint.getStrokeWidth() + innerOffset);
                int space = lineStart;
                space -= paint.getStrokeWidth() / 2;
                lineSpace1 = Math.max(2, space);
                lineSpace2 = Math.max(1, space);
                path.reset();
                path.moveTo(rect.left + lineSpace1, rect.bottom - lineStart);
                path.lineTo(rect.right - lineSpace2, rect.bottom - lineStart);
                canvas.drawPath(path, paint);
            }
        }

        canvas.restoreToCount(saveCount);
    }

    private void setBorderlinePaint(BorderLineStyle borderlineStyle, Paint paint, UnitsConverter uc) {
        float DASHLENGTH = 9f;
        float DOTLENGTH = 3f;
        float SPACELENGTH = 3f;
        if (uc != null) {
            DASHLENGTH = uc.getZoomedValue(DASHLENGTH);
            DOTLENGTH = uc.getZoomedValue(DOTLENGTH);
            SPACELENGTH = uc.getZoomedValue(SPACELENGTH);
        }
        PathEffect pe = null;

        int type = borderlineStyle.getType();
        int color = borderlineStyle.getColor();
        float lineWidth = borderlineStyle.getWidth();

        switch (type) {
            case BorderLineStyle.BORDER_DASH:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DOT:
                pe = new DashPathEffect(new float[]{DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DASH_DOT:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_DASH_DOT_DOT:
                pe = new DashPathEffect(new float[]{DASHLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH, DOTLENGTH, SPACELENGTH}, 0);
                break;
            case BorderLineStyle.BORDER_HAIRLINE:
                lineWidth = 0;
                break;
            default:
                break;
        }

        if (uc != null) {
            lineWidth = uc.getZoomedValue(lineWidth);
        }
        paint.reset();
        paint.setStyle(Style.STROKE);
        paint.setStrokeWidth(lineWidth);
        paint.setColor(color);
        paint.setPathEffect(pe);
        paint.setDither(true);
    }

    private void drawBackground(Canvas canvas, Paint paint, Rect rect) {
        if (mCellStyle == null)
            return;

        int bgC = mCellStyle.getBgColor();
        if (mIsExcel && bgC == 0 && isMerged()) {
            bgC = 0xFFFFFFFF;
            rect.set(rect.left + 1, rect.top + 1, rect.right, rect.bottom);
        } else {
            //TODO: why rect.right + 1 and rect.bottom + 1?
//            rect.set(rect.left, rect.top, rect.right + 1, rect.bottom + 1);
            rect.set(rect.left + 1, rect.top + 1, rect.right, rect.bottom);
        }

        setBGPaint(paint, bgC);

        canvas.drawRect(rect, paint);
    }

    private void setBGPaint(Paint paint, int color) {
        paint.reset();
        paint.setStyle(Style.FILL);
        paint.setColor(color);
    }

    private void drawText(Canvas canvas, Rect rect) {
        canvas.save();
        canvas.translate(rect.left, rect.top);

        int offsetH = 0, offsetV = 0;
        Layout layout = getLayout(rect.width());
        int height = layout.getHeight();
        //CellStyle cellStyle = mCell.getCellStyle();
        int vAlignment = (mCellStyle == null ? TableConst.VERTICAL_ALIGNMENT_CENTRE : mCellStyle.getVerticalAlignment());
        switch (vAlignment) {
            case TableConst.VERTICAL_ALIGNMENT_TOP:
                break;
            case TableConst.VERTICAL_ALIGNMENT_CENTRE:
                offsetV = (rect.height() - height) / 2;
                break;
            case TableConst.VERTICAL_ALIGNMENT_BOTTOM:
                offsetV = rect.height() - height;
                break;
            case TableConst.VERTICAL_ALIGNMENT_JUSTIFY:
                break;
            default:
                break;
        }

        boolean isTextWrap = getWrapText();
        Layout.Alignment align = layout.getAlignment();
        if (!isTextWrap) {
            int textWidth = layout.getWidth();
            int cellW = rect.width();
            if (textWidth > cellW) {
                if (align == Layout.Alignment.ALIGN_NORMAL) {
                    offsetH = mTextLeftPadding;
                } else if (align == Layout.Alignment.ALIGN_CENTER) {
                    offsetH = (cellW - textWidth) / 2;
                } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                    offsetH = cellW - textWidth - mTextRightPadding;
                }
            } else {
                if (align == Layout.Alignment.ALIGN_NORMAL) {
                    offsetH = mTextLeftPadding;
                } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                    offsetH = -mTextRightPadding;
                }
            }
        } else {
            if (align == Layout.Alignment.ALIGN_NORMAL) {
                offsetH = mTextLeftPadding;
            } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                offsetH = -mTextRightPadding;
            }
        }
        int indention = (mCellStyle == null ? 0 : mCellStyle.getIndention());
        if (indention > 0) {
            offsetH = indention;
        }

        if (offsetV < 0) {
            offsetV = 0;
        }

        canvas.translate(offsetH, offsetV);

        if (DEBUG) {
            Rect bounds = canvas.getClipBounds();
            Log.i(TAG, "drawText: ");
            Log.i(TAG, "bounds:" + bounds.toString());
            Log.i(TAG, "rect:" + rect.toString());
        }

        mHighlightPath.reset();
        if (mSelection != null) {
            int selStart = mSelection.getStartInCell();
            int selEnd = mSelection.getEndInCell();
            mLayout.getSelectionPath(selStart, selEnd, mHighlightPath);
        }

        int cursorOffsetVertical = 0;
        mLayout.draw(canvas, mHighlightPath, mHighlightPaint, cursorOffsetVertical);

        canvas.restore();
    }

    private void drawObject(Canvas canvas, Rect rect) {
        int count = canvas.save();
        canvas.translate(rect.left, rect.top);
        for(CellObject dObject : objects) {
            canvas.save();
            Point point = CellObject.getPositionInRect(dObject, rect.width(), rect.height());
            int left = point.x;
            int top = point.y;

            canvas.translate(left, top);
            dObject.draw(canvas);

            canvas.restore();
        }

        canvas.restoreToCount(count);
    }

    @Override
    public Range getMergedRange() {
        return mMergedRange;
    }

    @Override
    public CharSequence getTextValue() {
        return mText;
    }

    @Override
    public IRichText getRichTextValue() {
        return richText;
    }

//    @Override
//    public boolean getWrapText() {
//        return true;
//    }

    @Override
    public boolean isMerged() {
        return (mMergedRange != null);
    }

    @Override
    public void setMergedRange(Range range) {
        mMergedRange = range;
    }

    @Override
    public void setCellValue(IRichText richText) {
        this.richText = richText;
        mText = buildSpannableText(richText);
    }

    @Override
    public void update() {
        mLayout = null;
    }

    protected Layout getLayout(int layoutWidth) {
        if (mLayout == null) {
            mLayout = createLayout(layoutWidth);
        }
        return mLayout;
    }

    protected Layout createLayout(int layoutWidth) {
        int desireW = Math.round(Layout.getDesiredWidth(mText, mPaint));
        boolean isTextWrap = getWrapText();
        int width;
        if (isTextWrap) {
            width = layoutWidth;
            width -= (mTextLeftPadding + mTextRightPadding);
        } else {
            width = Math.max(desireW, layoutWidth);
        }

        Layout.Alignment alignment = getAlignment();

        int indention = (mCellStyle == null ? 0 : mCellStyle.getIndention());
        width -= indention;

        Layout layout = new StaticLayout(mText, mPaint, width, alignment, 1.0f, 0.0f, false);

        return layout;
    }

    protected Layout.Alignment getAlignment() {
        Layout.Alignment alignment = AligmentUtils.getAligment(mCellStyle.getAlignment());
        int indention = (mCellStyle == null ? 0 : mCellStyle.getIndention());
        if (indention > 0) {
            alignment = Layout.Alignment.ALIGN_NORMAL;
        }

        return alignment;
    }

    @Override
    public void setStyleIndex(int id) {
        mCellStyleId = id;

        mCellStyle = mSheet.getCellStyleManager().getCellStyle(id);

        updateTextPaint();
    }

    private void updateTextPaint() {
        Font font = mSheet.getFontManager().getFont(mCellStyle.getFontIndex());
        mPaint.setColor(font.getColor());
        mPaint.setTextSize(font.getFontSize());

        clearLayout();
    }

    @Override
    public int getStyleIndex() {
        return mCellStyleId;
    }

    public Layout getLayout() {
        return mLayout;
    }

    public void clearLayout() {
        mLayout = null;
    }

    public void setSelection(Selection s) {
        mSelection = s;
    }

    public int getPositionXInCell(int charOffset) {
        int x = 0;
        Layout layout = getLayout();
        if (layout != null) {
            x = (int) layout.getPrimaryHorizontal(charOffset);
        }
        x += mTextLeftPadding;
        return x;
    }

    private boolean getWrapText() {
        return getCellStyle().isAutoWrap();
    }

    public CellStyle getCellStyle() {
        if(mCellStyle == null) {
            mCellStyle = mSheet.getCellStyleManager().getCellStyle(mCellStyleId);
        }

        return mCellStyle;
    }

    @Override
    public ISheetData getSheet() {
        return mSheet;
    }

    @Override
    public void addObject(CellObject d) {
        objects.add(d);
    }

    @Override
    public void removeObject(CellObject d) {
        objects.remove(d);
    }

    @Override
    public int getObjectCount() {
        return objects.size();
    }

    @Override
    public CellObject getObject(int index) {
        return objects.get(index);
    }

    @Override
    public void setAction(Action action) {
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public int calcTextHeightByWidth(int width) {
        Layout layout = getLayout();
        if(layout == null) {
            layout = createLayout(width);
        }

        return layout.getHeight();
    }

    private CharSequence buildSpannableText(IRichText richText) {
        if(richText == null || richText.getText() == null) {
            return "";
        }
        SpannableStringBuilder sb = new SpannableStringBuilder(richText.getText());
        int length = sb.length();
        if(length > 0) {
            Font font = getSheet().getFontManager().getFont(getCellStyle().getFontIndex());
            SpannableUtils.convertFont(font, 0, length, sb);
        }
        int count = richText.getRunCount();
        for(int i = 0; i < count; i++) {
            ITextRun run = richText.getRun(i);
            int start = run.getStartPos();
            int end = start + run.getLength();
            Font font = getSheet().getFontManager().getFont(run.getFontIndex());
            SpannableUtils.convertFont(font, start, end, sb);

            SpannableUtils.convertBackground(run.getBackgroundColor(), start, end, sb);
            SpannableUtils.convertAction(this, run.getAction(), start, end, sb);
        }

        return sb;
    }
}
