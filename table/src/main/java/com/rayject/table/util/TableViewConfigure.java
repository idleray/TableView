package com.rayject.table.util;


public class TableViewConfigure {
    private boolean showHeaders;
    private boolean showFreezeLines;
    private boolean enableSelection;
    private boolean enableResizeRow;
    private boolean enableResizeColumn;
    private boolean enableZoom;

    public boolean isShowHeaders() {
        return showHeaders;
    }

    public void setShowHeaders(boolean showHeaders) {
        this.showHeaders = showHeaders;
    }

    public boolean isShowFreezeLines() {
        return showFreezeLines;
    }

    public void setShowFreezeLines(boolean showFreezeLines) {
        this.showFreezeLines = showFreezeLines;
    }

    public boolean isEnableSelection() {
        return enableSelection;
    }

    public void setEnableSelection(boolean enableSelection) {
        this.enableSelection = enableSelection;
    }

    public boolean isEnableResizeRow() {
        return enableResizeRow;
    }

    public void setEnableResizeRow(boolean enableResizeRow) {
        this.enableResizeRow = enableResizeRow;
    }

    public boolean isEnableResizeColumn() {
        return enableResizeColumn;
    }

    public void setEnableResizeColumn(boolean enableResizeColumn) {
        this.enableResizeColumn = enableResizeColumn;
    }

    public boolean isEnableZoom() {
        return enableZoom;
    }

//    public void setEnableZoom(boolean enableZoom) {
//        this.enableZoom = enableZoom;
//    }
}
