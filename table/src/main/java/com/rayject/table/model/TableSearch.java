package com.rayject.table.model;

import android.util.Log;

public class TableSearch {
    public static final String TAG = "TableSearch";
    private static final boolean DEBUG = false;

    public TableSearch() {

    }

    public static class SearchResult {
        public String searchString;
        public int rowId;
        public int colId;
        public int startOffset = 0;
//		public int endOffset = -1;

        public SearchResult(String str, int rowId, int colId, int start) {
            searchString = str;
            this.rowId = rowId;
            this.colId = colId;
            startOffset = start;
        }
    }

    public static SearchResult SearchNext(ITable table, SearchResult searchOpt) {
        if (table == null || table.getSheet() == null) {
            return null;
        }

        ICellData cellData;
        SearchResult result = null;
        int index = -1;
        int rowId = searchOpt.rowId;
        int colId = searchOpt.colId;
        String stringData = searchOpt.searchString;
        ISheetData sheetData = table.getSheet();
        int lastRowIndex = sheetData.getLastRow();
        int offset = searchOpt.startOffset;
        for (int i = rowId; i <= lastRowIndex; i++) {
            int j = 0;
            if (rowId == i) {
                j = colId;
            }
            int lastColIndex = sheetData.getLastColumn(i);
            for (; j <= lastColIndex; j++) {
                cellData = sheetData.getCellData(i, j);
                if (cellData != null) {
                    index = searchCell(stringData, offset, cellData);
                    if (index != -1) {
                        result = new SearchResult(stringData.toString(), i, j, index);
                        return result;
//						mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_SEARCHRESULT, new CellPosition(i, j)));
//						return;
                    }
                    offset = 0;
                }
            }
        }

        return result;
    }

    public static SearchResult SearchPrevious(ITable table, SearchResult searchOpt) {
        if (table == null || table.getSheet() == null) {
            return null;
        }

        ICellData cellData;
        SearchResult result = null;
        int index = -1;
        int rowId = searchOpt.rowId;
        int colId = searchOpt.colId;
        String stringData = searchOpt.searchString;
        ISheetData sheetData = table.getSheet();
        if (rowId == -1) {
            rowId = sheetData.getLastRow();
        }
        if (colId == -1) {
            colId = sheetData.getLastColumn(rowId);
        }
        int offset = searchOpt.startOffset;

        if (DEBUG) {
            Log.i(TAG, "startRow=" + rowId + ", startCol=" + colId);
        }

        for (int i = rowId; i >= 0; i--) {
            int lastColIndex = sheetData.getLastColumn(i);
            int j = lastColIndex;
            if (rowId == i) {
                j = colId;
            }
            for (; j >= 0; j--) {
                if (DEBUG) {
                    Log.i(TAG, "getcellData:" + i + ", " + j);
                }
                cellData = sheetData.getCellData(i, j);
                if (cellData != null) {
                    if (offset == Integer.MAX_VALUE) {
                        CharSequence text = cellData.getTextValue();
                        if (text != null) {
                            offset = text.length();
                        } else {
                            offset = 0;
                        }
                    }
                    index = searchCellPrevious(stringData, offset, cellData);
                    if (index != -1) {
                        result = new SearchResult(stringData.toString(), i, j, index);
                        return result;
//						mUiHandler.sendMessage(mUiHandler.obtainMessage(MSG_SEARCHRESULT, new CellPosition(i, j)));
//						return;
                    }
                    offset = Integer.MAX_VALUE;
                }
            }
        }
        return result;
    }

    private static int searchCell(String stringData, int start, ICellData cellData) {
        CharSequence text = cellData.getTextValue();
        int index = -1;
        if (text != null) {
            index = text.toString().toLowerCase().indexOf(stringData.toLowerCase(), start);
        }
        return index;
    }

    private static int searchCellPrevious(String stringData, int start, ICellData cellData) {
        CharSequence text = cellData.getTextValue();
        int index = -1;
        if (text != null) {
            index = text.toString().toLowerCase().lastIndexOf(stringData.toLowerCase(), start);
        }
        return index;
    }
}
